package client.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import client.interfaces.MainCallsDataClient;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import java.util.List;

import static com.sun.javafx.util.Utils.stripQuotes;


public class MainCallsDataImplementation implements MainCallsDataClient {
    private DataClientProvider provider;

    private static final Path USERS_FILE = Path.of("data", "users.json");
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
    public void saveUser(){}

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


    @Override
    public void exportProfile(UUID lightUserId, String path){}

    @Override
    public void importMyProfile(String path){}

    @Override
    public void sendCreateProfile(List<?> profileDetails){}



    //getters
    public DataClientProvider getProvider() {
        return this.provider;
    }
    //setters
    public void setProvider(DataClientProvider provider) {
        this.provider = provider;
    }




}