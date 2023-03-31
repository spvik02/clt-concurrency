import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class Client {

    private List<Integer> list;
    private AtomicInteger accumulator = new AtomicInteger();
    private Server server;
    private ExecutorService executor;
    private final Lock lock = new ReentrantLock();

    /**
     * Constructs Client with passed server, generate list with passed length and set number of thread for ThreadPool
     * for executor
     *
     * @param server
     * @param length   length of the generated list
     * @param nThreads number of thread for ThreadPool
     */
    public Client(Server server, int length, int nThreads) {
        this.server = server;
        this.list = new ArrayList<>(IntStream.rangeClosed(1, length).boxed().toList());
        this.executor = Executors.newFixedThreadPool(nThreads);
    }

    public AtomicInteger getAccumulator() {
        return accumulator;
    }

    public List<Integer> getList() {
        return list;
    }

    /**
     * Selects a random item, removes it from the list and send Request with removed value to server.
     * Result of Callable is Response from the server.
     */
    public Callable<Response> deleteRandomElement = () -> {
        int value;

        try {
            lock.lock();
            int randomIdx = ThreadLocalRandom.current().nextInt(0, list.size());
            value = list.remove(randomIdx);
        } finally {
            lock.unlock();
        }
        return server.processRequest(new Request(value));
    };

    /**
     * Returns runnable that adds value from future response to accumulator.
     *
     * @param future future response from the server
     * @return runnable
     */
    private Runnable accumulate(Future<Response> future) {
        return () -> {
            try {
                accumulator.addAndGet(future.get().getValue());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Deletes all elements from list and calculate accumulator based on the server responses.
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void removeAllFromList() throws ExecutionException, InterruptedException {
        int length = list.size();

        for (int i = 0; i < length; i++) {
            Future<Response> future = executor.submit(deleteRandomElement);
            executor.submit(accumulate(future));
        }
    }

    /**
     * Initiates an orderly shutdown in which previously submitted tasks are executed, but no new tasks will be
     * accepted. Waits for previously submitted tasks to complete execution, or the timeout occurs, or the current
     * thread is interrupted, whichever happens first.
     *
     * @param timeout timeout in minutes
     * @return true if this executor terminated and false if the timeout elapsed before termination
     * @throws InterruptedException
     */
    public boolean shutdownExecutorWithAwait(long timeout) throws InterruptedException {
        executor.shutdown();
        return executor.awaitTermination(timeout, TimeUnit.MINUTES);
    }
}
