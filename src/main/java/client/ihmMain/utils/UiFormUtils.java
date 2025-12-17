package client.ihmMain.utils;

import javafx.scene.control.Label;

/**
 * Classe utilitaire pour les formulaires de l'interface utilisateur.
 * 
 * Cette classe fournit des méthodes statiques utilitaires pour gérer
 * l'affichage des erreurs dans les formulaires et le nettoyage des chaînes
 * de caractères saisies par l'utilisateur.
 * 
 * Cette classe ne peut pas être instanciée (constructeur privé).
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 */
public final class UiFormUtils {

    /**
     * Constructeur privé pour empêcher l'instanciation.
     * 
     * Cette classe est une classe utilitaire et ne doit pas être instanciée.
     */
    private UiFormUtils() {
        // utilitaire, pas d'instance
    }

    /**
     * Affiche un message d'erreur dans un label.
     * 
     * Cette méthode configure un label pour afficher un message d'erreur
     * en rouge. Si le message est null ou vide, le label est masqué.
     * Si le label est null, la méthode retourne sans action.
     * 
     * @param errorLabel Le label dans lequel afficher l'erreur (peut être null)
     * @param msg Le message d'erreur à afficher (null ou vide pour masquer le label)
     */
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

    /**
     * Nettoie une chaîne de caractères en supprimant les espaces.
     * 
     * Cette méthode supprime les espaces en début et fin de chaîne.
     * Si la chaîne est null, une chaîne vide est retournée.
     * 
     * @param s La chaîne à nettoyer (peut être null)
     * @return La chaîne nettoyée, ou une chaîne vide si null
     */
    public static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}