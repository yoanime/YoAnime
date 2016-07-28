package cycx.yoanime.Models.SourceProviders;


import java.util.List;

import cycx.yoanime.Models.Video;
import rx.exceptions.OnErrorThrowable;

public interface SourceProvider {

    List<Video> fetchSource (String embedPageUrl) throws OnErrorThrowable;

}
