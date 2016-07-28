package cycx.yoanime.Models.SearchProviders;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import cycx.yoanime.Models.Anime;
import cycx.yoanime.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class RamSearchProvider implements SearchProvider{
    private static final String BASE_URL = "http://www.animeram.co/search?search=";

    @Override
    public List<Anime> searchFor(String searchTerm) throws OnErrorThrowable {
        String url = BASE_URL + GeneralUtils.encodeForUtf8(searchTerm);

        String responseBody = GeneralUtils.getWebPage(url);

        Element searchResultsBox =  isolate(responseBody);

        if (!hasSearchResults(searchResultsBox)) {
            throw OnErrorThrowable.from(new Throwable("No search results."));
        }

        Elements searchResults = separateResults(searchResultsBox);

        return parseResults(searchResults);
    }

    @Override
    public Element isolate(String document) {
        return Jsoup.parse(document).select("body > div.container > div.cblock2.col-md-10 > div.cblock > div.moose.page").first();
    }

    @Override
    public boolean hasSearchResults(Element element) throws OnErrorThrowable {
        return element != null && !element.select("td > div").text().contains("Nothing here");
    }

    private Elements separateResults(Element searchResultsBox) {
        return searchResultsBox.children();
    }

    private List<Anime> parseResults (Elements searchResults) {
        List<Anime> animes = new ArrayList<>(searchResults.size());

        for (Element searchResult : searchResults) {
            Anime anime = new Anime().setProviderType(Anime.RAM);

            anime.setTitle(searchResult.select("h2").text());
            anime.setUrl("http://www.animeram.co" + searchResult.attr("href"));
            anime.setImageUrl("http:" + searchResult.select("img").attr("src"));
            StringBuilder descBuilder = new StringBuilder();
            descBuilder.append(searchResult.select("div.first > div").first().text());
            descBuilder.append("\n");
            descBuilder.append(searchResult.select("div").last().text());
            anime.setDesc(descBuilder.toString().trim());

            animes.add(anime);
        }

        return animes;
    }

}
