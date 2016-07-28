package cycx.yoanime.Models.AnimeProviders;

import java.util.List;

import cycx.yoanime.Models.Anime;
import cycx.yoanime.Models.Source;
import cycx.yoanime.Utils.CloudFlareInitializationException;
import rx.exceptions.OnErrorThrowable;

public interface AnimeProvider {

    Anime fetchAnime(String url) throws OnErrorThrowable, CloudFlareInitializationException;

    Anime updateCachedAnime (Anime cachedAnime) throws OnErrorThrowable, CloudFlareInitializationException;

    List<Source> fetchSources (String url) throws OnErrorThrowable, CloudFlareInitializationException;

    Source fetchVideo (Source source) throws OnErrorThrowable, CloudFlareInitializationException;

}
