public class ProxyTest {

    Proxy.
            processor(Function<T, CompletableFuture<S>> processRequest, Function<S, R> processResponse)
            withCaching(Function<T, K> extractKey,
                        BiFunction<T, Function<T, K>, CompletableFuture<Optional<R>>> retrieveFromCache,
                        BiFunction<S, Function<S, R>, BiFunction<K, R, CompletableFuture<Boolean>>> storeInCache
            )
            withInFlightRequestHanlding(Function<K, Boolean> queueRequest, <1>, Function<K, List<CompletableFuture<R>>> retrievePendingRequests, <2>

    );)
}
