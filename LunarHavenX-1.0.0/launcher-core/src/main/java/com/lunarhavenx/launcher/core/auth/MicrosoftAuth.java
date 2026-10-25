package com.lunarhavenx.launcher.core.auth;

import com.lunarhavenx.launcher.core.Http;
import com.lunarhavenx.launcher.core.Json;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Login oficial Microsoft → Xbox Live → XSTS → Minecraft Services, usando o fluxo
 * "device code" (o usuário digita um código no site da Microsoft; o launcher nunca vê a senha).
 *
 * IMPORTANTE: é preciso um Application (client) ID registrado no Azure (cliente público,
 * "allow public client flows") E aprovado pela Mojang para a API do Minecraft; sem isso a
 * etapa login_with_xbox responde 403. Veja docs/AUTENTICACAO.md.
 */
public final class MicrosoftAuth {
    private static final String SCOPE = "XboxLive.signin offline_access";
    private static final String TENANT = "https://login.microsoftonline.com/consumers/oauth2/v2.0/";

    private final String clientId;

    public MicrosoftAuth(String clientId) { this.clientId = clientId == null ? "" : clientId.trim(); }

    public boolean isConfigured() { return !clientId.isEmpty(); }

    public static final class DeviceCode {
        public String deviceCode, userCode, verificationUri;
        public int intervalSec, expiresInSec;
    }

    public static final class AuthException extends IOException {
        public AuthException(String m) { super(m); }
    }

    public DeviceCode requestDeviceCode() throws IOException {
        requireConfigured();
        Map<String, String> f = new LinkedHashMap<>();
        f.put("client_id", clientId);
        f.put("scope", SCOPE);
        Http.Response r = Http.postForm(TENANT + "devicecode", f);
        if (!r.ok()) throw new AuthException("Falha ao iniciar login: " + describe(r));
        Object j = Json.parse(r.body);
        DeviceCode d = new DeviceCode();
        d.deviceCode = Json.str(j, "device_code");
        d.userCode = Json.str(j, "user_code");
        d.verificationUri = Json.str(j, "verification_uri");
        d.intervalSec = (int) Json.num(j, 5, "interval");
        d.expiresInSec = (int) Json.num(j, 900, "expires_in");
        return d;
    }

    /** Aguarda o usuário concluir o login no navegador e completa a cadeia até o perfil do Minecraft. */
    public Account completeLogin(DeviceCode d, java.util.function.BooleanSupplier cancelled) throws IOException {
        long deadline = System.currentTimeMillis() + d.expiresInSec * 1000L;
        int interval = Math.max(d.intervalSec, 1);
        while (System.currentTimeMillis() < deadline) {
            if (cancelled != null && cancelled.getAsBoolean()) throw new AuthException("Login cancelado.");
            try { Thread.sleep(interval * 1000L); } catch (InterruptedException e) { throw new AuthException("Login interrompido."); }
            Map<String, String> f = new LinkedHashMap<>();
            f.put("grant_type", "urn:ietf:params:oauth:grant-type:device_code");
            f.put("client_id", clientId);
            f.put("device_code", d.deviceCode);
            Http.Response r = Http.postForm(TENANT + "token", f);
            if (r.ok()) {
                Object j = Json.parse(r.body);
                return finishChain(Json.str(j, "access_token"), Json.str(j, "refresh_token"));
            }
            String err = Json.str(Json.parse(r.body), "error");
            if ("authorization_pending".equals(err)) continue;
            if ("slow_down".equals(err)) { interval += 5; continue; }
            throw new AuthException("Login recusado: " + describe(r));
        }
        throw new AuthException("O código expirou. Tente entrar novamente.");
    }

    /** Renova usando o refresh token salvo. */
    public Account refresh(Account a) throws IOException {
        requireConfigured();
        Map<String, String> f = new LinkedHashMap<>();
        f.put("grant_type", "refresh_token");
        f.put("client_id", clientId);
        f.put("refresh_token", a.msRefreshToken);
        f.put("scope", SCOPE);
        Http.Response r = Http.postForm(TENANT + "token", f);
        if (!r.ok()) throw new AuthException("Sessão expirada, entre novamente (" + describe(r) + ")");
        Object j = Json.parse(r.body);
        String newRefresh = Json.str(j, "refresh_token");
        return finishChain(Json.str(j, "access_token"), newRefresh != null ? newRefresh : a.msRefreshToken);
    }

