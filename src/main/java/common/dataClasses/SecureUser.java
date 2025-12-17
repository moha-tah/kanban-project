package common.dataClasses;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.time.LocalDate;
import java.util.UUID;

public class SecureUser extends User {
    private String password; // Mot de passe hashé

    // Constructeur
    public SecureUser(String username, String firstName, String lastName,
                      LocalDate birthDate, String password) {
        super(username, firstName, lastName, birthDate);
        this.password = hashPassword(password);
    }

    // Constructeur avec ID
    public SecureUser(UUID id, String username, String firstName, String lastName,
                      LocalDate birthDate, String password) {
        super(id, username, firstName, lastName, birthDate);
        this.password = hashPassword(password);
    }

    // Getters
    public String getPassword() {
        return password; // Retourne le hash, jamais le mot de passe en clair
    }

    // Setters
    public void setPassword(String newPassword) {
        this.password = hashPassword(newPassword);
    }

    // Méthode pour vérifier un mot de passe
    public boolean verifyPassword(String inputPassword) {

        // Extraire le salt du hash stocké et utiliser le même salt pour vérifier
        try {
            byte[] combined = Base64.getDecoder().decode(this.password);
            byte[] salt = new byte[16];
            System.arraycopy(combined, 0, salt, 0, 16);

            // Hasher le mot de passe fourni avec le même salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedInput = md.digest(inputPassword.getBytes(StandardCharsets.UTF_8));

            // Comparer avec le hash stocké (sans le salt)
            for (int i = 0; i < hashedInput.length; i++) {
                if (hashedInput[i] != combined[i + 16]) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Méthode privée pour hasher un mot de passe
    private String hashPassword(String password) {
        try {
            // Utilisation de SHA-256 avec un salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());

            // Combiner le salt et le hash
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hachage du mot de passe", e);
        }
    }

    // Méthode pour changer le mot de passe avec vérification de l'ancien
    public boolean changePassword(String oldPassword, String newPassword) {
        if (verifyPassword(oldPassword)) {
            setPassword(newPassword);
            return true;
        }
        return false;
    }
}
