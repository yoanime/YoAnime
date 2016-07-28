// cloudflare-scrape Java implementation, original: https://github.com/Anorov/cloudflare-scrape
// Original License:
//
// The MIT License (MIT)
//
// Copyright (c) 2015 Anorov
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.


package cycx.yoanime.Utils;

import android.util.Log;

import com.annimon.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Cookie;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.exceptions.OnErrorThrowable;
import rx.schedulers.Schedulers;

/**
 * A singleton Http Client that bypasses Cloudflare authentication.
 * It does so by pretending to be a regular user when the connection is first made, after which it stores
 * the authorization cookie for upcoming sessions. Note that the singleton has to be initialized
 * by calling the `onCreate` method before it can be used for making connections.
 */
public enum CloudflareHttpClient {
    INSTANCE;

    private static final String[] CLOUDFLARE_URLS = {
            "http://kissanime.to/AdvanceSearch"
    };

    private final Pattern functionPattern = Pattern.compile("setTimeout\\(\\s*function\\s*\\(\\)\\s*\\{(.*)f\\.submit", Pattern.DOTALL);
    private final Pattern assignPattern = Pattern.compile("a\\.value =(.+?) \\+ .*");
    private final Pattern stripPattern = Pattern.compile("\\s{3,}[a-z](?: = |\\.).+");
    private final Pattern jsPattern = Pattern.compile("[\\n\\\\']");

    private AtomicInteger numInitialized;
    private boolean isInitializing;

    public void onCreate() {
        numInitialized = new AtomicInteger(0);
        isInitializing = false;

        registerSites();
    }

    public boolean isInitialized() {
        return !isInitializing;
    }

    /**
     * Registers a Cloudflare site so it can be used without delay when needed. The first registration
     * always takes at least 5 seconds, so this is run on a separate thread.
     * CLOUDFLARE_URLS  The URL of the site we want to register, with the http:// prefix
     */
    public void registerSites () {
        isInitializing = true;
        numInitialized.set(0);

        Observable.from(CLOUDFLARE_URLS)
                .observeOn(Schedulers.io())
                .map(s -> new Request.Builder().url(s).build())
                .map(s -> CloudflareHttpClient.INSTANCE.execute(s))
                .subscribe(new Subscriber<Response>() {
                    @Override
                    public void onNext(Response response) {
                        response.body().close();
                        if (CloudflareHttpClient.INSTANCE.numInitialized.incrementAndGet() == CLOUDFLARE_URLS.length)
                            onCompleted();
                    }

                    @Override
                    public void onCompleted() {
                        isInitializing = false;
                        unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w("Cloudflare", "Couldn't register CloudFlare site", e);
                    }
                });
    }

    private Response execute(Request request) throws OnErrorThrowable {
        Response resp = GeneralUtils.makeRequest(request);

        String refresh = resp.header("Refresh");
        String server = resp.header("Server");

        if (refresh != null && (refresh.contains("URL=/cdn-cgi/") ||
                server != null && server.equals("cloudflare-nginx"))) {

            return solveCloudflareRx(resp);
        }

        List<Cookie> cookies = OK.INSTANCE.Client.cookieJar().loadForRequest(request.url());
        boolean hasCookie = Stream.of(cookies).anyMatch(c -> c.name().equals("cf_clearance"));

        if (hasCookie) {
            return resp;
        }

        return resp;
    }

    private String transformFunction(String function) throws CloudflareException {
        // We first extract the main javascript function body: function() { --- this part --- }
        Matcher transformer = functionPattern.matcher(function);
        if (!transformer.find()) {
            throw new CloudflareException("Cloudflare evaluation function body could not be extracted.");
        }
        function = transformer.group(1);

        // We then replace the final statement so it returns the correct answer instead of assigning it
        transformer = assignPattern.matcher(function);
        if (!transformer.find()) {
            throw new CloudflareException("Cloudflare function structure changed.");
        }
        function = transformer.replaceFirst("$1;");

        // We then remove unneeded lines that would mess with the execution
        transformer = stripPattern.matcher(function);
        if (!transformer.find()) {
            throw new CloudflareException("Cloudflare variable names changed.");
        }
        function = transformer.replaceAll("");

        // If the function is not already a single line then convert it to that format
        transformer = jsPattern.matcher(function);
        if (transformer.find()) {
            function = transformer.replaceAll("");
        }

        return function;
    }

    private Response solveCloudflareRx (Response response) throws OnErrorThrowable{
        try {
            return solveCloudflare(response);
        } catch (IOException | CloudflareException e) {
            throw OnErrorThrowable.from(e);
        }
    }

    private Response solveCloudflare(Response response) throws IOException, CloudflareException {
        // Cloudflare requires 5 seconds of waiting before posting the response
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {
            // We cannot really do anything meaningful here
        }

        URI uri = response.request().url().uri();
        String domain = uri.getHost();

        Document page = Jsoup.parse(response.body().string());
        String challenge = page.select("[name=jschl_vc]").first().attr("value");
        String challengePass = page.select("[name=pass]").first().attr("value");
        String function = transformFunction(page.select("head script").first().html());

        // Get the JS context and set the optimization to -1 (interpreted mode), so that
        // it actually works on Android
        Context context = Context.enter();
        context.setOptimizationLevel(-1);
        try {
            // Get a JS scope so we can execute the Cloudflare code
            Scriptable scope = context.initSafeStandardObjects();
            Object jsResult = context.evaluateString(scope, function, "<cloudflare>", 1, null);
            long answer = new BigDecimal(jsResult.toString()).longValue() + domain.length();
            String submitUrl = String.format("%s://%s/cdn-cgi/l/chk_jschl?pass=%s&jschl_answer=%d&jschl_vc=%s",
                    uri.getScheme(), domain, challengePass, answer, challenge);

            Request solved = new Request.Builder()
                    .url(submitUrl)
                    .header("Referer", uri.toString())
                    .build();

            return OK.INSTANCE.Client.newCall(solved).execute();
        } finally {
            Context.exit();
        }
    }
}
