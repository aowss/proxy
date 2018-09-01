import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class Processor<T, R> implements Function<T, CompletableFuture<R>> {

    protected final Function<T, CompletableFuture<R>> processRequest;
    protected final Function<S, R> processResponse;

    protected Processor(Function<T, CompletableFuture<S>> processRequest, Function<S, R> processResponse) {
        this.processRequest = processRequest;
        this.processResponse = processResponse;
    }

    protected Processor(Builder<?> builder) {
        this.processRequest = builder.processRequest;
        this.processResponse = builder.processResponse;
    }

    @Override
    public CompletableFuture<R> apply(T t) {
        return processRequest.apply(t).thenApply(processResponse);
    }

    abstract class Builder<U extends Builder<U>> {

        private Function<T, CompletableFuture<S>> processRequest;
        private Function<S, R> processResponse;

        protected Builder(Function<T, CompletableFuture<S>> processRequest, Function<S, R> processResponse) {
            this.processRequest = processRequest;
            this.processResponse = processResponse;
        }

        public U withProcessors(Function<T, CompletableFuture<S>> processRequest, Function<S, R> processResponse) {
            return self();
        }

        protected Builder self() { return this; }

        public Processor<T, S, R> build() {
            return new Processor(this);
        }

    }

    public static void main(String[] args) {
        Processor<String, String, String> proxyProcessor = new Processor.Builder().
                withProcessors(in -> CompletableFuture.completedFuture("response for " + in), out -> "processed response for " + out).
                build();
    }

}
