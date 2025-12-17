package client.data;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.*;

import client.interfaces.MainCallsDataClient;
import common.dataClasses.*;

/**
 * Implémentation de l'interface {@link MainCallsDataClient}.
 * 
 * Cette classe gère les appels depuis la couche IHM principale vers la couche données.
 * Elle est responsable de l'authentification, de la gestion des profils utilisateurs,
 * de la sauvegarde/chargement des données depuis le disque, et de la gestion des kanbans.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see MainCallsDataClient
 * @see DataClientProvider
 */
public class MainCallsDataImplementation implements MainCallsDataClient {
    /**
     * Fournisseur de services de la couche données.
     */
    private DataClientProvider provider;
    
    /**
     * Logger pour les messages de log de cette classe.
     */
    private static final java.util.logging.Logger LOGGER =
        java.util.logging.Logger.getLogger(MainCallsDataImplementation.class.getName());

    /**
     * Chemin vers le fichier JSON contenant la liste des utilisateurs.
     */
    private static final Path USERS_FILE = Path.of("data", "users.json");
    
    /**
     * Répertoire contenant les fichiers JSON des utilisateurs individuels.
     */
    private static final Path USERS_DIR = Path.of("data", "users");
    
    /**
     * Clé JSON pour le nom d'utilisateur.
     */
    private static final String USERNAME = "username";

    /**
     * Supprime les guillemets d'une chaîne de caractères si présents.
     * 
     * @param s La chaîne à traiter (peut être null)
     * @return La chaîne sans guillemets, ou null si s est null
     */
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

    /**
     * Constructeur de l'implémentation.
     * 
     * @param provider Le fournisseur de services de la couche données (ne doit pas être null)
     */
    public MainCallsDataImplementation(DataClientProvider provider) {
        this.provider = provider;
    }

    /**
     * Récupère la liste des kanbans disponibles pour l'utilisateur local.
     * 
     * @return La liste des kanbans disponibles sous forme de LightKanban
     */
    @Override
    public List<LightKanban> getMyListLightKanbans(){
        return provider.getMyModel().getAvailableLightKanbans();
    }

    /**
     * Récupère l'utilisateur local connecté sous forme de LightUser.
     * 
     * @return L'utilisateur local, ou null si aucun utilisateur n'est connecté
     */
    @Override
    public LightUser getMyLightUser(){
        return provider.getMyModel().getLocalUser();
    }

    /**
     * Sauvegarde l'utilisateur local dans un fichier JSON.
     * 
     * Cette méthode sérialise l'utilisateur en JSON et l'écrit dans le répertoire
     * des utilisateurs. Le fichier est nommé selon le nom d'utilisateur.
     */
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

    /**
     * Authentifie un utilisateur avec son nom d'utilisateur et son mot de passe.
     * 
     * Cette méthode charge l'utilisateur depuis le disque, vérifie le mot de passe,
     * et met à jour le modèle local si l'authentification réussit. Elle charge
     * également les kanbans de l'utilisateur.
     * 
     * @param username Le nom d'utilisateur (ne doit pas être null ou vide)
     * @param password Le mot de passe (ne doit pas être null ou vide)
     * @return true si l'authentification réussit, false sinon
     */
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

    /**
     * Lit un fichier JSON et le convertit en Map.
     * 
     * @param file Le chemin vers le fichier JSON à lire
     * @return Une Map contenant les paires clé-valeur du JSON
     * @throws IOException si la lecture du fichier échoue
     */
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


    /**
     * Écrit une Map dans un fichier JSON.
     * 
     * @param file Le chemin vers le fichier JSON à écrire
     * @param map La Map à sérialiser en JSON
     * @throws IOException si l'écriture du fichier échoue
     */
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


    /**
     * Sérialise un utilisateur en chaîne JSON.
     * 
     * @param user L'utilisateur à sérialiser (ne doit pas être null)
     * @return La représentation JSON de l'utilisateur
     */
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


    /**
     * Désérialise un utilisateur depuis une chaîne JSON.
     * 
     * Cette méthode charge également les kanbans de l'utilisateur depuis
     * leurs fichiers JSON individuels.
     * 
     * @param jsonContent Le contenu JSON à désérialiser (ne doit pas être null)
     * @return L'utilisateur désérialisé, ou null si le JSON est vide
     * @throws IOException si la désérialisation échoue ou si des champs sont manquants
     */
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
                        LightKanban tempLight = new LightKanban(kId, "");

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

    /**
     * Charge un utilisateur depuis le disque par son nom d'utilisateur.
     * 
     * @param username Le nom d'utilisateur à charger (ne doit pas être null)
     * @return L'utilisateur chargé, ou null si non trouvé ou en cas d'erreur
     */
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


