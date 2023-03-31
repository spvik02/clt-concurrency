import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ServerTest {
    private Client client;
    private Server server;

    @BeforeEach
    void setUp() {
        server = new Server();
        client = new Client(server, 100, 10);
    }

    @Test
    void checkServerListShouldContainsAllExpected(){
        int length = 100;
        List<Integer> expectedList = IntStream.rangeClosed(1, length).boxed().toList();

        try {
            client.removeAllFromList();
            client.shutdownExecutorWithAwait(1);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertThat(server.getList()).containsExactlyInAnyOrderElementsOf(expectedList);
    }

    @Test
    void checkResultServerListSizeShouldBeEqualToClientListStartSize(){
        try {
            client.removeAllFromList();
            client.shutdownExecutorWithAwait(1);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertThat(server.getList()).size().isEqualTo(100);
    }
}
