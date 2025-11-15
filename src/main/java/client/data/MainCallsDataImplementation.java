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



public class MainCallsDataImplementation implements MainCallsDataClient {
    private DataClientProvider provider;

    private static final Path USERS_FILE = Path.of("data", "users.json");
    private static final Path USERS_DIR = Path.of("data", "users");

    private static String stripQuotes(String s) {
        if (s == null) {
            return null;
        }
        String trimmed = s.trim();
        int len = trimmed.length();
        if (len >= 2 && trimmed.charAt(0) == '"' && trimmed.charAt(len - 1) == '"') {
            return trimmed.substring(1, len - 1);
        }
        return trimmed;
    }
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
            System.err.println("Aucun utilisateur a sauvegarder");
            return;
        }

        try {
            if (!Files.exists(USERS_DIR)) {
                Files.createDirectories(USERS_DIR);
            }

            Path userFile = USERS_DIR.resolve(user.getUsername() + ".json");

            String json = serializeUserToJson(user);

            Files.write(userFile, json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde de l'utilisateur: " + e.getMessage());
        }
    }

    @Override
    public boolean authentify(String username, String password) {
        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            return false;
        }

        try {
            User user = loadUser(username);
            if (user == null) {
                return false;
            }

            if (!(user instanceof SecureUser secureUser)) {
                System.err.println("L'utilisateur " + username + " n'est pas un SecureUser");
                return false;
            }

            boolean isValid = secureUser.verifyPassword(password);

            if (isValid) {
                provider.getMyModel().setLocalUser(user);
            }

            return isValid;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'authentification de " + username + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Map<String, String> readJsonToMap(Path file) throws IOException {
        if (!Files.exists(file)) return new HashMap<>();

        String content = new String(Files.readAllBytes(file), StandardCharsets.UTF_8).trim();

        Map<String, String> map = new HashMap<>();
        if (content.isEmpty()) return map;

        if (content.startsWith("{")) content = content.substring(1);
        if (content.endsWith("}")) content = content.substring(0, content.length() - 1);
        content = content.trim();
        if (content.isEmpty()) return map;
        String[] entries = content.split(",");

        for (String e : entries) {
            String[] kv = e.split(":", 2);
            if (kv.length != 2) continue;
            String k = stripQuotes(kv[0].trim());
            String v = stripQuotes(kv[1].trim());
            map.put(k, v);
        }
        return map;
    }


    private void writeMapToJson(Path file, Map<String, String> map) throws IOException {
        if (file.getParent() != null && !Files.exists(file.getParent())) {
            Files.createDirectories(file.getParent());
        }

        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            first = false;
            json.append("\"").append(entry.getKey().replace("\"", "\\\"")).append("\"");
            json.append(":");
            json.append("\"").append(entry.getValue().replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r")).append("\"");
        }
        json.append("}");

        Files.write(file, json.toString().getBytes(StandardCharsets.UTF_8));
    }


    private String serializeUserToJson(User user) {
        StringBuilder json = new StringBuilder("{");
        json.append("\"id\":\"").append(escapeJson(user.getId().toString())).append("\",");
        json.append("\"username\":\"").append(escapeJson(user.getUsername())).append("\",");
        json.append("\"firstName\":\"").append(escapeJson(user.getFirstName())).append("\",");
        json.append("\"lastName\":\"").append(escapeJson(user.getLastName())).append("\",");
        json.append("\"birthDate\":\"").append(escapeJson(user.getBirthDate().toString())).append("\",");
        json.append("\"avatar\":\"").append(escapeJson(user.getAvatar() != null ? user.getAvatar() : "")).append("\"");

        if (user instanceof SecureUser secureUser) {
            json.append(",\"passwordHash\":\"").append(escapeJson(secureUser.getPassword())).append("\"");
        }

        json.append("}");
        return json.toString();
    }


    private User deserializeUserFromJson(String jsonContent) throws IOException {
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

            SecureUser secureUser = new SecureUser(id, username, firstName, lastName, birthDate, "temp");
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


    private Map<String, String> parseJsonObject(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null || json.trim().isEmpty()) {
            return map;
        }

        String content = json.trim();
        if (content.startsWith("{")) content = content.substring(1);
        if (content.endsWith("}")) content = content.substring(0, content.length() - 1);
        content = content.trim();
        if (content.isEmpty()) return map;

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


    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }


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
        LocalDate birthDate = LocalDate.of(age, 1, 1);

        SecureUser secureUser = new SecureUser(login, name, surname, birthDate, password);

        String avatarPath = (avatar != null && !avatar.isEmpty()) ? avatar : status;
        if (avatarPath != null && !avatarPath.isEmpty()) {
            secureUser.setAvatar(avatarPath);
        }

        provider.getMyModel().setLocalUser(secureUser);


        try {
            Map<String, String> users = readJsonToMap(USERS_FILE);
            users.put(login, secureUser.getPassword());
            writeMapToJson(USERS_FILE, users);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde du hash du mot de passe: " + e.getMessage());
        }

        return secureUser;
    }



    public DataClientProvider getProvider() {
        return this.provider;
    }
    public void setProvider(DataClientProvider provider) {
        this.provider = provider;
    }

}