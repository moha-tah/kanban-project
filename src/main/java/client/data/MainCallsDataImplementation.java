package client.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import client.interfaces.MainCallsDataClient;
import common.dataClasses.Access;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.SecureUser;
import common.dataClasses.User;

public class MainCallsDataImplementation implements MainCallsDataClient {
    private DataClientProvider provider;
    private static final java.util.logging.Logger LOGGER =
        java.util.logging.Logger.getLogger(MainCallsDataImplementation.class.getName());


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
        return provider.getMyModel().getAvailableLightKanbans();
    }

    @Override
    public LightUser getMyLightUser(){
        return provider.getMyModel().getLocalUser();
    }

    @Override
    public void saveUser(){
        User user = provider.getMyModel().getLocalUser();
        if (user == null) {
            LOGGER.warning("Aucun utilisateur à sauvegarder");
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
        LOGGER.log(java.util.logging.Level.SEVERE, "Erreur lors de la sauvegarde de l'utilisateur", e);
        }
    }

    @Override
    public boolean authentify(String username, String password) {
        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            return false;
        }

        try {
            // 1. Charge l'utilisateur et ses Kanbans depuis le disque
            User user = loadUser(username);
            if (user == null) {
                return false;
            }

            if (!(user instanceof SecureUser secureUser)) {
                LOGGER.warning("L'utilisateur " + username + " n'est pas un SecureUser");
                return false;
            }

            boolean isValid = secureUser.verifyPassword(password);

            if (isValid) {
                // 2. Met à jour l'utilisateur dans le modèle
                provider.getMyModel().setLocalUser(user);

                // 3. AJOUT CRITIQUE : Met à jour la liste des LightKanbans dans le modèle
                // Cela permet au LoginController de récupérer la liste via getMyListLightKanbans()
                List<LightKanban> lights = new ArrayList<>();
                if (user.getMyKanban() != null) {
                    for (Kanban k : user.getMyKanban()) {
                        lights.add(k.getLightKanban());
                    }
                }
                provider.getMyModel().setAvailableLightKanbans(lights);
            }

            return isValid;
        } catch (Exception e) {
        LOGGER.log(java.util.logging.Level.SEVERE, "Erreur lors de l'authentification de " + username, e);
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
        json.append(",\"kanbanIds\":[");
        if (user.getMyKanban() != null && !user.getMyKanban().isEmpty()) {
            for (int i = 0; i < user.getMyKanban().size(); i++) {
                json.append("\"").append(user.getMyKanban().get(i).getId().toString()).append("\"");
                if (i < user.getMyKanban().size() - 1) {
                    json.append(",");
                }
            }
        }
        json.append("]");

        json.append("}");
        return json.toString();
    }


    private User deserializeUserFromJson(String jsonContent) throws IOException {
        Map<String, String> fields = parseJsonObject(jsonContent);

        if (fields.isEmpty()) return null;

        String idStr = fields.get("id");
        String username = fields.get("username");
        String firstName = fields.get("firstName");
        String lastName = fields.get("lastName");
        String birthDateStr = fields.get("birthDate");
        String avatar = fields.get("avatar");
        String passwordHash = fields.get("passwordHash");

        // Récupération de la liste brute des IDs
        String kanbanIdsJson = fields.get("kanbanIds");

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
                throw new IOException("Erreur hash", e);
            }
            user = secureUser;
        } else {
            user = new User(id, username, firstName, lastName, birthDate);
        }

        if (avatar != null && !avatar.isEmpty()) user.setAvatar(avatar);

        // --- CHARGEMENT DES KANBANS ---
        List<Kanban> userKanbans = new ArrayList<>();
        if (kanbanIdsJson != null && kanbanIdsJson.length() > 2) { // > 2 car "[]" vide
            String content = kanbanIdsJson.substring(1, kanbanIdsJson.length() - 1); // Retirer [ et ]
            String[] ids = content.split(",");

            for (String rawId : ids) {
                String cleanId = stripQuotes(rawId);
                if (cleanId != null && !cleanId.isBlank()) {
                    try {
                        // 1. Conversion de la String en UUID
                        UUID kId = UUID.fromString(cleanId);

                        // 2. Création d'un LightKanban temporaire pour passer l'ID
                        LightKanban tempLight = new LightKanban(kId,null,null );

                        // 3. Appel de la méthode avec le LightKanban
                        Kanban loadedK = KanbanCallsDataImplementation.loadKanbanFromJson(tempLight);

                        if (loadedK != null) {
                            userKanbans.add(loadedK);
                        }
                    } catch (Exception e) {
                        LOGGER.warning("Impossible de charger le kanban ID: " + cleanId + " — " + e.getMessage());

                    }
                }
            }
        }
        user.setMyKanban(userKanbans); // Associer la liste chargée à l'utilisateur

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
            LOGGER.log(java.util.logging.Level.SEVERE, "Erreur lors du chargement de l'utilisateur " + username, e);

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
    public void exportProfile(LightUser lightUserId, String path){
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
            LOGGER.log(java.util.logging.Level.SEVERE, "Erreur lors de la sauvegarde du hash du mot de passe", e);

        }

        return secureUser;
    }

    @Override
    public void addAuthorizedUserToKanban(LightUser user, LightKanban kanban) {
        if (user == null || kanban == null) {
            LOGGER.warning("addAuthorizedUserToKanban : user ou kanban est null");

            return;
        }

        try {
            // 1. Charger le kanban complet depuis le JSON
            Kanban fullKanban = KanbanCallsDataImplementation.loadKanbanFromJson(kanban);
            
            if (fullKanban == null) {
                LOGGER.warning("Kanban non trouvé pour l'ID : " + kanban.getId());

                return;
            }

            // 2. Vérifier si l'utilisateur n'est pas déjà dans la accessList
            if (fullKanban.getAccessList() == null) {
                fullKanban.setAccessList(new ArrayList<>());
            }

            // Vérifier si l'utilisateur existe déjà
            boolean userExists = fullKanban.getAccessList().stream()
                    .anyMatch(access -> access.getUser() != null && access.getUser().getId().equals(user.getId()));

            if (!userExists) {
                // 3. Ajouter l'utilisateur à la accessList
                Access newAccess = new Access(user, null);
                fullKanban.getAccessList().add(newAccess);
                
                // 4. Sauvegarder le kanban mis à jour
                KanbanCallsDataImplementation.saveKanbanAsJson(fullKanban);
                
                LOGGER.info("Utilisateur " + user.getUsername() + " ajouté au kanban " + fullKanban.getTitle());

                // 5. Mettre à jour le kanban dans le modèle local si c'est le kanban actuel
                ClientModel model = provider.getMyModel();
                Kanban currentKanban = model.getCurrentKanban();
                if (currentKanban != null && currentKanban.getId().equals(fullKanban.getId())) {
                    // Mettre à jour le kanban actuel avec la nouvelle accessList
                    currentKanban.setAccessList(fullKanban.getAccessList());
                    model.setCurrentKanban(currentKanban);
                }
            } else {
                LOGGER.info("Utilisateur " + user.getUsername() + " est déjà dans la accessList du kanban");
            }
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Erreur lors de l'ajout de l'utilisateur au kanban", e);

        }
    }

    @Override
    public User getLocalUser () {
        ClientModel myModel = provider.getMyModel();
        User localUser = myModel.getLocalUser();
        return localUser;
    }

    public Void modifyLocalUser(String newFirstName, String newLastName, LocalDate newBirthDate,
                                String newAvatar, String newUsername) {

        ClientModel myModel = provider.getMyModel();
        User currentUser = myModel.getLocalUser();
        if (currentUser == null) return null;

        String oldUsername = currentUser.getUsername();

        // ✏️ Mise à jour des champs
        if(newFirstName != null) currentUser.setFirstName(newFirstName);
        if(newLastName != null) currentUser.setLastName(newLastName);
        if(newBirthDate != null) currentUser.setBirthDate(newBirthDate);
        if(newAvatar != null) currentUser.setAvatar(newAvatar);
        if(newUsername != null) currentUser.setUsername(newUsername);

        // 🔥 Renommage du fichier si le nom change
        if (newUsername != null && !newUsername.equals(oldUsername)) {
            renameUserFile(oldUsername, newUsername);
        }

        saveUser(); // sauvegarde dans le *nouveau* fichier

        return null;
    }

    private void renameUserFile(String oldUsername, String newUsername) {
        try {
            Path oldFile = USERS_DIR.resolve(oldUsername + ".json");
            Path newFile = USERS_DIR.resolve(newUsername + ".json");

            if (Files.exists(oldFile)) {
                Files.move(oldFile, newFile);
                LOGGER.info("Fichier utilisateur renommé : " + oldFile + " → " + newFile);
            }

        } catch (IOException e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Erreur lors du renommage du fichier utilisateur", e);
        }
    }

    @Override
    public void deleteLocalProfile() {
        common.dataClasses.User user = provider.getMyModel().getLocalUser();

        if (user == null) {
            LOGGER.warning("Impossible de supprimer : aucun utilisateur connecté localement.");
            return;
        }

        try {
            Path userFile = USERS_DIR.resolve(user.getUsername() + ".json");

            if (Files.exists(userFile)) {
                Files.delete(userFile);
                LOGGER.info("Profil supprimé avec succès : " + userFile.toAbsolutePath());
            } else {
                LOGGER.warning("Fichier profil introuvable : " + userFile);
            }

        } catch (IOException e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Erreur critique lors de la suppression du profil", e);
            throw new RuntimeException("Erreur I/O lors de la suppression.");
        }
    }

    public void ModifyLocalUserFirstName(String newFirstName) {
        modifyLocalUser(newFirstName, null, null, null, null);
    }
    public void ModifyLocalUserLastName(String newLastName) {
        modifyLocalUser(null, newLastName, null, null, null);
    }
    public void ModifyLocalUserBirthDate(LocalDate newBirthDate) {
        modifyLocalUser(null, null, newBirthDate, null, null);
    }
    public void ModifyLocalUserAvatar(String newAvatar) {
        modifyLocalUser(null, null, null, newAvatar, null);
    }
    public void ModifyLocalUserUsername(String newUsername) {
        modifyLocalUser(null, null, null, null, newUsername);
    }
    


    public DataClientProvider getProvider() {
        return this.provider;
    }
    public void setProvider(DataClientProvider provider) {
        this.provider = provider;
    }

    @Override
    public void askDeleteKanban(LightKanban kanban) {
        LightUser LightUserId = getMyLightUser();
        provider.getCommInterface().askDeleteKanban(kanban, LightUserId);
        ClientModel model = provider.getMyModel();
        
        List<Kanban> myKanbans = model.getLocalUser().getMyKanban();
        myKanbans.removeIf(k -> k.getId().equals(kanban.getId()));
        model.getLocalUser().setMyKanban(myKanbans);
        
        // Supprimer le fichier JSON local
        KanbanCallsDataImplementation.deleteKanbanFromJson(kanban.getId());
    }
}
