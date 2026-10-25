package com.lunarhavenx.launcher.desktop;

import com.lunarhavenx.launcher.core.LaunchService;
import com.lunarhavenx.launcher.core.LauncherConfig;
import com.lunarhavenx.launcher.core.Platform;
import com.lunarhavenx.launcher.core.auth.Account;
import com.lunarhavenx.launcher.core.auth.AccountStore;
import com.lunarhavenx.launcher.core.auth.MicrosoftAuth;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
import javax.swing.*;

/** Janela principal do launcher LunarHavenX (tema lunar, baseado na arte do cliente). */
public final class LauncherFrame extends JFrame {
    private static final Color CYAN = new Color(0x2E, 0xC5, 0xFF);
    private static final Color PURPLE = new Color(0x7C, 0x4D, 0xFF);
    private static final Color PANEL = new Color(7, 11, 32, 205);

    private final Path root = Platform.dataDir();
    private final LauncherConfig config = LauncherConfig.load(root);
    private final AccountStore store = new AccountStore(root);
    private Account account = store.load();
    private final BufferedImage bg = loadImage("/assets/launcher_bg.jpg");

    private final JLabel accountLabel = label("", 14, false);
    private final GlowButton loginButton = new GlowButton("ENTRAR COM MICROSOFT", false);
    private final JComboBox<String> versionBox = new JComboBox<>(new String[]{"1.8.9 + Forge + LunarHavenX Client", "1.8.9 Vanilla (sem mod)"});
    private final JSlider ramSlider = new JSlider(2, 16, Math.max(2, Math.min(16, config.ramMb / 512)));
    private final JLabel ramLabel = label("", 13, false);
    private final GlowButton playButton = new GlowButton("JOGAR", true);
    private final JProgressBar bar = new JProgressBar(0, 1000);
    private final JLabel status = label("Pronto.", 13, false);
    private final JTextArea log = new JTextArea();

    public LauncherFrame() {
        super("LunarHavenX Launcher 1.0.0");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        BufferedImage icon = loadImage("/assets/icon.png");
        if (icon != null) setIconImage(icon);
        setContentPane(new Background());
        getContentPane().setLayout(null);
        setSize(1100, 690);
        setResizable(false);
        setLocationRelativeTo(null);
        build();
        refreshAccountLabel();
        log("Pasta de dados: " + root);
        log("Java 8 é necessário para o Minecraft 1.8.9; se não houver um instalado, o launcher baixa o Temurin 8.");
    }

    private void build() {
        JLabel title = label("LUNARHAVENX", 34, true);
        title.setForeground(Color.WHITE);
        title.setBounds(52, 22, 400, 44);
        add(title);
        JLabel sub = label("C L I E N T   ·   Launcher 1.0.0", 13, false);
        sub.setForeground(CYAN);
        sub.setBounds(54, 64, 400, 20);
        add(sub);

        add(sectionTitle("CONTA", 118));
        accountLabel.setBounds(52, 142, 300, 22);
        add(accountLabel);
        loginButton.setBounds(52, 170, 300, 38);
        loginButton.addActionListener(e -> onLoginButton());
        add(loginButton);

        add(sectionTitle("VERSÃO", 232));
        versionBox.setBounds(52, 256, 300, 30);
        versionBox.setSelectedIndex(config.useForge ? 0 : 1);
        add(versionBox);

        add(sectionTitle("MEMÓRIA (RAM)", 304));
        ramSlider.setBounds(48, 328, 308, 34);
        ramSlider.setOpaque(false);
        ramSlider.addChangeListener(e -> updateRamLabel());
        add(ramSlider);
        ramLabel.setBounds(52, 360, 300, 20);
        add(ramLabel);
        updateRamLabel();

        playButton.setBounds(52, 410, 300, 60);
        playButton.addActionListener(e -> onPlay());
        add(playButton);

        bar.setBounds(52, 490, 300, 14);
        bar.setForeground(CYAN);
        bar.setBackground(new Color(20, 28, 60));
        bar.setBorderPainted(false);
        add(bar);
        status.setBounds(52, 510, 300, 20);
        add(status);

        log.setEditable(false);
        log.setLineWrap(true);
        log.setForeground(new Color(190, 215, 255));
        log.setBackground(new Color(5, 8, 24, 215));
        log.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane sp = new JScrollPane(log);
        sp.setBounds(400, 470, 650, 170);
        sp.setBorder(BorderFactory.createLineBorder(new Color(46, 197, 255, 120)));
        add(sp);
    }

