import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ClientTest {
    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client(new Server(), 100, 10);
    }

    @Test
    void checkClientListSizeShouldBeZero(){
        try {
            client.removeAllFromList();
            client.shutdownExecutorWithAwait(1);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertThat(client.getList()).size().isEqualTo(0);
    }

    @Test
    void checkAccumulatorShouldBeEqualToExpected(){
        int expectedAccumulator = 5050;

        try {
            client.removeAllFromList();
            client.shutdownExecutorWithAwait(1);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertThat(client.getAccumulator().get()).isEqualTo(expectedAccumulator);
    }
}
