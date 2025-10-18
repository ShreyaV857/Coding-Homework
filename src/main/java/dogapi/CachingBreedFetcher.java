package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    private final BreedFetcher fetcher;                   // Underlying fetcher
    private final Map<String, List<String>> cache;         // Cache
    private int callsMade;
    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.fetcher = fetcher;
        this.cache = new HashMap<>();
        this.callsMade = 0;
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        if (cache.containsKey(breed)) {
            return cache.get(breed);
        }
        callsMade++;
        try {
            // Call underlying fetcher and increment calls
            List<String> subBreeds = fetcher.getSubBreeds(breed);
            cache.put(breed, subBreeds);  // Cache successful result
            return subBreeds;
        } catch (BreedNotFoundException e) {
            // Do not cache failed results
            throw e;
        }
    }

    public int getCallsMade() {
        return callsMade;
    }
}