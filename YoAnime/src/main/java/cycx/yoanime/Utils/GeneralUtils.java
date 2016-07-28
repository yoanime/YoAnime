package cycx.yoanime.Utils;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.greenrobot.event.EventBus;
import cycx.yoanime.Models.Anime;
import cycx.yoanime.Models.Source;
import cycx.yoanime.Models.SourceProviders.SourceProvider;
import cycx.yoanime.Utils.Events.SnackbarEvent;
import okhttp3.Request;
import okhttp3.Response;
import rx.exceptions.OnErrorThrowable;

public class GeneralUtils {

    public static Response makeRequest (final Request request) {
        try {
            return OK.INSTANCE.Client.newCall(request).execute();
        } catch (IOException io) {
            throw OnErrorThrowable.from(io);
        }
    }

    public static String getWebPage (final String url) {
        return GeneralUtils.getWebPage(new Request.Builder().url(url).build());
    }

    public static String getWebPage (final Request request) {
        try {
            return GeneralUtils.makeRequest(request).body().string();
        } catch (IOException e) {
            throw OnErrorThrowable.from(new Throwable("Failed to connect.", e));
        }
    }

    public static String encodeForUtf8 (String s) {
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (UnsupportedEncodingException u) {
            u.printStackTrace();
            return s.replace(":", "%3A")
                    .replace("/", "%2F")
                    .replace("#", "%23")
                    .replace("?", "%3F")
                    .replace("&", "%24")
                    .replace("@", "%40")
                    .replace("%", "%25")
                    .replace("+", "%2B")
                    .replace(" ", "+")
                    .replace(";","%3B")
                    .replace("=", "%3D")
                    .replace("$", "%26")
                    .replace(",", "%2C")
                    .replace("<", "%3C")
                    .replace(">", "%3E")
                    .replace("~", "%25")
                    .replace("^", "%5E")
                    .replace("`", "%60")
                    .replace("\\", "%5C")
                    .replace("[", "%5B")
                    .replace("]", "%5D")
                    .replace("{", "%7B")
                    .replace("|", "%7C")
                    .replace("\"", "%22");
        }
    }

 //   private static String getFileExtensionFromUrl (String url) {
   //    return url.substring(url.lastIndexOf("."));
  //  }

   // public static void internalDownload (DownloadManager downloadManager, String url, String title) {
        //String fileName = title.trim() + getFileExtensionFromUrl(url);
      //  String fileName = title.trim() + ".mp4";
      //  DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
      //  request.setTitle(fileName);
       // request.setDescription("YoAnime! Download");
      //  request.setDestinationInExternalPublicDir("/YoAnime", fileName);
      //  request.setMimeType("video/*");
     //  request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //request.allowScanningByMediaScanner();
        // ^ opens a dialog to open your launcher for some reason.
      //  downloadManager.enqueue(request);
   // }

    public static void lazyDownload(AppCompatActivity activity,  String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        } else {
            EventBus.getDefault().post(new SnackbarEvent("No app to open this link."));
        }
    }

    public static String formatError (Throwable e) {
        if (e.getMessage() != null) {
            return " " + e.getMessage().replace("java.lang.Throwable:", "").trim();
        }
       return "An error occurred.";
    }

    public static String jwPlayerIsolate (String body) {
        return Jsoup.parse(body).select("div#player").first().nextElementSibling().html();
    }

    public static String formattedGenres(String[] genres) {
        StringBuilder genresBuilder = new StringBuilder();
        for (String genre : genres) {
            genresBuilder.append(" ");
            genresBuilder.append(genre);
            genresBuilder.append(",");
        }
        genresBuilder.deleteCharAt(genresBuilder.length() - 1);
        return genresBuilder.toString();
    }

    // Need to handle null by yourself
    @Nullable
    public static String serializeAnime(Anime anime) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(anime);
        }catch (IOException io) {
            io.printStackTrace();
            return null;
        }
    }

    // Need to handle null by yourself
    @Nullable
    public static Anime deserializeAnime(String serializedFavourite) {
        try {
            return new ObjectMapper().readValue(serializedFavourite, Anime.class);
        }catch (IOException io) {
            io.printStackTrace();
            return null;
        }
    }

    public static int determineProviderType (String url) throws Exception{
        url = url.toUpperCase();
        if (url.contains(Anime.RUSH_TITLE)) {
            return Anime.RUSH;
        } else if (url.contains(Anime.RAM_TITLE)) {
            return Anime.RAM;
        } else if (url.contains(Anime.BAM_TITLE)) {
            return Anime.BAM;
        }
        throw new Exception("Unsupported source");
    }


    public static SourceProvider determineSourceProvider (String lowerCaseTitle) {
        for (String sourceName : Source.sourceMap.keySet()) {
            if (lowerCaseTitle.contains(sourceName)) {
                return Source.sourceMap.get(sourceName);
            }
        }
        return null;
    }


/*
    public static void basicAsyncObservableVoid (BasicObservableable basicObservableable, String string) {

        Observable.defer(new Func0<Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call() {
                return Observable.just(basicObservableable.execute(string));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {

                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });

    }
*/

}










