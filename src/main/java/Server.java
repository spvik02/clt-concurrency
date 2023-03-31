import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
    List<Integer> list;
    private final Lock lock = new ReentrantLock();

    public Server() {
        this.list = new ArrayList<>();
    }

    public List<Integer> getList() {
        return list;
    }

    /**
     * Adds value from request to the list, awaits random amount of time in range 100-1000ms and returns list size
     * after adding. Uses a lock during adding
     *
     * @param request request passed from client
     * @return Response containing the size of the list after adding value
     * @throws InterruptedException
     */
    public Response processRequest(Request request) throws InterruptedException {
        int randomMs = ThreadLocalRandom.current().nextInt(100, 1000);
        int size;
        try {
            lock.lock();
            list.add(request.getValue());
            size = list.size();
        } finally {
            lock.unlock();
        }
        Thread.sleep(randomMs);
        return new Response(size);
    }
}
