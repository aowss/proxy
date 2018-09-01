import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Executor {

    /*
        1. extract key ( Function<T, String> )
        2. check in cache if present ( if yes return ) ( Function<String, CompletableFuture<Optional<R>>> )
        3. check if similar request in flight ( if yes, queue the request )
        4. map the request
        5. process the request
        9. asynchronously store result in cache
        6. map the result
        7. return result to requester
        8. return result to queued requesters
     */
    public  <T, K, S, R> CompletableFuture<R> process(T request,
                                                      Function<T, K> extractKey,
                                                      BiFunction<T, Function<T, K>, CompletableFuture<Optional<R>>> retrieveFromCache,
                                                      Function<K, List<CompletableFuture<R>>> retrievePendingRequests,
                                                      Function<T, CompletableFuture<S>> processRequest,
                                                      Function<S, R> processResponse,
                                                      BiFunction<S, Function<S, R>, BiConsumer<K, R>> storeInCache) {
        return null;
    }

    public BiFunction<?, Function<?, ?>, CompletableFuture<Optional<?>>> dontCheckCache = (request, extractKey) -> CompletableFuture.completedFuture(Optional.empty());

    /*
public  <T, K, S, R> CompletableFuture<R> process(
    T request,
-    Function<T, K> extractKey,
-    BiFunction<T, Function<T, K>, CompletableFuture<Optional<R>>> retrieveFromCache,
-    Function<K, Boolean> queueRequest,
-    Function<T, CompletableFuture<S>> processRequest,
-    Function<S, R> processResponse,
-    Function<K, List<CompletableFuture<R>>> retrievePendingRequests,
-    BiFunction<S, Function<S, R>, BiFunction<K, R, CompletableFuture<Boolean>>> storeInCache
);
 */
    public class Builder {

        public class Processor<T, S, R> {

            private Function<T, CompletableFuture<S>> processRequest;
            private Function<S, R> processResponse;

            private Processor(Function<T, CompletableFuture<S>> processRequest, Function<S, R> processResponse) {
                this.processRequest = processRequest;
                this.processResponse = processResponse;
            }

            public class ProcessorBuilder<T, S, R> {

                public CacheManagerBuilder<T, ?, S, R> build() {
                    return new CacheMana()
                }

            }

        }

        public class CacheManager<T, K, S, R> {
            private Function<T, K> extractKey;
            private BiFunction<T, Function<T, K>, CompletableFuture<Optional<R>>> retrieveFromCache;
            private BiFunction<S, Function<S, R>, BiFunction<K, R, CompletableFuture<Boolean>>> storeInCache;

            public class ProcessorBuilder<T, S, R> {
                public CacheManagerBuilder build() {

                }

            }
        }

        public class InFlightRequestManager<K, R> {
            private Function<K, Boolean> queueRequest;
            private Function<K, List<CompletableFuture<R>>> retrievePendingRequests;

            public class InFlightRequestManagerBuilder<T, S, R> {
                public Builder build() {

                }

            }

        }

    }

    ProxyProcessor<T, R> proxyProcessor = Builder.
            withProcessor().
            withCacheManager.
            withInFlightRequestManager();
}
