package com.lunarhavenx.launcher.desktop;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--version")) {
            System.out.println("LunarHavenX Launcher 1.0.0");
            return;
        }
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> new LauncherFrame().setVisible(true));
    }
}
