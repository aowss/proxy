package proxy;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ProxyProcessor<T, S, R> implements Function<T, CompletableFuture<R>> {

    protected final Function<T, CompletableFuture<S>> processRequest;
    protected final Function<S, R> processResponse;

    protected ProxyProcessor(Function<T, CompletableFuture<S>> processRequest, Function<S, R> processResponse) {
        this.processRequest = processRequest;
        this.processResponse = processResponse;
    }

    @Override
    public CompletableFuture<R> apply(T t) {
        return processRequest.apply(t).thenApply(processResponse);
    }

    public static class Builder<T, S, R extends Builder<T, S, R>> {

        private Function<T, CompletableFuture<S>> processRequest;
        private Function<S, R> processResponse;

        public Builder<T, S, R> withProcessors(Function<T, CompletableFuture<S>> processRequest, Function<S, R> processResponse) {
            this.processRequest = processRequest;
            this.processResponse = processResponse;
            return this;
        }

        public ProxyProcessor<T, S, R> build() {
            return new ProxyProcessor(processRequest, processResponse);
        }

    }

    public static void main(String[] args) {
        ProxyProcessor<String, String, String> proxyProcessor = new ProxyProcessor.Builder().
                withProcessors(in -> CompletableFuture.completedFuture("response for " + in), out -> "processed response for " + out).
                build();
    }

}
