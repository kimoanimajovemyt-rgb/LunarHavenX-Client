package com.lunarhavenx.launcher.core;

/** Callback de progresso. fraction em [0,1], ou -1 para indeterminado. */
public interface Progress {
    void update(String text, double fraction);

    Progress NONE = (t, f) -> { };
}
