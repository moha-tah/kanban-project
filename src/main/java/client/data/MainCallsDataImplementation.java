package client.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

import client.interfaces.MainCallsDataClient;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.User;
import common.dataClasses.SecureUser;
import java.util.List;
import java.util.UUID;
import java.security.MessageDigest;

import static com.sun.javafx.util.Utils.stripQuotes;


public class MainCallsDataImplementation implements MainCallsDataClient {
    private DataClientProvider provider;

    private static final Path USERS_FILE = Path.of("data", "users.json");
    private static final Path USERS_DIR = Path.of("data", "users");
    //Constructeur
    public MainCallsDataImplementation(DataClientProvider provider) {
        this.provider = provider;
    }

    @Override
    public List<LightKanban> getMyListLightKanbans(){
        return provider.getMyModel().getMyLightKanbans();
    }

    @Override
    public LightUser getMyLightUser(){
        return provider.getMyModel().getLocalUser();
    }

    @Override
    public void saveUser(){
        User user = provider.getMyModel().getLocalUser();
        if (user == null) {
            System.err.println("Aucun utilisateur à sauvegarder");
            return;
        }
        
        try {
            // Créer le répertoire users s'il n'existe pas
            if (!Files.exists(USERS_DIR)) {
                Files.createDirectories(USERS_DIR);
            }
            
            // Créer le chemin du fichier pour cet utilisateur
            Path userFile = USERS_DIR.resolve(user.getUsername() + ".json");
            
            // Sérialiser le User en JSON
            String json = serializeUserToJson(user);
            
            // Écrire dans le fichier
            Files.write(userFile, json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde de l'utilisateur: " + e.getMessage());
        }
    }

    @Override
    public boolean authentify(String username, String password) {
        if (username == null || password == null) return false;
        try {
            Map<String, String> users = readJsonToMap(USERS_FILE);
            String storedPassword = users.get(username);
            return storedPassword != null && storedPassword.equals(password);
        } catch (Exception e) {
            return false;
        }
    }

    // Lecture très simple d'un JSON objet plat sans dépendances externes.
    private Map<String, String> readJsonToMap(Path file) throws IOException {
        // Si le fichier file n'existe pas, retourne immédiatement une Map vide
        if (!Files.exists(file)) return new HashMap<>();

        // Lit tous les octets du fichier, les convertit en String UTF-8 et supprime les espaces en début/fin
        String content = new String(Files.readAllBytes(file), StandardCharsets.UTF_8).trim();

        // Crée une HashMap vide pour stocker les paires clé->valeur lues
        Map<String, String> map = new HashMap<>();
        if (content.isEmpty()) return map;

        // Nettoyage du contenu JSON pour enlever les accolades de début et de fin, et le tranmer en une chaîne propre
        if (content.startsWith("{")) content = content.substring(1);
        if (content.endsWith("}")) content = content.substring(0, content.length() - 1);
        content = content.trim();
        if (content.isEmpty()) return map;
        String[] entries = content.split(",");

        // Sépare les entrées sur la virgule (suppose qu'il n'y a pas de virgules échappées dans les valeurs)
        for (String e : entries) {
            // Pour chaque entrée, sépare sur le premier deux-points pour obtenir la clé et la valeur
            String[] kv = e.split(":", 2);
            if (kv.length != 2) continue;
            String k = stripQuotes(kv[0].trim());
            String v = stripQuotes(kv[1].trim());
            map.put(k, v);
        }
        return map;
    }

    // Écriture très simple d'une Map vers un fichier JSON objet plat sans dépendances externes.
    private void writeMapToJson(Path file, Map<String, String> map) throws IOException {
        // Créer le répertoire parent s'il n'existe pas
        if (file.getParent() != null && !Files.exists(file.getParent())) {
            Files.createDirectories(file.getParent());
        }

        // Construire le contenu JSON
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            first = false;
            // Échapper les guillemets dans les clés et valeurs si nécessaire
            json.append("\"").append(entry.getKey().replace("\"", "\\\"")).append("\"");
            json.append(":");
            json.append("\"").append(entry.getValue().replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r")).append("\"");
        }
        json.append("}");

        // Écrire dans le fichier
        Files.write(file, json.toString().getBytes(StandardCharsets.UTF_8));
    }

