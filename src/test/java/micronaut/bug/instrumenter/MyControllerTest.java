package micronaut.bug.instrumenter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@MicronautTest
class MyControllerTest {

    @Inject
    EmbeddedServer server;

    @Inject
    @Client("/")
    RxHttpClient client;

    @Test
    void testExecutorService() {
        var response = client.toBlocking().exchange(HttpRequest.GET("/executor-service"), String.class).body();
        assertEquals("123", response);
    }

    @Test
    void testThreadFactory() {
        var response = client.toBlocking().exchange(HttpRequest.GET("/thread-factory"), String.class).body();
        assertEquals("123", response);
    }
}
