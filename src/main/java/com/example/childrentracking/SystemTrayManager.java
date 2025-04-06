package com.example.childrentracking;

import javafx.application.Platform;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class SystemTrayManager {
    private TrayIcon trayIcon;
    private final Stage primaryStage;
    private boolean isTraySupported;

    public SystemTrayManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.isTraySupported = SystemTray.isSupported();
    }

    public void setUp() {
        if (!isTraySupported) {
            return;
        }
        try {
            final PopupMenu popup = new PopupMenu();
            MenuItem openItem = new MenuItem("Mở ứng dụng");
            MenuItem exitItem = new MenuItem("Thoát");
            popup.add(openItem);
            popup.addSeparator();
            popup.add(exitItem);
            URL imageURL = getClass().getResource("Images/appicon.png");
            Image icon = null;
            try {
                if (imageURL != null) {
                    icon = ImageIO.read(imageURL);
                } else {
                    icon = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = ((BufferedImage) icon).createGraphics();
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(0, 0, 16, 16);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(0, 0, 15, 15);
                    g2d.dispose();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            trayIcon = new TrayIcon(icon, "Ứng dụng theo dõi hoạt động", popup);
            trayIcon.setImageAutoSize(true);
            openItem.addActionListener(e -> Platform.runLater(() -> {
                primaryStage.show();
                primaryStage.setIconified(false);
                primaryStage.toFront();
            }));
            exitItem.addActionListener(e -> Platform.runLater(() -> {
                SystemTray.getSystemTray().remove(trayIcon);
                Platform.exit();
                System.exit(0);
            }));
            trayIcon.addActionListener(e -> Platform.runLater(() -> {
                primaryStage.show();
                primaryStage.setIconified(false);
                primaryStage.toFront();
            }));
            SystemTray.getSystemTray().add(trayIcon);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showNotification(String title, String message) {
        if (isTraySupported && trayIcon != null) {
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
        }
    }

    public void minimizeToTray() {
        if (isTraySupported) {
            Platform.runLater(() -> {
                primaryStage.hide();
                showNotification("Ứng dụng đang chạy ngầm", "Ứng dụng đang ghi lại hoạt động của bạn");
            });
        }
    }
}
