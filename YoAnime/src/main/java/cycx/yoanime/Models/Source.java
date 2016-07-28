package cycx.yoanime.Models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cycx.yoanime.Models.SourceProviders.AnimeBamSourceProvider;
import cycx.yoanime.Models.SourceProviders.DailyMotionSourceProvider;
import cycx.yoanime.Models.SourceProviders.EngineSourceProvider;
import cycx.yoanime.Models.SourceProviders.GoSourceProvider;
import cycx.yoanime.Models.SourceProviders.Mp4UploadSourceProvider;
import cycx.yoanime.Models.SourceProviders.SourceProvider;
import cycx.yoanime.Models.SourceProviders.YourUploadSourceProvider;

public class Source implements Serializable{
    public static final Map<String, SourceProvider> sourceMap = initSourceList();

    private static Map<String, SourceProvider> initSourceList () {
        Map<String, SourceProvider> sourceMap = new HashMap<>();

        sourceMap.put("mp4upload", new Mp4UploadSourceProvider());
        sourceMap.put("dailymotion", new DailyMotionSourceProvider());
        sourceMap.put("engine", new EngineSourceProvider());
        sourceMap.put("yourupload", new YourUploadSourceProvider());
        sourceMap.put("go", new GoSourceProvider());
        sourceMap.put("abvideo", new AnimeBamSourceProvider());

        return sourceMap;
    }

    private String title;
    private String pageUrl;
    private String embedUrl;
    private List<Video> videos;
    private SourceProvider sourceProvider;

    public String getTitle() {
        return title;
    }

    public Source setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public Source setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
        return this;
    }

    public String getEmbedUrl() {
        return embedUrl;
    }

    public Source setEmbedUrl(String embedUrl) {
        this.embedUrl = embedUrl;
        return this;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public Source setVideos(List<Video> videos) {
        this.videos = videos;
        return this;
    }

    public SourceProvider getSourceProvider() {
        return sourceProvider;
    }

    public Source setSourceProvider(SourceProvider sourceProvider) {
        this.sourceProvider = sourceProvider;
        return this;
    }

}
