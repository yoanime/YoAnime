package cycx.yoanime.Utils;

import android.content.Context;

import cycx.yoanime.LazyLibs.PersistentCookieJar.ClearableCookieJar;
import cycx.yoanime.LazyLibs.PersistentCookieJar.PersistentCookieJar;
import cycx.yoanime.LazyLibs.PersistentCookieJar.cache.SetCookieCache;
import cycx.yoanime.LazyLibs.PersistentCookieJar.persistence.SharedPrefsCookiePersistor;
import okhttp3.OkHttpClient;

public enum OK {
    INSTANCE;

    public OkHttpClient Client;

    public OkHttpClient createClient (Context context) {
        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));

        Client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();

        return Client;
    }

}
