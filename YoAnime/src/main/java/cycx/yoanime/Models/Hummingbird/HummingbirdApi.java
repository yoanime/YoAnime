package cycx.yoanime.Models.Hummingbird;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

import java.util.HashMap;
import java.util.Map;

import cycx.yoanime.Utils.GeneralUtils;
import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.exceptions.OnErrorThrowable;

public class HummingbirdApi {
    public static final String BASE_URL = "https://hummingbird.me/";
    public static final String BASE_URL_V1 = "http://hummingbird.me/api/v1/";

    public static final String STATUS_CURRENTLY_WATCHING = "currently-watching";
    public static final String STATUS_COMPLETED = "completed";
    public static final String PRIVACY_PUBLIC = "public";
    public static final String PRIVACY_PRIVATE = "private";

    private final HummingbirdService hummingbirdService;

    public HummingbirdApi () {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_V1) //uses api-v1 by default
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        hummingbirdService = retrofit.create(HummingbirdService.class);
    }

    public Observable<String> getAuthToken (String usernameOrEmail, String password) {
        Map<String, String> userLoginData = new HashMap<>(2);
        userLoginData.put("password", password);

        if (usernameOrEmail.contains("@")) {
            userLoginData.put("email", usernameOrEmail);
        } else {
            userLoginData.put("username", usernameOrEmail);
        }

        return hummingbirdService.getAuthToken(userLoginData);
    }

    public Observable<HBLibraryEntry> updateLibraryEntry (String id, String authToken, String status, String privacy, int episodesWatched) {
        return hummingbirdService.updateLibraryEntry(id, authToken, status, privacy, episodesWatched);
    }

    public Observable<HBAnime> getAnime (String animeSlug) {
        return hummingbirdService.getAnime(animeSlug);
    }

    public Observable<HBUser> getUser (String displayName) {
        return hummingbirdService.getUser(displayName);
    }

    public Observable<HBUser> getUserFromAuthToken(String authToken) {
        return hummingbirdService.getUserFromAuthToken(authToken);
    }

    public static String getTitleFromRegularPage(String url) {
        String body = GeneralUtils.getWebPage(url.replace("anime/", "api/v1/anime/"));

        String title = null;

        try {

            JsonParser jsonParser = new JsonFactory().createParser(body);

            while (!jsonParser.isClosed()) {
                jsonParser.nextToken();

                if (jsonParser.getCurrentName() != null && jsonParser.getCurrentName().equals("title")) {
                    title = jsonParser.nextTextValue();
                    jsonParser.close();
                }

            }

        } catch (Exception e) {
            throw OnErrorThrowable.from(new Throwable("Hummingbird retrieval failed.", e));
        }

        return title;
        }

}
