package cycx.yoanime.Models.SourceProviders;

import java.util.ArrayList;
import java.util.List;

import cycx.yoanime.Models.Video;
import cycx.yoanime.Utils.GeneralUtils;
import rx.exceptions.OnErrorThrowable;

public class YourUploadSourceProvider implements SourceProvider{

    @Override
    public List<Video> fetchSource(String embedPageUrl) throws OnErrorThrowable {

        String body = GeneralUtils.getWebPage(embedPageUrl);

        String elementHtml = GeneralUtils.jwPlayerIsolate(body);

        List<Video> videos = new ArrayList<>(1);
        videos.add(new Video(null, elementHtml.substring(elementHtml.indexOf("file: '") + 7, elementHtml.indexOf("',"))));

        return videos;
    }

}
