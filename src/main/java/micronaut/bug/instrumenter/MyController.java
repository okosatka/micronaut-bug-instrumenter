package micronaut.bug.instrumenter;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import org.apache.logging.log4j.ThreadContext;
import org.awaitility.Awaitility;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;

@Controller
public class MyController {

    public static final String CORRELATION_ID = "correlation-id";
    public static final String VALUE = "123";

    @Inject @Named(TaskExecutors.IO) ExecutorService executorService;
    @Inject ThreadFactory threadFactory;


    /**
     * Instruments a task and gives us an expected correlation id.
     *
     * @return 123 from passed from parent context as expected
     */
    @Get(uri = "/executor-service", produces = MediaType.TEXT_PLAIN)
    public String executorService() throws ExecutionException, InterruptedException {
        ThreadContext.put(CORRELATION_ID, "123");
        return executorService.submit(() -> ThreadContext.get(CORRELATION_ID)).get();
    }

    /**
     * Does not instrument a runnable and give us wrong context
     * I would expect 123 as a response.
     *
     * @return 321, because runnables are not instrumented
     */
    @Get(uri = "/thread-factory", produces = MediaType.TEXT_PLAIN)
    public String threadFactory() throws InterruptedException {
        ThreadContext.put(CORRELATION_ID, VALUE);
        var latch = new CountDownLatch(1);
        var runnable = new MyRunnable(latch);
        var t = threadFactory.newThread(runnable);
        t.start();
        if (latch.await(1, TimeUnit.SECONDS)) {
            return VALUE;
        }
        return "WRONG";
    }

    static class MyRunnable implements Runnable {
        private final CountDownLatch latch;

        public MyRunnable(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            if (VALUE.equals(ThreadContext.get(CORRELATION_ID))) {
                latch.countDown();
            }
        }
    }
}