    private Account finishChain(String msAccess, String msRefresh) throws IOException {
        // 1) Xbox Live
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("AuthMethod", "RPS");
        props.put("SiteName", "user.auth.xboxlive.com");
        props.put("RpsTicket", "d=" + msAccess);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("Properties", props);
        body.put("RelyingParty", "http://auth.xboxlive.com");
        body.put("TokenType", "JWT");
        Http.Response xbl = Http.postJson("https://user.auth.xboxlive.com/user/authenticate", body);
        if (!xbl.ok()) throw new AuthException("Falha no Xbox Live: " + describe(xbl));
        Object xj = Json.parse(xbl.body);
        String xblToken = Json.str(xj, "Token");
        String uhs = Json.str(Json.list(Json.path(xj, "DisplayClaims", "xui")).get(0), "uhs");

        // 2) XSTS
        List<Object> tokens = new ArrayList<>();
        tokens.add(xblToken);
        Map<String, Object> p2 = new LinkedHashMap<>();
        p2.put("SandboxId", "RETAIL");
        p2.put("UserTokens", tokens);
        Map<String, Object> b2 = new LinkedHashMap<>();
        b2.put("Properties", p2);
        b2.put("RelyingParty", "rp://api.minecraftservices.com/");
        b2.put("TokenType", "JWT");
        Http.Response xsts = Http.postJson("https://xsts.auth.xboxlive.com/xsts/authorize", b2);
        if (!xsts.ok()) throw new AuthException(xstsMessage(xsts));
        String xstsToken = Json.str(Json.parse(xsts.body), "Token");

        // 3) Minecraft Services
        Map<String, Object> b3 = new LinkedHashMap<>();
        b3.put("identityToken", "XBL3.0 x=" + uhs + ";" + xstsToken);
        Http.Response mc = Http.postJson("https://api.minecraftservices.com/authentication/login_with_xbox", b3);
        if (mc.code == 403) throw new AuthException("A Mojang recusou o Application ID (403). O ID precisa ser aprovado para a API do Minecraft — veja docs/AUTENTICACAO.md.");
        if (!mc.ok()) throw new AuthException("Falha no login do Minecraft: " + describe(mc));
        Object mj = Json.parse(mc.body);
        String mcToken = Json.str(mj, "access_token");
        long expires = Json.num(mj, 86400, "expires_in");

        // 4) Perfil (também confirma que a conta possui o jogo)
        Http.Response prof = Http.getRaw("https://api.minecraftservices.com/minecraft/profile", Http.bearer(mcToken));
        if (prof.code == 404) throw new AuthException("Esta conta Microsoft não possui Minecraft Java Edition.");
        if (!prof.ok()) throw new AuthException("Falha ao ler o perfil: " + describe(prof));
        Object pj = Json.parse(prof.body);

        Account a = new Account();
        a.name = Json.str(pj, "name");
        a.uuid = Json.str(pj, "id");
        a.accessToken = mcToken;
        a.accessExpiresAt = System.currentTimeMillis() / 1000 + expires;
        a.msRefreshToken = msRefresh;
        return a;
    }

    static String xstsMessage(Http.Response r) {
        try {
            long x = Json.num(Json.parse(r.body), 0, "XErr");
            if (x == 2148916233L) return "Esta conta Microsoft não tem perfil Xbox. Crie um em xbox.com e tente de novo.";
            if (x == 2148916235L) return "O Xbox Live não está disponível no seu país.";
            if (x == 2148916236L || x == 2148916237L) return "A conta precisa de verificação de adulto no Xbox.";
            if (x == 2148916238L) return "Conta de menor de idade: precisa ser adicionada a uma família Microsoft.";
        } catch (Exception ignored) { }
        return "Falha no XSTS: " + describe(r);
    }

    private static String describe(Http.Response r) {
        String b = r.body == null ? "" : r.body;
        if (b.length() > 200) b = b.substring(0, 200);
        return "HTTP " + r.code + " " + b;
    }

    private void requireConfigured() throws AuthException {
        if (!isConfigured()) throw new AuthException("Application ID da Microsoft não configurado (veja docs/AUTENTICACAO.md).");
    }
}
