package test.proxy;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CachingProcessor<T, K, S, R> implements Function<T, CompletableFuture<R>> {

    protected final ProxyProcessor<T, S, R> processor;
    protected final Function<T, K> extractKey;
    protected final BiFunction<T, Function<T, K>, CompletableFuture<Optional<R>>> retrieveFromCache;
    protected final Function<S, BiFunction<K, R, CompletableFuture<Boolean>>> storeInCache;

    protected CachingProcessor(ProxyProcessor<T, S, R> processor,
                             Function<T, K> extractKey,
                             BiFunction<T, Function<T, K>, CompletableFuture<Optional<R>>> retrieveFromCache,
                             Function<S, BiFunction<K, R, CompletableFuture<Boolean>>> storeInCache) {
        this.processor = processor;
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
