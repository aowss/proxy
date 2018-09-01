import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CachingProcessor<T, K, S, R> extends ProxyProcessor<T, S, R> {

    protected final Function<T, K> extractKey;
    protected final BiFunction<T, Function<T, K>, CompletableFuture<Optional<R>>> retrieveFromCache;
    protected final BiFunction<S, Function<S, R>, BiFunction<K, R, CompletableFuture<Boolean>>> storeInCache;

    protected CachingProcessor(Function<T, CompletableFuture<S>> processRequest,
                             Function<S, R> processResponse,
                             Function<T, K> extractKey,
                             BiFunction<T, Function<T, K>, CompletableFuture<Optional<R>>> retrieveFromCache,
                             BiFunction<S, Function<S, R>, BiFunction<K, R, CompletableFuture<Boolean>>> storeInCache) {
        super(processRequest, processResponse);
        this.extractKey = extractKey;
        this.retrieveFromCache = retrieveFromCache;
        this.storeInCache = storeInCache;
    }

    protected CachingProcessor(Builder builder) {
        super(builder);
        this.extractKey = builder.extractKey;
        this.retrieveFromCache = builder.retrieveFromCache;
        this.storeInCache = builder.storeInCache;
    }

    @Override
    public CompletableFuture<R> apply(T t) {
        //return processRequest.apply(t).thenApply(processResponse);
        return null;
    }

    public static class Builder<T, K, S, R extends Builder<T, K, S, R>> extends ProxyProcessor.Builder<T, S, R> {

        private Function<T, K> extractKey;
        private BiFunction<T, Function<T, K>, CompletableFuture<Optional<R>>> retrieveFromCache;
        private BiFunction<S, Function<S, R>, BiFunction<K, R, CompletableFuture<Boolean>>> storeInCache;

        protected Builder(Function<T, CompletableFuture<S>> processRequest, Function<S, R> processResponse) {
            super(processRequest, processResponse);
        }

        public Builder withCaching(Function<T, K> extractKey,
                                   BiFunction<T, Function<T, K>, CompletableFuture<Optional<R>>> retrieveFromCache,
                                   BiFunction<S, Function<S, R>, BiFunction<K, R, CompletableFuture<Boolean>>> storeInCache) {
            this.extractKey = extractKey;
            this.retrieveFromCache = retrieveFromCache;
            this.storeInCache = storeInCache;
            return this;
        }

        @Override
        protected CachingProcessor.Builder self() { return this; }

        public CachingProcessor build() {
            return new CachingProcessor(this);
        }

    }

    public static void main(String[] args) {

        ProxyProcessor<String, String, String> proxyProcessor = new CachingProcessor.Builder().
                withProcessors(in -> CompletableFuture.completedFuture("response for " + in), out -> "processed response for " + out).
                build();

        CachingProcessor<String, String, String, String> cachingProcessor = new CachingProcessor.Builder().
                withProcessors(in -> CompletableFuture.completedFuture("response for " + in), out -> "processed response for " + out)
                build();

    }

}
