package cycx.yoanime.Models.SearchProviders;

import org.jsoup.nodes.Element;

import java.util.List;

import cycx.yoanime.Models.Anime;
import cycx.yoanime.Utils.CloudFlareInitializationException;
import rx.exceptions.OnErrorThrowable;

public interface SearchProvider {

    List<Anime> searchFor (String searchTerm) throws OnErrorThrowable, CloudFlareInitializationException;

    Element isolate (String document);

    boolean hasSearchResults (Element element) throws OnErrorThrowable;

}
