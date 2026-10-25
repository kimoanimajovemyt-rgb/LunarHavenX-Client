package com.lunarhavenx.launcher.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.LongConsumer;

/** HTTP sobre HttpURLConnection (disponível também no Android, ao contrário de java.net.http). */
public final class Http {
    public static final String UA = "LunarHavenX-Launcher/1.0.0";

    private Http() { }

    public static final class Response {
        public final int code;
        public final String body;
        Response(int code, String body) { this.code = code; this.body = body; }
        public boolean ok() { return code >= 200 && code < 300; }
    }

    private static HttpURLConnection openFollowing(String url, Map<String, String> headers) throws IOException {
        String cur = url;
        for (int hop = 0; hop < 8; hop++) {
            HttpURLConnection c = (HttpURLConnection) new URL(cur).openConnection();
            c.setInstanceFollowRedirects(false);
            c.setConnectTimeout(15000);
            c.setReadTimeout(30000);
            c.setRequestProperty("User-Agent", UA);
            if (headers != null) for (Map.Entry<String, String> e : headers.entrySet()) c.setRequestProperty(e.getKey(), e.getValue());
            int code = c.getResponseCode();
            if (code >= 300 && code < 400 && c.getHeaderField("Location") != null) {
                cur = new URL(new URL(cur), c.getHeaderField("Location")).toString();
                c.disconnect();
                continue;
            }
            return c;
        }
        throw new IOException("Redirecionamentos demais: " + url);
    }

    private static String readAll(InputStream in) throws IOException {
        if (in == null) return "";
        try (InputStream is = in; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = is.read(buf)) > 0) out.write(buf, 0, n);
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    /** GET que lança IOException se o código não for 2xx. */
    public static String get(String url, Map<String, String> headers) throws IOException {
        HttpURLConnection c = openFollowing(url, headers);
        int code = c.getResponseCode();
        if (code < 200 || code >= 300) throw new IOException("HTTP " + code + " em " + url);
        return readAll(c.getInputStream());
    }

    /** GET que devolve o código e o corpo mesmo em erro (usado na autenticação). */
    public static Response getRaw(String url, Map<String, String> headers) throws IOException {
        HttpURLConnection c = openFollowing(url, headers);
        int code = c.getResponseCode();
        return new Response(code, readAll(code >= 400 ? c.getErrorStream() : c.getInputStream()));
    }

    public static Response post(String url, String contentType, String body, Map<String, String> headers) throws IOException {
        HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
        c.setConnectTimeout(15000);
        c.setReadTimeout(30000);
        c.setRequestMethod("POST");
        c.setRequestProperty("User-Agent", UA);
        c.setRequestProperty("Content-Type", contentType);
        c.setRequestProperty("Accept", "application/json");
        if (headers != null) for (Map.Entry<String, String> e : headers.entrySet()) c.setRequestProperty(e.getKey(), e.getValue());
        c.setDoOutput(true);
        try (OutputStream o = c.getOutputStream()) { o.write(body.getBytes(StandardCharsets.UTF_8)); }
        int code = c.getResponseCode();
        return new Response(code, readAll(code >= 400 ? c.getErrorStream() : c.getInputStream()));
    }

    public static Response postForm(String url, Map<String, String> form) throws IOException {
        StringBuilder b = new StringBuilder();
        for (Map.Entry<String, String> e : form.entrySet()) {
            if (b.length() > 0) b.append('&');
            b.append(URLEncoder.encode(e.getKey(), "UTF-8")).append('=').append(URLEncoder.encode(e.getValue(), "UTF-8"));
        }
        return post(url, "application/x-www-form-urlencoded", b.toString(), null);
    }

    public static Response postJson(String url, Object json) throws IOException {
        return post(url, "application/json", Json.write(json), null);
    }

    public static Map<String, String> bearer(String token) {
        Map<String, String> h = new LinkedHashMap<>();
        h.put("Authorization", "Bearer " + token);
        return h;
    }

    /** Baixa para um arquivo temporário e move para o destino ao concluir. */
    public static void download(String url, Path dest, LongConsumer bytes) throws IOException {
        Files.createDirectories(dest.getParent());
        Path tmp = dest.resolveSibling(dest.getFileName() + ".part");
        HttpURLConnection c = openFollowing(url, null);
        int code = c.getResponseCode();
        if (code < 200 || code >= 300) throw new IOException("HTTP " + code + " em " + url);
        try (InputStream in = c.getInputStream(); OutputStream out = Files.newOutputStream(tmp)) {
            byte[] buf = new byte[65536];
            int n;
            while ((n = in.read(buf)) > 0) {
                out.write(buf, 0, n);
                if (bytes != null) bytes.accept(n);
            }
        }
        Files.move(tmp, dest, StandardCopyOption.REPLACE_EXISTING);
    }
}
