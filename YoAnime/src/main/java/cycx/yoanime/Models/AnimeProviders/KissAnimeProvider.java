package cycx.yoanime.Models.AnimeProviders;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cycx.yoanime.Models.Anime;
import cycx.yoanime.Models.Episode;
import cycx.yoanime.Models.Source;
import cycx.yoanime.Models.Video;
import cycx.yoanime.Utils.CloudFlareInitializationException;
import cycx.yoanime.Utils.CloudflareHttpClient;
import cycx.yoanime.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class KissAnimeProvider implements AnimeProvider {
    static String BASE_URL = "https://kissanime.to";
    static int providerType = Anime.KISS;
    private static final Pattern EXTRACT_STATUS = Pattern.compile("Status:\\s*(.*?)\\s{2,}Views");

    private static final byte[] DECODE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".getBytes(Charset.forName("UTF-8"));
    private static int[] DECODE_LOOKUP = new int[129];

    static {
        for (int i = 0; i < DECODE_ALPHABET.length; i++) {
            DECODE_LOOKUP[DECODE_ALPHABET[i]] = i;
        }
    }

    @Override
    public Anime fetchAnime(String url) throws OnErrorThrowable, CloudFlareInitializationException {

        if (!CloudflareHttpClient.INSTANCE.isInitialized()) {
            throw new CloudFlareInitializationException();
        }

        String body = GeneralUtils.getWebPage(url);

        Document doc = Jsoup.parse(body);

        Anime anime = new Anime()
                .setUrl(url)
                .setProviderType(providerType);
        anime = parseInfo(doc, anime);
        anime.setEpisodes(parseEpisodes(doc));

        return anime;
    }

    @Override
    public Anime updateCachedAnime(Anime cachedAnime) throws OnErrorThrowable, CloudFlareInitializationException {
        Anime updatedAnime = fetchAnime(cachedAnime.getUrl());
        updatedAnime.inheritWatchedFrom(cachedAnime.getEpisodes());
        updatedAnime.setMajorColour(cachedAnime.getMajorColour());
        return updatedAnime;
    }

    @Override
    public List<Source> fetchSources(String url) throws OnErrorThrowable, CloudFlareInitializationException {

        if (!CloudflareHttpClient.INSTANCE.isInitialized()) {
            throw new CloudFlareInitializationException();
        }

        String body = GeneralUtils.getWebPage(url);

        Elements downloads = Jsoup.parse(body).select("#selectQuality option");
        List<Source> sources = new ArrayList<>(downloads.size());

        for (Element source : downloads) {
            String value = source.attr("value");
            String decoded = decode(value);
            sources.add(new Source()
                .setTitle(source.text())
                .setPageUrl(decoded));
        }

        return sources;
    }

    @Override
    public Source fetchVideo(Source source) throws OnErrorThrowable {
        List<Video> videos = new ArrayList<>(1);
        videos.add(new Video(source.getTitle(), source.getPageUrl()));
        return source.setVideos(videos);
    }

    private Anime parseInfo(Document doc, Anime anime) {
        Elements info = doc .select("#leftside > .bigBarContainer:first-of-type > .barContent > div > p:not(:empty)");

        String title = doc.select(".bigChar").text();

        String image = doc.select(".rightBox img").attr("src");

        String altNames = Stream.of(info.select("p:contains(Other name)").select("[title]"))
                .map(Element::text)
                .collect(Collectors.joining(", "));

        List<String> genres = Stream.of(info.select("p:contains(Genres)").select("a"))
                .map(g -> g.attr("href"))
                .map(g -> g.substring(g.lastIndexOf('/') + 1))
                .collect(Collectors.toList());
        String genreString = Stream.of(genres).collect(Collectors.joining(", "));


        Element date = info.select("p:contains(Date)").first();
        if (date != null) {
            String text = date.text();
            anime.setDate(text.substring(text.indexOf(':') + 2));
        } else {
            anime.setDate("-");
        }

        Element status = info.select("p:contains(Status)").first();
        if (status != null) {
            Matcher statusMatcher = EXTRACT_STATUS.matcher(status.text());
            if (statusMatcher.find()) {
                anime.setStatus(statusMatcher.group(1));
            } else {
                anime.setStatus("-");
            }
        } else {
            anime.setStatus("-");
        }

        anime.setTitle(title)
             .setAlternateTitle(altNames)
             .setGenres(genres.toArray(new String[genres.size()]))
             .setGenresString(genreString)
             .setDesc(info.last().text())
             .setImageUrl(image);

        return anime;
    }

    private List<Episode> parseEpisodes(Document doc) {
        Elements episodeElements = doc.select(".episodeList .listing").first().select("[title]");
        List<Episode> episodes = new ArrayList<>(episodeElements.size());

        for (Element episode : episodeElements) {
            episodes.add(new Episode()
                .setTitle(episode.text())
                .setUrl(BASE_URL + episode.attr("href")));
        }

        return episodes;
    }

    private List<Integer> decoderFromUtf8(byte[] s) {
        List<Integer> result = new ArrayList<>();
        int[] enc = {-1, -1, -1, -1};

        int position = 0;
        while (position < s.length) {
            enc[0] = DECODE_LOOKUP[s[position++]];
            enc[1] = DECODE_LOOKUP[s[position++]];
            result.add(enc[0] << 2 | enc[1] >> 4);

            enc[2] = DECODE_LOOKUP[s[position++]];
            if (enc[2] == 64)
                break;
            result.add(((enc[1] & 15) << 4) | (enc[2] >> 2));

            enc[3] = DECODE_LOOKUP[s[position++]];
            if (enc[3] == 64)
                break;
            result.add(((enc[2] & 3) << 6) | enc[3]);
        }

        return result;
    }

    private String decode(String s) {
         List<Integer> buffer = decoderFromUtf8(s.getBytes(Charset.forName("UTF-8")));
         StringBuilder result = new StringBuilder();

         int position = 0;
         while (position < buffer.size()) {
             if (buffer.get(position) < 128) {
                result.append(Character.toChars(buffer.get(position++)));
             } else if (buffer.get(position) > 191 && buffer.get(position) < 224) {
                int a = (buffer.get(position++) & 31) << 6;
                int b = (buffer.get(position++) & 63);
                result.append(Character.toChars(a | b));
             } else {
                int a = (buffer.get(position++) & 15) << 12;
                int b = (buffer.get(position++) & 63) << 6;
                int c = (buffer.get(position++) & 63);
                result.append(Character.toChars(a | b | c));
             }
         }
         return result.toString();
    }
}
