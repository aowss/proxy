package test.proxy;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ProxyProcessor<T, S, R> implements Function<T, CompletableFuture<R>> {

    protected final Function<T, CompletableFuture<S>> processRequest;
    protected final Function<S, R> processResponse;

    public ProxyProcessor(Function<T, CompletableFuture<S>> processRequest, Function<S, R> processResponse) {
        this.processRequest = processRequest;
        this.processResponse = processResponse;
    }

    @Override
    public CompletableFuture<R> apply(T t) {
        return processRequest.apply(t).thenApply(processResponse);
    }

}
