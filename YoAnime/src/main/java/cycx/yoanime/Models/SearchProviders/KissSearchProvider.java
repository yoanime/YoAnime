package cycx.yoanime.Models.SearchProviders;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cycx.yoanime.Models.Anime;
import cycx.yoanime.Utils.CloudFlareInitializationException;
import cycx.yoanime.Utils.CloudflareHttpClient;
import cycx.yoanime.Utils.GeneralUtils;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import rx.exceptions.OnErrorThrowable;

public class KissSearchProvider implements SearchProvider {
    static String BASE_URL = "https://kissanime.to";
    static String SEARCH_URL = "http://kissanime.to/AdvanceSearch";
    static int providerType = Anime.KISS;
    private static final Pattern PARSER = Pattern.compile(".*src=\"(.*?)\".*href=\"(.*)\">(.*?)</a>.*<p>\\s*(.*?)\\s*</p>", Pattern.DOTALL);
    private static final int NUM_GENRES = 47;

    @Override
    public List<Anime> searchFor(String searchTerm) throws OnErrorThrowable, CloudFlareInitializationException {

        if (!CloudflareHttpClient.INSTANCE.isInitialized()) {
            throw new CloudFlareInitializationException();
        }

        RequestBody query = searchTemplate()
                .add("animeName", searchTerm)
                .build();

        Request search = new Request.Builder()
                .url(SEARCH_URL)
                .post(query)
                .build();

        String responseBody = GeneralUtils.getWebPage(search);

        Element resultTable = isolate(responseBody);

        if (resultTable == null) {
            throw OnErrorThrowable.from(new Throwable("Anime Not found"));
        }

        return parseElements(resultTable.select("td[title]"));
    }

    @Override
    public Element isolate(String document) {
        return Jsoup.parse(document).select("table.listing").first();
    }

    @Override
    public boolean hasSearchResults(Element element) throws OnErrorThrowable {
        return element.select("td[title]").size() > 0;
    }

    private List<Anime> parseElements(Elements rows) {
        List<Anime> results = new ArrayList<>(rows.size());
        for (Element row : rows) {
            Anime anime = new Anime().setProviderType(providerType);
            String titleTag = row.attr("title");
            Matcher matcher = PARSER.matcher(titleTag);
            if (matcher.find()) {
                anime.setImageUrl(matcher.group(1))
                     .setUrl(BASE_URL + matcher.group(2))
                     .setTitle(matcher.group(3))
                     .setDesc(matcher.group(4));
            }
            results.add(anime);
        }
        return results;
    }

    private FormBody.Builder searchTemplate() {
        FormBody.Builder searchTemplate = new FormBody.Builder();
        for (int i = 0; i < NUM_GENRES; ++i) {
            searchTemplate.add("genres", "0");
        }
        searchTemplate.add("status", "");
        return searchTemplate;
    }
}
