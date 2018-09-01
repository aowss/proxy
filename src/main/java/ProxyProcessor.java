import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ProxyProcessor<T, S, R> implements Function<T, CompletableFuture<R>> {

    protected final Function<T, CompletableFuture<S>> processRequest;
    protected final Function<S, R> processResponse;

    protected ProxyProcessor(Function<T, CompletableFuture<S>> processRequest, Function<S, R> processResponse) {
        this.processRequest = processRequest;
        this.processResponse = processResponse;
    }

    protected ProxyProcessor(Builder<?> builder) {
        this.processRequest = builder.processRequest;
        this.processResponse = builder.processResponse;
    }

    @Override
    public CompletableFuture<R> apply(T t) {
        return processRequest.apply(t).thenApply(processResponse);
    }

    public class Builder<U extends Builder<U>> {

        private Function<T, CompletableFuture<S>> processRequest;
        private Function<S, R> processResponse;

        protected Builder(Function<T, CompletableFuture<S>> processRequest, Function<S, R> processResponse) {
            this.processRequest = processRequest;
            this.processResponse = processResponse;
        }

        public U withProcessors(Function<T, CompletableFuture<S>> processRequest, Function<S, R> processResponse) {
            return self();
        }

        protected U self() { return this; }

        public ProxyProcessor<T, S, R> build() {
            return new ProxyProcessor(this);
        }

    }

    public static void main(String[] args) {
        ProxyProcessor<String, String, String> proxyProcessor = new ProxyProcessor.Builder().
                withProcessors(in -> CompletableFuture.completedFuture("response for " + in), out -> "processed response for " + out).
                build();
    }

}
