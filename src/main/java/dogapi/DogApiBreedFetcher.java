package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        String url = "https://dog.ceo/api/breed/" + breed + "/list";
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new BreedNotFoundException("Failed to fetch sub-breeds for: " + breed);
            }

            String responseBody = response.body().string();
            JSONObject json = new JSONObject(responseBody);

            if (json.getString("status").equals("success")) {
                JSONArray messageArray = json.getJSONArray("message");
                List<String> subBreeds = new ArrayList<>();
                for (int i = 0; i < messageArray.length(); i++) {
                    subBreeds.add(messageArray.getString(i));
                }
                return subBreeds;  // ✅ return valid list here
            } else {
                throw new BreedNotFoundException("Breed not found: " + breed);
            }

        } catch (IOException e) {
            throw new BreedNotFoundException("Error fetching breed info for: " + breed);
        }
    }
}