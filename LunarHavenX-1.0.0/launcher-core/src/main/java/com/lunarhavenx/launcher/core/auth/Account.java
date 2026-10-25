package com.lunarhavenx.launcher.core.auth;

/** Conta Microsoft/Minecraft autenticada. */
public final class Account {
    public String name;
    public String uuid;
    public String accessToken;      // token do Minecraft (curta duração)
    public long accessExpiresAt;    // epoch em segundos
    public String msRefreshToken;   // renova o login sem pedir o código de novo

    public boolean accessValid() {
        return accessToken != null && System.currentTimeMillis() / 1000 < accessExpiresAt - 60;
    }
}
