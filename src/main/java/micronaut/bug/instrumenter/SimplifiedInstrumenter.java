package micronaut.bug.instrumenter;

import io.micronaut.scheduling.instrument.InvocationInstrumenter;
import io.micronaut.scheduling.instrument.InvocationInstrumenterFactory;
import org.apache.logging.log4j.ThreadContext;

import java.util.Map;
import javax.annotation.Nullable;
import javax.inject.Singleton;

@Singleton
class SimplifiedInstrumenter implements InvocationInstrumenterFactory {

    @Nullable
    @Override
    public InvocationInstrumenter newInvocationInstrumenter() {
        Map<String, String> context = ThreadContext.getContext();
        return () -> {
            ThreadContext.putAll(context);
            return cleanup -> ThreadContext.clearMap();
        };
    }

}