    /**
     * Parse une chaîne JSON en Map, en gérant les objets et tableaux imbriqués.
     * 
     * @param json La chaîne JSON à parser (peut être null ou vide)
     * @return Une Map contenant les paires clé-valeur du JSON
     */
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


    /**
     * Échappe les caractères spéciaux pour une utilisation dans JSON.
     * 
     * @param str La chaîne à échapper (peut être null)
     * @return La chaîne échappée, ou chaîne vide si str est null
     */
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }


    /**
     * Déséchappe les caractères spéciaux d'une chaîne JSON.
     * 
     * @param str La chaîne à déséchapper (peut être null)
     * @return La chaîne déséchappée, ou chaîne vide si str est null
     */
    private String unescapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }


    /**
     * Exporte le profil de l'utilisateur local vers un fichier JSON.
     * 
     * Cette méthode crée une copie du profil sans données sensibles (mot de passe)
     * et l'écrit dans le fichier spécifié. Le profil exporté ne contient pas
     * les kanbans pour des raisons de taille.
     * 
     * @param lightUserId L'identifiant de l'utilisateur à exporter (ne doit pas être null)
     * @param path Le chemin vers le fichier de destination (ne doit pas être null ou vide)
     */
    @Override
    public void exportProfile(LightUser lightUserId, String path){
        if (lightUserId == null || path == null || path.isBlank()) {
            LOGGER.warning("exportProfile: lightUserId or path is null/empty");
            return;
        }
        try {
            User localUser = provider.getMyModel().getLocalUser();
            if (localUser == null || !localUser.getId().equals(lightUserId.getId())) {
                LOGGER.warning("exportProfile: user not found or ID mismatch (expected: " + lightUserId.getId() + ")");
                return;
            }
            // Always export as plain User, never SecureUser, to avoid leaking password hashes or sensitive data
            User profileCopy = new User(localUser.getId(), localUser.getUsername(), 
                    localUser.getFirstName(), localUser.getLastName(), localUser.getBirthDate());
            if (localUser.getAvatar() != null && !localUser.getAvatar().isBlank()) {
                profileCopy.setAvatar(localUser.getAvatar());
            }
            profileCopy.setMyKanban(null);
            // Ensure no password or sensitive fields are present in profileCopy
            String json = serializeUserToJson(profileCopy);
            java.nio.file.Path outputPath = java.nio.file.Paths.get(path);
            java.nio.file.Path parentDir = outputPath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }
            java.nio.file.Path tmpPath = outputPath.resolveSibling(outputPath.getFileName().toString() + ".tmp");
            Files.write(tmpPath, json.getBytes(StandardCharsets.UTF_8));
            try {
                Files.move(tmpPath, outputPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                LOGGER.info("Profile exported successfully: " + outputPath.toAbsolutePath());
            } finally {
                // Clean up the temporary file if it still exists (i.e., move failed)
                try {
                    if (Files.exists(tmpPath)) {
                        Files.delete(tmpPath);
                    }
                } catch (IOException cleanupEx) {
                    LOGGER.warning("Failed to delete temporary file: " + tmpPath + " - " + cleanupEx.getMessage());
                }
            }

        } catch (IOException e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "exportProfile: IOException", e);
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "exportProfile: unexpected error while exporting profile for userId=" + (lightUserId != null ? lightUserId.getId() : "null") + " to path=" + path, e);
        }
    }

    /**
     * Importe un profil utilisateur depuis un fichier JSON.
     * 
     * Cette méthode copie le fichier JSON dans le répertoire des utilisateurs
     * et met à jour le fichier de liste des utilisateurs.
     * 
     * @param path Le chemin vers le fichier JSON à importer (ne doit pas être null ou vide)
     * @throws UncheckedIOException si l'importation échoue
     * @throws IllegalArgumentException si le fichier JSON est invalide
     */
    @Override
    public void importMyProfile(String path){
        // convert the string into a path
        Path filePath = Paths.get(path);   

        try{
            //parser to read data from the file
            Object o = new JSONParser().parse(new FileReader(path));
            JSONObject profile = (JSONObject) o;

            // get the correct directory to copy the file
            Path newFile = USERS_DIR.resolve(profile.get(USERNAME) + ".json");

            Files.copy(filePath, newFile);

            String usersFile = USERS_FILE.toString();

            Object u = new JSONParser().parse(new FileReader(usersFile));
            JSONObject users = (JSONObject) u;
            users.put(profile.get(USERNAME), profile.get("passworHash") );
            Files.write(USERS_FILE, users.toJSONString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load profile from JSON", e);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Failed to parse JSON file", e);
        }
    }

    /**
     * Crée un nouveau profil utilisateur et le sauvegarde localement.
     * 
     * Cette méthode crée un utilisateur sécurisé avec un mot de passe hashé,
     * le définit comme utilisateur local, et sauvegarde le hash du mot de passe
     * dans le fichier de liste des utilisateurs.
     * 
     * @param login Le nom d'utilisateur (ne doit pas être null)
     * @param password Le mot de passe en clair (ne doit pas être null)
     * @param name Le prénom (ne doit pas être null)
     * @param surname Le nom de famille (ne doit pas être null)
     * @param age L'âge (utilisé pour calculer la date de naissance)
     * @param avatar Le chemin vers l'avatar (peut être null)
     * @param role Le rôle (non utilisé actuellement)
     * @param permissions Les permissions (non utilisé actuellement)
     * @param contacts Les contacts (non utilisé actuellement)
     * @param kanbanList La liste des kanbans (non utilisé actuellement)
     * @param status Le statut (peut être utilisé comme avatar si avatar est null)
     * @return L'utilisateur créé sous forme de LightUser
     */
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

    /**
     * Ajoute un utilisateur autorisé à un kanban.
     * 
     * Cette méthode charge le kanban depuis le disque, ajoute l'utilisateur
     * à sa liste d'accès, et sauvegarde le kanban mis à jour. Si le kanban
     * est actuellement ouvert, il est également mis à jour dans le modèle local.
     * 
     * @param user L'utilisateur à autoriser (ne doit pas être null)
     * @param kanban Le kanban auquel ajouter l'utilisateur (ne doit pas être null)
     */
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

    /**
     * Récupère l'utilisateur local complet.
     * 
     * @return L'utilisateur local, ou null si aucun utilisateur n'est connecté
     */
    public User getLocalUser() {
        ClientModel myModel = provider.getMyModel();
        User localUser = myModel.getLocalUser();
        return localUser;
    }

    /**
     * Modifie les informations de l'utilisateur local.
     * 
     * Cette méthode met à jour les champs de l'utilisateur local et sauvegarde
     * les modifications. Si le nom d'utilisateur change, le fichier est renommé.
     * 
     * @param newFirstName Le nouveau prénom (peut être null pour ne pas modifier)
     * @param newLastName Le nouveau nom de famille (peut être null pour ne pas modifier)
     * @param newBirthDate La nouvelle date de naissance (peut être null pour ne pas modifier)
     * @param newAvatar Le nouveau chemin d'avatar (peut être null pour ne pas modifier)
     * @param newUsername Le nouveau nom d'utilisateur (peut être null pour ne pas modifier)
     * @return null (type Void pour compatibilité)
     */
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

    /**
     * Renomme le fichier JSON d'un utilisateur.
     * 
     * @param oldUsername L'ancien nom d'utilisateur (ne doit pas être null)
     * @param newUsername Le nouveau nom d'utilisateur (ne doit pas être null)
     */
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

    /**
     * Supprime le profil utilisateur local du disque.
     * 
     * Cette méthode supprime le fichier JSON de l'utilisateur local.
     * Aucune vérification n'est effectuée avant la suppression.
     */
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

    /**
     * Modifie uniquement le prénom de l'utilisateur local.
     * 
     * @param newFirstName Le nouveau prénom (ne doit pas être null)
     */
    public void ModifyLocalUserFirstName(String newFirstName) {
        modifyLocalUser(newFirstName, null, null, null, null);
    }
    
    /**
     * Modifie uniquement le nom de famille de l'utilisateur local.
     * 
     * @param newLastName Le nouveau nom de famille (ne doit pas être null)
     */
    public void ModifyLocalUserLastName(String newLastName) {
        modifyLocalUser(null, newLastName, null, null, null);
    }
    
    /**
     * Modifie uniquement la date de naissance de l'utilisateur local.
     * 
     * @param newBirthDate La nouvelle date de naissance (ne doit pas être null)
     */
    public void ModifyLocalUserBirthDate(LocalDate newBirthDate) {
        modifyLocalUser(null, null, newBirthDate, null, null);
    }
    
    /**
     * Modifie uniquement l'avatar de l'utilisateur local.
     * 
     * @param newAvatar Le nouveau chemin d'avatar (ne doit pas être null)
     */
    public void ModifyLocalUserAvatar(String newAvatar) {
        modifyLocalUser(null, null, null, newAvatar, null);
    }
    
    /**
     * Modifie uniquement le nom d'utilisateur de l'utilisateur local.
     * 
     * @param newUsername Le nouveau nom d'utilisateur (ne doit pas être null)
     */
    public void ModifyLocalUserUsername(String newUsername) {
        modifyLocalUser(null, null, null, null, newUsername);
    }
    


    /**
     * Récupère le fournisseur de services de la couche données.
     * 
     * @return Le fournisseur de services
     */
    public DataClientProvider getProvider() {
        return this.provider;
    }
    
    /**
     * Définit le fournisseur de services de la couche données.
     * 
     * @param provider Le fournisseur de services à définir (ne doit pas être null)
     */
    public void setProvider(DataClientProvider provider) {
        this.provider = provider;
    }
}
