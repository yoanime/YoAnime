package cycx.yoanime.Models.AnimeProviders;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cycx.yoanime.Models.Anime;
import cycx.yoanime.Models.Episode;
import cycx.yoanime.Models.Source;
import cycx.yoanime.Models.SourceProviders.SourceProvider;
import cycx.yoanime.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class RamAnimeProvider implements AnimeProvider {
    private static final String BASE_URL = "http://www.animeram.co";

    @Override
    public Anime fetchAnime(String url) throws OnErrorThrowable {
        String body = GeneralUtils.getWebPage(url);

        if (!hasAnime(body)) {
            throw OnErrorThrowable.from(new Throwable("Failed to retrieve anime."));
        }

        Element animeBox = isolate(body);

        Element info = animeBox.select("div.fattynavinside > div.container > div.media").first();

        Elements episodes = animeBox.select("div.container > div > div > div.col-md-10 > div.cblock > ul").first().children();

        Anime anime = new Anime()
                .setProviderType(Anime.RAM)
                .setUrl(url);

        anime = parseForInfo(info, anime);

        anime.setEpisodes(parseForEpisodes(episodes));

        return anime;
    }

    @Override
    public Anime updateCachedAnime(Anime cachedAnime) throws OnErrorThrowable {
        Anime updatedAnime = fetchAnime(cachedAnime.getUrl());

        updatedAnime.inheritWatchedFrom(cachedAnime.getEpisodes());

        updatedAnime.setMajorColour(cachedAnime.getMajorColour());

        return updatedAnime;
    }

    @Override
    public List<Source> fetchSources(String url) throws OnErrorThrowable {
        String body = GeneralUtils.getWebPage(url);

        return parseForSources(url, isolateForSources(body));
    }

    @Override
    public Source fetchVideo(Source source) throws OnErrorThrowable {
        String body = GeneralUtils.getWebPage(source.getPageUrl());

        source.setEmbedUrl(parseForEmbedUrl(body));

        source.setVideos(source.getSourceProvider().fetchSource(source.getEmbedUrl()));

        return source;
    }

    private Element isolate (String body) {
        return Jsoup.parse(body).select("body > div.fattynav").first();
    }

    private Elements isolateForSources (String body) {
        return Jsoup.parse(body).select("body > div.darkness > div > div > div.col-md-10 > div:nth-child(1) > ul.nav.nav-tabs").first().children();
    }

    private boolean hasAnime (String body) {
        return !body.toLowerCase().contains("show not found");
    }

    private Anime parseForInfo (Element info, Anime anime) {
        anime.setImageUrl("http:" + info.select("img").attr("src"));

        info = info.select("div.media-body").first();

        return anime
                .setTitle(info.select("h1").text())
                .setAlternateTitle(info.child(0).child(2).text().substring(18))
                .setGenresString(info.child(1).child(0).text())
                .setStatus(info.child(0).child(1).child(1).text().substring(8))
                .setDesc(info.select("p.ptext").text());

    }

    private List<Episode> parseForEpisodes (Elements episodesElement) {
        List<Episode> episodes = new ArrayList<>(episodesElement.size());

        for (Element episodeElement : episodesElement) {
            Elements info = episodeElement.child(0).children();

            String title = info.first().text() + " " + info.get(1).text();

            episodes.add(new Episode()
                    .setTitle(title.trim())
                    .setUrl(BASE_URL + info.first().attr("href")));
        }

        return episodes;
    }

    private List<Source> parseForSources (String url, Elements sourcesElements) throws OnErrorThrowable{

        List<Source> sources = new ArrayList<>(sourcesElements.size());

        for (Element sourceElement : sourcesElements) {
            sourceElement = sourceElement.child(0);

            StringBuilder titleBuilder = new StringBuilder();
            for (Element child : sourceElement.children()) {
                titleBuilder.append(child.text());
                titleBuilder.append(" ");
            }
            String title = titleBuilder.toString();

            SourceProvider sourceProvider = GeneralUtils.determineSourceProvider(title.toLowerCase());
            if (sourceProvider != null) {

                String subUrl = sourceElement.attr("href");
                if (subUrl != null) {
                    subUrl = subUrl.trim();
                }

                Source source = new Source()
                        .setTitle(title.trim())
                        .setSourceProvider(sourceProvider);

                if (subUrl == null || subUrl.trim().isEmpty() || subUrl.equals("#")) {
                    source.setPageUrl(url);
                } else {
                    source.setPageUrl(BASE_URL + subUrl);
                }

                sources.add(source);
            }
        }

        return sources;
    }

    private String parseForEmbedUrl (String body) {
        String embedUrl = Jsoup.parse(body)
                .select("body > div.darkness > div > div > div.col-md-10 > div:nth-child(1) > div.tab-content.embed-responsive.embed-responsive-16by9 > div > iframe")
                .attr("src");

        if (!embedUrl.contains("http")) {
            Pattern pattern = Pattern.compile("//.+");
            Matcher matcher = pattern.matcher(embedUrl);

            if (matcher.matches()) {
                embedUrl = embedUrl.replaceFirst("//", "http://");
            }
        }

        return embedUrl;
    }

}
