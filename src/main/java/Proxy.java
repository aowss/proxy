import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Proxy {

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


    public  <T,R> CompletableFuture<R> fetch(T request,
                                             Function<T, String> keyExtractor,
                                             Function<T, WSRequestHolder> requestMapper,
                                             Function<String, R> responseMapper,
                                             Function<String, CompletableFuture<Optional<R>>> retrieveFromCache,
                                             BiConsumer<String, R> insertIntoCache, ProcessingContext processingContext) throws RuntimeException {

        String key = keyExtractor.apply(request);

        long t1 = System.nanoTime();

        CompletableFuture<Optional<R>> cachedResult = retrieveFromCache.apply(key);

        cachedResult.exceptionally(exception -> {
            logger.warn(processingContext,Proxy.exception, "fetchFromCache", exception.getMessage());
            return Optional.<R>empty();
        });

        return cachedResult.thenCompose(eventuallyCachedResult -> {

            logger.info(processingContext,timing, "fetchFromCache", ( System.nanoTime() - t1 ) / 1000000.0);

            if (eventuallyCachedResult.isPresent()) {
                logger.debug(processingContext,"Data was cached for key : " + key);
                return cachedResult.thenApply(result -> result.get());
            } else {

                WSRequestHolder wsRequestHolder = requestMapper.apply(request);

                logger.info(processingContext,"outbound request url : " + new RequestBuilder("GET").
                        setUrl(wsRequestHolder.getUrl()).
                        setQueryParameters(new FluentStringsMap(wsRequestHolder.getQueryParameters())).
                        build().
                        getUrl());

                long t2 = System.nanoTime();

                CompletableFuture<WSResponse> backendResponse = Adapter.from(wsRequestHolder.get());

                backendResponse.handle( (result, exception) -> {
                    if (exception != null) {
                        logger.error(processingContext,Proxy.exception, "connectToBackend", exception.getMessage());
                        throw new BackendConnectionException(exception.getMessage());
                    } else {
                        return result;
                    }
                    //ProxyException mappedException = new BackendConnectionException(exception.getMessage());
                    //cachedResult.completeExceptionally(mappedException);
                    //throw mappedException;
                });
/*
                backendResponse.exceptionally(exception -> {
                    logger.error(Proxy.exception, "connectToBackend", exception.getMessage());
                    return null;
                    //ProxyException mappedException = new BackendConnectionException(exception.getMessage());
                    //cachedResult.completeExceptionally(mappedException);
                    //throw mappedException;
                });
*/

    CompletableFuture<R> response = backendResponse.
            thenApply(wsResponse -> {
                if (wsResponse.getStatus() == Http.Status.OK) {
                    return responseMapper.apply(wsResponse.getBody());
                } else {
                    logger.warn(processingContext,
                            wsRequestHolder.getUrl(),
                            ( wsRequestHolder.getQueryParameters() != null ? wsRequestHolder.getQueryParameters() : ""),
                            wsResponse.getStatus());
                    backendResponse.completeExceptionally(new BackendError(wsResponse.getStatus(), wsResponse.getAllHeaders(), wsResponse.getBody()));
                    return null;
                }
            });

                response.whenComplete( (result, exception) -> {
        if (result != null) {   //  We only save the result if no exception occured
            try {
                insertIntoCache.accept(key, result);
            } catch (Exception e) {
                logger.warn(processingContext, Proxy.exception, "insertIntoCache", e.getMessage());
            }
        }
    });

                return response;

}
        });

                }


}
