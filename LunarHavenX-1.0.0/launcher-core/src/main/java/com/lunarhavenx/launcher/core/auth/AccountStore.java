package com.lunarhavenx.launcher.core.auth;

import com.lunarhavenx.launcher.core.Json;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Guarda apenas tokens (nunca senha). O refresh token é uma credencial: o arquivo recebe
 * permissão 600 em sistemas POSIX. No Windows ele fica no perfil do usuário (APPDATA);
 * migrar para o Windows Credential Manager/DPAPI está no plano de evolução.
 */
public final class AccountStore {
    private final Path file;

    public AccountStore(Path root) { this.file = root.resolve("account.json"); }

    public Account load() {
        try {
            if (!Files.exists(file)) return null;
            Map<String, Object> m = Json.obj(Json.parse(new String(Files.readAllBytes(file), StandardCharsets.UTF_8)));
            if (m == null || m.get("msRefreshToken") == null) return null;
            Account a = new Account();
            a.name = Json.str(m, "name");
            a.uuid = Json.str(m, "uuid");
            a.accessToken = Json.str(m, "accessToken");
            a.accessExpiresAt = Json.num(m, 0, "accessExpiresAt");
            a.msRefreshToken = Json.str(m, "msRefreshToken");
            return a;
        } catch (Exception e) {
            return null;
        }
    }

    public void save(Account a) throws IOException {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("name", a.name);
        m.put("uuid", a.uuid);
        m.put("accessToken", a.accessToken);
        m.put("accessExpiresAt", a.accessExpiresAt);
        m.put("msRefreshToken", a.msRefreshToken);
        Files.createDirectories(file.getParent());
        Files.write(file, Json.write(m).getBytes(StandardCharsets.UTF_8));
        try { Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("rw-------")); } catch (Exception ignored) { }
    }

    public void delete() throws IOException { Files.deleteIfExists(file); }
}