    // ---------------------------------------------------------------- conta
    private MicrosoftAuth auth() { return new MicrosoftAuth(config.msClientId); }

    private void refreshAccountLabel() {
        if (account == null) {
            accountLabel.setText("Não conectado");
            loginButton.setText("ENTRAR COM MICROSOFT");
        } else {
            accountLabel.setText("Jogador: " + account.name);
            loginButton.setText("SAIR DA CONTA");
        }
    }

    private void onLoginButton() {
        if (account != null) {
            try { store.delete(); } catch (IOException ignored) { }
            account = null;
            refreshAccountLabel();
            return;
        }
        if (!auth().isConfigured()) {
            String id = JOptionPane.showInputDialog(this,
                    "Informe o Application (client) ID do seu app Azure aprovado pela Mojang.\n"
                            + "Veja docs/AUTENTICACAO.md. (Também pode usar a variável LHX_MS_CLIENT_ID.)",
                    "Application ID da Microsoft", JOptionPane.QUESTION_MESSAGE);
            if (id == null || id.trim().isEmpty()) return;
            config.msClientId = id.trim();
            try { config.save(); } catch (IOException e) { log("Não foi possível salvar a configuração: " + e.getMessage()); }
        }
        loginButton.setEnabled(false);
        final AtomicBoolean cancelled = new AtomicBoolean(false);
        new Thread(() -> {
            JDialog[] dlg = new JDialog[1];
            try {
                MicrosoftAuth a = auth();
                MicrosoftAuth.DeviceCode dc = a.requestDeviceCode();
                SwingUtilities.invokeLater(() -> dlg[0] = showCodeDialog(dc, cancelled));
                try { Desktop.getDesktop().browse(new URI(dc.verificationUri)); } catch (Exception ignored) { }
                Account acc = a.completeLogin(dc, cancelled::get);
                store.save(acc);
                account = acc;
                log("Login concluído como " + acc.name);
            } catch (Exception e) {
                log("Login falhou: " + e.getMessage());
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, e.getMessage(), "Login", JOptionPane.WARNING_MESSAGE));
            } finally {
                SwingUtilities.invokeLater(() -> {
                    if (dlg[0] != null) dlg[0].dispose();
                    loginButton.setEnabled(true);
                    refreshAccountLabel();
                });
            }
        }, "ms-login").start();
    }

    private JDialog showCodeDialog(MicrosoftAuth.DeviceCode dc, AtomicBoolean cancelled) {
        JDialog d = new JDialog(this, "Entrar com Microsoft", false);
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        JLabel code = new JLabel(dc.userCode, SwingConstants.CENTER);
        code.setFont(new Font(Font.MONOSPACED, Font.BOLD, 34));
        p.add(new JLabel("<html>Abra <b>" + dc.verificationUri + "</b> e digite este código:</html>"), BorderLayout.NORTH);
        p.add(code, BorderLayout.CENTER);
        JPanel b = new JPanel();
        JButton copy = new JButton("Copiar código");
        copy.addActionListener(e -> Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(dc.userCode), null));
        JButton cancel = new JButton("Cancelar");
        cancel.addActionListener(e -> { cancelled.set(true); d.dispose(); });
        b.add(copy);
        b.add(cancel);
        p.add(b, BorderLayout.SOUTH);
        d.setContentPane(p);
        d.pack();
        d.setLocationRelativeTo(this);
        d.setVisible(true);
        return d;
    }

    // ---------------------------------------------------------------- jogar
    private void onPlay() {
        if (account == null) {
            JOptionPane.showMessageDialog(this, "Entre com sua conta Microsoft primeiro.", "Conta necessária", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        config.useForge = versionBox.getSelectedIndex() == 0;
        config.ramMb = ramSlider.getValue() * 512;
        try { config.save(); } catch (IOException ignored) { }
        playButton.setEnabled(false);
        new Thread(() -> {
            try {
                if (!account.accessValid()) {
                    setStatus("Renovando sessão…", -1);
                    account = auth().refresh(account);
                    store.save(account);
                }
                Process p = new LaunchService(root, config).prepareAndLaunch(account, this::setStatus, this::log);
                setStatus("Minecraft em execução.", 1.0);
                int code = p.waitFor();
                log("Minecraft encerrou com código " + code);
                setStatus(code == 0 ? "Pronto." : "O jogo terminou com erro (veja o log).", 0);
            } catch (Exception e) {
                log("ERRO: " + e.getMessage());
                setStatus("Falhou: " + e.getMessage(), 0);
            } finally {
                SwingUtilities.invokeLater(() -> playButton.setEnabled(true));
            }
        }, "launch").start();
    }

    // ---------------------------------------------------------------- util UI
    private void setStatus(String text, double fraction) {
        SwingUtilities.invokeLater(() -> {
            status.setText(text.length() > 46 ? text.substring(0, 45) + "…" : text);
            status.setToolTipText(text);
            bar.setIndeterminate(fraction < 0);
            if (fraction >= 0) bar.setValue((int) (fraction * 1000));
        });
    }

    private void log(String line) {
        SwingUtilities.invokeLater(() -> {
            log.append(line + "\n");
            if (log.getDocument().getLength() > 200_000) {
                try { log.getDocument().remove(0, 50_000); } catch (Exception ignored) { }
            }
            log.setCaretPosition(log.getDocument().getLength());
        });
    }

    private void updateRamLabel() {
        int mb = ramSlider.getValue() * 512;
        ramLabel.setText(String.format("%.1f GB alocados ao jogo", mb / 1024.0));
    }

    private static JLabel label(String t, int size, boolean bold) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, size));
        l.setForeground(new Color(220, 232, 255));
        return l;
    }

    private static JLabel sectionTitle(String t, int y) {
        JLabel l = label(t, 11, true);
        l.setForeground(CYAN);
        l.setBounds(52, y, 300, 18);
        return l;
    }

    private static BufferedImage loadImage(String res) {
        try { return ImageIO.read(LauncherFrame.class.getResource(res)); } catch (Exception e) { return null; }
    }

    private final class Background extends JPanel {
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            int w = getWidth(), h = getHeight();
            g.setColor(new Color(5, 8, 24));
            g.fillRect(0, 0, w, h);
            if (bg != null) {
                double s = Math.max(w / (double) bg.getWidth(), h / (double) bg.getHeight());
                int iw = (int) (bg.getWidth() * s), ih = (int) (bg.getHeight() * s);
                g.drawImage(bg, (w - iw) / 2, (h - ih) / 2, iw, ih, null);
            }
            g.setPaint(new GradientPaint(0, 0, new Color(4, 6, 20, 170), 0, h, new Color(4, 6, 20, 90)));
            g.fillRect(0, 0, w, h);
            g.setColor(PANEL);
            g.fillRoundRect(28, 100, 344, 450, 24, 24);
            g.setColor(new Color(46, 197, 255, 110));
            g.drawRoundRect(28, 100, 344, 450, 24, 24);
            g.dispose();
        }
    }

    private static final class GlowButton extends JButton {
        private final boolean primary;
        GlowButton(String t, boolean primary) {
            super(t);
            this.primary = primary;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, primary ? 24 : 13));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color a = isEnabled() ? (primary ? CYAN : new Color(30, 50, 110)) : new Color(60, 66, 90);
            Color b = isEnabled() ? (primary ? PURPLE : new Color(60, 40, 130)) : new Color(45, 50, 72);
            if (getModel().isRollover() && isEnabled()) { a = a.brighter(); b = b.brighter(); }
            g.setPaint(new GradientPaint(0, 0, a, getWidth(), getHeight(), b));
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g.dispose();
            super.paintComponent(g0);
        }
    }
}
