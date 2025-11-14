package client.ihmMain.utils;

import javafx.scene.control.Label;

public final class UiFormUtils {

    private UiFormUtils() {
        // utilitaire, pas d'instance
    }

    public static void showError(Label errorLabel, String msg) {
        if (errorLabel == null) {
            return;
        }
        if (msg == null || msg.isBlank()) {
            errorLabel.setVisible(false);
            return;
        }
        errorLabel.setText(msg);
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setVisible(true);
    }

    public static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}