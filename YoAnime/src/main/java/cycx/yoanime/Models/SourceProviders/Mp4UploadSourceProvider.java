package cycx.yoanime.Models.SourceProviders;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import cycx.yoanime.Models.Video;
import cycx.yoanime.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class Mp4UploadSourceProvider implements SourceProvider{

    @Override
    public List<Video> fetchSource(String embedPageUrl) {
        String body = GeneralUtils.getWebPage(embedPageUrl);

        Element playerScript = Jsoup.parse(body).select("script").get(6);

        if (playerScript == null) {
            throw OnErrorThrowable.from(new Throwable("MP4Upload video retrieval failed."));
        }

        String elementHtml = playerScript.html();

        List<Video> videos = new ArrayList<>(1);
        videos.add(new Video(null, elementHtml.substring(elementHtml.indexOf("jwplayer(\"player_code\").setup({\n" +
                "\t  \"file\": ") + 44).split("\",")[0]));

        System.out.println(videos.get(0).getUrl());

        return videos;
    }

}
