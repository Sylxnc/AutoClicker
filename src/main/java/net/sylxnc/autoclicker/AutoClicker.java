package net.sylxnc.autoclicker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.SpinnerNumberModel;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class AutoClicker implements NativeKeyListener {

    private static final Color BACKGROUND = new Color(25, 28, 36);
    private static final Color PANEL = new Color(35, 39, 50);
    private static final Color TEXT = new Color(235, 238, 245);
    private static final Color MUTED = new Color(157, 165, 180);
    private static final Color ACCENT = new Color(91, 141, 239);

    private final Robot robot;
    private final AtomicBoolean clicking = new AtomicBoolean();
    private volatile boolean running = true;
    private volatile int interval = 100;
    private JFrame frame;
    private JLabel stateLabel;
    private JLabel rateLabel;
    private JSpinner intervalSpinner;
    private JButton toggleButton;

    public AutoClicker() throws java.awt.AWTException {
        robot = new Robot();
    }

    public void start() {
        Thread clickThread = new Thread(() -> {
            while (running) {
                if (clicking.get()) {
                    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                }
                try {
                    Thread.sleep(clicking.get() ? interval : 50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }, "auto-clicker-worker");

        clickThread.setDaemon(true);
        clickThread.start();
    }

    private void createAndShowUi() {
        frame = new JFrame("AutoClicker");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setMinimumSize(new Dimension(430, 330));
        frame.setSize(480, 360);
        frame.setLocationByPlatform(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutdown();
            }
        });

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JLabel title = new JLabel("AutoClicker");
        title.setForeground(TEXT);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 27));
        JLabel subtitle = new JLabel("Schnelle Klicks. Volle Kontrolle.");
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.add(title);
        heading.add(subtitle);
        root.add(heading, BorderLayout.NORTH);

        JPanel settings = new JPanel(new GridBagLayout());
        settings.setBackground(PANEL);
        settings.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(7, 0, 7, 12);
        c.anchor = GridBagConstraints.WEST;

        JLabel intervalLabel = new JLabel("Klickintervall");
        intervalLabel.setForeground(TEXT);
        intervalLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        c.gridx = 0;
        c.gridy = 0;
        settings.add(intervalLabel, c);

        intervalSpinner = new JSpinner(new SpinnerNumberModel(interval, 10, 60000, 10));
        intervalSpinner.setPreferredSize(new Dimension(100, 31));
        intervalSpinner.addChangeListener(e -> {
            interval = (Integer) intervalSpinner.getValue();
            updateRateLabel();
        });
        c.gridx = 1;
        c.weightx = 1;
        settings.add(intervalSpinner, c);

        JLabel milliseconds = new JLabel("Millisekunden");
        milliseconds.setForeground(MUTED);
        c.gridx = 2;
        c.weightx = 0;
        c.insets = new Insets(7, 0, 7, 0);
        settings.add(milliseconds, c);
        root.add(settings, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout(0, 13));
        footer.setOpaque(false);
        JPanel status = new JPanel(new BorderLayout());
        status.setOpaque(false);
        stateLabel = new JLabel("●  Bereit");
        stateLabel.setForeground(MUTED);
        rateLabel = new JLabel();
        rateLabel.setForeground(MUTED);
        rateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        status.add(stateLabel, BorderLayout.WEST);
        status.add(rateLabel, BorderLayout.EAST);
        footer.add(status, BorderLayout.NORTH);

        toggleButton = new JButton("Starten (F4)");
        toggleButton.setFocusPainted(false);
        toggleButton.setForeground(Color.WHITE);
        toggleButton.setBackground(ACCENT);
        toggleButton.setBorder(BorderFactory.createEmptyBorder(11, 16, 11, 16));
        toggleButton.addActionListener(e -> setClicking(!clicking.get()));
        footer.add(toggleButton, BorderLayout.CENTER);

        JLabel hint = new JLabel("F4 Start / Stopp    •    F5 Beenden");
        hint.setForeground(MUTED);
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        hint.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        footer.add(hint, BorderLayout.SOUTH);
        root.add(footer, BorderLayout.SOUTH);

        frame.setContentPane(root);
        updateRateLabel();
        frame.setVisible(true);
    }

    private void updateRateLabel() {
        if (rateLabel != null) {
            rateLabel.setText(String.format("≈ %.1f Klicks/Sekunde", 1000.0 / interval));
        }
    }

    private void setClicking(boolean enabled) {
        clicking.set(enabled);
        if (stateLabel != null) {
            stateLabel.setText(enabled ? "●  Läuft" : "●  Bereit");
            stateLabel.setForeground(enabled ? new Color(93, 211, 137) : MUTED);
        }
        if (toggleButton != null) {
            toggleButton.setText(enabled ? "Stoppen (F4)" : "Starten (F4)");
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_F4) {
            SwingUtilities.invokeLater(() -> setClicking(!clicking.get()));
        }

        if (e.getKeyCode() == NativeKeyEvent.VC_F5) {
            SwingUtilities.invokeLater(this::shutdown);
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
    }

    private void shutdown() {
        running = false;
        setClicking(false);
        if (GlobalScreen.isNativeHookRegistered()) {
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException e) {
                System.err.println("Globaler Hotkey konnte nicht abgemeldet werden: " + e.getMessage());
            }
        }
        if (frame != null) {
            frame.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                AutoClicker autoClicker = new AutoClicker();
                autoClicker.createAndShowUi();
                autoClicker.start();
                try {
                    GlobalScreen.registerNativeHook();
                    GlobalScreen.addNativeKeyListener(autoClicker);
                } catch (NativeHookException e) {
                    System.err.println("Globale Hotkeys sind nicht verfügbar: " + e.getMessage());
                }
            } catch (java.awt.AWTException | ClassNotFoundException | InstantiationException
                     | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException e) {
                throw new IllegalStateException("AutoClicker konnte nicht gestartet werden.", e);
            }
        });
    }
}
