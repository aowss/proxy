package proxy;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ConcurrentCachingProcessor<T, K, S, R> extends ProxyProcessor<T, S, R> {

    protected final Function<T, K> extractKey;
    protected final BiFunction<T, Function<T, K>, CompletableFuture<Optional<R>>> retrieveFromCache;
    protected final BiFunction<S, Function<S, R>, BiFunction<K, R, CompletableFuture<Boolean>>> storeInCache;

    protected ConcurrentCachingProcessor(Function<T, CompletableFuture<S>> processRequest,
                                         Function<S, R> processResponse,
                                         Function<T, K> extractKey,
                                         BiFunction<T, Function<T, K>, CompletableFuture<Optional<R>>> retrieveFromCache,
                                         BiFunction<S, Function<S, R>, BiFunction<K, R, CompletableFuture<Boolean>>> storeInCache) {
        super(processRequest, processResponse);
        this.extractKey = extractKey;
        this.retrieveFromCache = retrieveFromCache;
        this.storeInCache = storeInCache;
    }

    @Override
    public CompletableFuture<R> apply(T t) {
        //return processRequest.apply(t).thenApply(processResponse);
        return null;
    }

}
