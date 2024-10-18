package example.micronaut;

import io.micronaut.function.aws.runtime.AbstractMicronautLambdaRuntime;
import java.net.MalformedURLException;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.micronaut.core.annotation.Nullable;

public class FunctionLambdaRuntime extends AbstractMicronautLambdaRuntime<Measurement, Measurement, Measurement, Measurement>
{
    public static void main(String[] args) {
        try {
            new FunctionLambdaRuntime().run(args);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Nullable
    protected RequestHandler<Measurement, Measurement> createRequestHandler(String... args) {
        return new FunctionRequestHandler();
    }
}
