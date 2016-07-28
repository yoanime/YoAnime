package cycx.yoanime.Models.Hummingbird;

import java.util.Map;

import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import rx.Observable;

public interface HummingbirdService {

    @POST("users/authenticate")
    Observable<String> getAuthToken(@QueryMap Map<String, String> usernameAndPassword);

    @POST("libraries/{id}")
    Observable<HBLibraryEntry> updateLibraryEntry(@Path("id") String id,
                                          @Query("auth_token") String authToken,
                                          @Query("status") String status,
                                          @Query("privacy") String privacy,
                                          @Query("episodes_watched") int episodesWatched);

    @GET("anime/{slug}")
    Observable<HBAnime> getAnime(@Path("slug") String animeSlug);

    @GET("users/{user}")
    Observable<HBUser> getUser(@Path("user") String displayName);

    @GET("users/me")
    Observable<HBUser> getUserFromAuthToken(@Query("auth_token") String authToken);
}
