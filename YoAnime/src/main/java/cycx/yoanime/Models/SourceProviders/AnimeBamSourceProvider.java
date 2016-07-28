package cycx.yoanime.Models.SourceProviders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cycx.yoanime.Utils.OK;
import cycx.yoanime.Models.Video;
import cycx.yoanime.Utils.GeneralUtils;
import okhttp3.Request;
import rx.exceptions.OnErrorThrowable;

public class AnimeBamSourceProvider implements SourceProvider{

    @Override
    public List<Video> fetchSource(String embedPageUrl) {
        String body = GeneralUtils.getWebPage(embedPageUrl);

        String elementHtml = GeneralUtils.jwPlayerIsolate(body);

        String notVideoUrl = elementHtml.substring(elementHtml.indexOf("file: \"") + 7, elementHtml.indexOf("\","));

        Request request = new Request.Builder()
                .get()
                .addHeader("Referer", "http://www.animebam.net/embed/17302")
                .addHeader("X-Requested-With", "ShockwaveFlash/18.0.0.209")
                .url(notVideoUrl)
                .build();

        List<Video> videos = new ArrayList<>(1);

        try {
            String response = OK.INSTANCE.Client.newCall(request).execute().toString();
            videos.add(new Video(null, response.substring(response.indexOf("url=") + 4, response.lastIndexOf(".mp4") + 4)));
        }catch (IOException io) {
            throw OnErrorThrowable.from(new Throwable("AnimeBam video retrieval failed.", io));
        }

        return videos;
    }

}
