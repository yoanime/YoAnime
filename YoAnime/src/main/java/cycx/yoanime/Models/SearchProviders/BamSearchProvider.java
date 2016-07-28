package cycx.yoanime.Models.SearchProviders;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import cycx.yoanime.Models.Anime;
import cycx.yoanime.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class BamSearchProvider implements SearchProvider{
    private static final String BASE_URL = "http://www.animebam.net/search?search=";

    @Override
    public List<Anime> searchFor(String searchTerm) throws OnErrorThrowable {
        String url = BASE_URL + GeneralUtils.encodeForUtf8(searchTerm);

        String responseBody = GeneralUtils.getWebPage(url);

        Element searchResultsBox = isolate(responseBody);

        if (!hasSearchResults(searchResultsBox)) {
            throw OnErrorThrowable.from(new Throwable("No search results."));
        }

        Elements searchResults = seperateResults(searchResultsBox);

        return parseResults(searchResults);
    }

    @Override
    public Element isolate(String document) {
        return Jsoup.parse(document).select("body > div.container.videoframe > div > div.col-md-10 > div").first();
    }

    @Override
    public boolean hasSearchResults(Element element) throws OnErrorThrowable {
        return element.select("p").first().text().isEmpty();
    }

    private Elements seperateResults (Element searchResultsBox) {
        return searchResultsBox.children();
    }

    private List<Anime> parseResults (Elements searchResults) {
        List<Anime> animes = new ArrayList<>(searchResults.size());

        for (Element searchResult : searchResults) {
            Anime anime = new Anime()
                    .setProviderType(Anime.BAM)
                    .setUrl("http://www.animebam.net" + searchResult.attr("href"));

            searchResult = searchResult.child(0);

            String imageUrl = "http:" + searchResult.select("img").attr("src").trim();

            if (!imageUrl.isEmpty()) {
                anime.setImageUrl(imageUrl);
            } else {
                // stupid image yay
                anime.setImageUrl("https://coubsecure-a.akamaihd.net/get/b25/p/coub/simple/cw_image/a55449e4464/f2e8c21cd0f3a62a3c3b7/cotd_email_1419345847_00016.jpg");
            }

            anime.setTitle(searchResult.select("h2").text().trim());

            StringBuilder descBuilder = new StringBuilder();
            Elements infoElements = searchResult.children().select("div");
            descBuilder.append(infoElements.first().select("div > div").get(1).text());
            descBuilder.append("\n");
            descBuilder.append(infoElements.last().text());

            anime.setDesc(descBuilder.toString().trim());
            anime.setAlternateTitle(infoElements.get(1).text().split(":")[1].trim());

            animes.add(anime);
        }

        return animes;
    }

}