    // Sérialise un User en JSON
    private String serializeUserToJson(User user) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"id\":\"").append(escapeJson(user.getId().toString())).append("\",");
        json.append("\"username\":\"").append(escapeJson(user.getUsername())).append("\",");
        json.append("\"firstName\":\"").append(escapeJson(user.getFirstName())).append("\",");
        json.append("\"lastName\":\"").append(escapeJson(user.getLastName())).append("\",");
        json.append("\"birthDate\":\"").append(escapeJson(user.getBirthDate().toString())).append("\",");
        json.append("\"avatar\":\"").append(escapeJson(user.getAvatar() != null ? user.getAvatar() : "")).append("\"");
        
        // Si c'est un SecureUser, inclure le hash du mot de passe
        if (user instanceof SecureUser secureUser) {
            json.append(",\"passwordHash\":\"").append(escapeJson(secureUser.getPassword())).append("\"");
        }
        
        json.append("}");
        return json.toString();
    }

    // Désérialise un User depuis JSON
    private User deserializeUserFromJson(String jsonContent) throws IOException {
        // Parser simple du JSON
        Map<String, String> fields = parseJsonObject(jsonContent);
        
        if (fields.isEmpty()) {
            return null;
        }
        
        String idStr = fields.get("id");
        String username = fields.get("username");
        String firstName = fields.get("firstName");
        String lastName = fields.get("lastName");
        String birthDateStr = fields.get("birthDate");
        String avatar = fields.get("avatar");
        String passwordHash = fields.get("passwordHash");
        
        if (username == null || firstName == null || lastName == null || birthDateStr == null) {
            throw new IOException("Champs manquants dans le JSON");
        }
        
        UUID id = idStr != null ? UUID.fromString(idStr) : UUID.randomUUID();
        LocalDate birthDate = LocalDate.parse(birthDateStr);
        
        User user;
        if (passwordHash != null) {
            // Créer un SecureUser (on ne peut pas récupérer le mot de passe original, donc on crée avec un mot de passe temporaire)
            // Le hash sera restauré après
            SecureUser secureUser = new SecureUser(id, username, firstName, lastName, birthDate, "temp");
            // Remplacer le hash par celui stocké
            try {
                java.lang.reflect.Field passwordField = SecureUser.class.getDeclaredField("password");
                passwordField.setAccessible(true);
                passwordField.set(secureUser, passwordHash);
            } catch (Exception e) {
                throw new IOException("Erreur lors de la restauration du hash du mot de passe", e);
            }
            user = secureUser;
        } else {
            user = new User(id, username, firstName, lastName, birthDate);
        }
        
        if (avatar != null && !avatar.isEmpty()) {
            user.setAvatar(avatar);
        }
        
        return user;
    }

    // Charge un User depuis un fichier
    private User loadUser(String username) {
        try {
            Path userFile = USERS_DIR.resolve(username + ".json");
            if (!Files.exists(userFile)) {
                return null;
            }
            
            String content = new String(Files.readAllBytes(userFile), StandardCharsets.UTF_8).trim();
            return deserializeUserFromJson(content);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'utilisateur " + username + ": " + e.getMessage());
            return null;
        }
    }

    // Parse un objet JSON simple en Map
    private Map<String, String> parseJsonObject(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null || json.trim().isEmpty()) {
            return map;
        }
        
        // Enlever les accolades
        String content = json.trim();
        if (content.startsWith("{")) content = content.substring(1);
        if (content.endsWith("}")) content = content.substring(0, content.length() - 1);
        content = content.trim();
        if (content.isEmpty()) return map;
        
        // Parser les champs (gérer les valeurs qui peuvent contenir des virgules)
        int depth = 0;
        StringBuilder current = new StringBuilder();
        List<String> fields = new ArrayList<>();
        
        for (char c : content.toCharArray()) {
            if (c == '{' || c == '[') depth++;
            else if (c == '}' || c == ']') depth--;
            else if (c == ',' && depth == 0) {
                fields.add(current.toString().trim());
                current = new StringBuilder();
                continue;
            }
            current.append(c);
        }
        if (current.length() > 0) {
            fields.add(current.toString().trim());
        }
        
        for (String field : fields) {
            String[] kv = field.split(":", 2);
            if (kv.length == 2) {
                String key = unescapeJson(stripQuotes(kv[0].trim()));
                String value = unescapeJson(stripQuotes(kv[1].trim()));
                map.put(key, value);
            }
        }
        
        return map;
    }

    // Échappe une chaîne pour JSON
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    // Déséchappe une chaîne JSON
    private String unescapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\\"", "\"")
                  .replace("\\\\", "\\")
                  .replace("\\n", "\n")
                  .replace("\\r", "\r")
                  .replace("\\t", "\t");
    }


    @Override
    public void exportProfile(UUID lightUserId, String path){
        throw new UnsupportedOperationException("exportProfile not implemented yet");
    }

    @Override
    public void importMyProfile(String path){
        throw new UnsupportedOperationException("importMyProfile not implemented yet");
    }

    @Override
    public LightUser  sendCreateProfile(String login, String password, String name, String surname,
                                        int age, String avatar, String role, String permissions,
                                        String contacts, String kanbanList, String status) {
        // Créer une LocalDate à partir de l'âge (année de naissance)
        LocalDate birthDate = LocalDate.of(age, 1, 1);
        
        // Créer un SecureUser avec le mot de passe hashé
        SecureUser secureUser = new SecureUser(login, name, surname, birthDate, password);
        
        // Définir l'avatar si fourni (utiliser status si avatar est vide, car SignupController passe imagePath dans status)
        String avatarPath = (avatar != null && !avatar.isEmpty()) ? avatar : status;
        if (avatarPath != null && !avatarPath.isEmpty()) {
            secureUser.setAvatar(avatarPath);
        }
        
        // Stocker le User (SecureUser est un User) dans le modèle
        provider.getMyModel().setLocalUser(secureUser);
        
        // Sauvegarder le hash du mot de passe dans le fichier users.json pour compatibilité
        // (le User complet sera sauvegardé par saveUser())
        try {
            Map<String, String> users = readJsonToMap(USERS_FILE);
            users.put(login, secureUser.getPassword()); // Stocker le hash, pas le mot de passe en clair
            writeMapToJson(USERS_FILE, users);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde du hash du mot de passe: " + e.getMessage());
        }
        
        // Retourner le LightUser
        return secureUser;
    }



    //getters
    public DataClientProvider getProvider() {
        return this.provider;
    }
    //setters
    public void setProvider(DataClientProvider provider) {
        this.provider = provider;
    }

}