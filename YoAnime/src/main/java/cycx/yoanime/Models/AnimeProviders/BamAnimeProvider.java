package cycx.yoanime.Models.AnimeProviders;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import cycx.yoanime.Models.Anime;
import cycx.yoanime.Models.Episode;
import cycx.yoanime.Models.Source;
import cycx.yoanime.Models.SourceProviders.SourceProvider;
import cycx.yoanime.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class BamAnimeProvider implements AnimeProvider {

    @Override
    public Anime fetchAnime(String url) throws OnErrorThrowable {
        String body = GeneralUtils.getWebPage(url);

        Element main = isolate(body);

        Anime anime = new Anime()
                .setUrl(url)
                .setProviderType(Anime.BAM);

        anime = parseForInfo(main, anime);

        anime.setEpisodes(parseForEpisodes(main));

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

        Elements sources = Jsoup.parse(body).select("body > div.container.videoframe > div > div.col-md-10 > div.tab-content.embed-responsive.embed-responsive-16by9 > div");

        return parseForSources(sources);
    }

    @Override
    public Source fetchVideo(Source source) throws OnErrorThrowable {

        source.setVideos(source.getSourceProvider().fetchSource(source.getEmbedUrl()));

        return source;
    }

    private Element isolate (String body) {
        return Jsoup.parse(body).select("body").first();
    }

    private Anime parseForInfo (Element main, Anime anime) {
        Element info = main.select("div.fattynav > div.fattynavinside > div.container > div.media").first();

        anime.setImageUrl("http:" + info.child(0).select("img").attr("src"));

        info = info.select("div.media-body").first();

        anime.setTitle(info.select("div.first > h1").text().trim());

        String status = info.select("div.first > p").first().children().get(1).text();
        anime.setStatus(status.substring(status.indexOf(": ") + 2));

        info = info.children().last();

        Elements genres = info.select("ul").first().select("li > a");
        StringBuilder genresBuilder = new StringBuilder();
        for (Element e : genres) {
            genresBuilder.append(e.text() + ", ");
        }
        anime.setGenresString(genresBuilder.toString().substring(0, genresBuilder.length() - 2));

        anime.setDesc(info.select("p.ptext").text());

        return anime;
    }

    private List<Episode> parseForEpisodes (Element main) {
        List<Episode> episodes = new ArrayList<>();

        Elements episodeElements = main.select("div.container.upbit > div.row > div.col-md-10 > div.cblock > ul.newmanga").first().children();

        for (Element episodeElement : episodeElements) {
             episodes.add(new Episode()
                     .setUrl("http://www.animebam.net" + episodeElement.child(0).child(0).attr("href"))
                     .setTitle(episodeElement.select("strong").text() + " " + episodeElement.select("i").first().text()));
        }

        return episodes;
    }

    private List<Source> parseForSources(Elements sourceElements) {
        List<Source> sources = new ArrayList<>();

        for (Element e : sourceElements) {
            String title = e.id().replace("-", " ");
            SourceProvider sourceProvider = GeneralUtils.determineSourceProvider(title.toLowerCase());
            if (sourceProvider != null) {
                sources.add(new Source()
                        // no page url needed
                        .setTitle(title)
                        .setSourceProvider(sourceProvider)
                        .setEmbedUrl("http://www.animebam.net" + e.select("iframe[src]").attr("src")));
            }
        }

        return sources;
    }

}
