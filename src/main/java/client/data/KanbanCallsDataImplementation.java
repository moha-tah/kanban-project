package client.data;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import client.interfaces.DataCallsComm;
import client.interfaces.DataClientCallsKanban;
import client.interfaces.KanbanCallsDataClient;
import common.dataClasses.* ;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Instant;

import common.dataClasses.Modification;

public class KanbanCallsDataImplementation implements KanbanCallsDataClient {

    private DataClientProvider provider;

    // Gson configuré pour supporter java.time.LocalDate sans réflexion sur les champs privés
    private static final Gson GSON = new GsonBuilder()
            // LocalDate -> "2025-11-25"
            .registerTypeAdapter(LocalDate.class,
                    (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                            new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDate.class,
                    (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                            LocalDate.parse(json.getAsString()))

            // LocalDateTime -> "2025-11-25T10:15:30"
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                            new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                            LocalDateTime.parse(json.getAsString()))

            // LocalTime -> "10:15:30"
            .registerTypeAdapter(LocalTime.class,
                    (JsonSerializer<LocalTime>) (src, typeOfSrc, context) ->
                            new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalTime.class,
                    (JsonDeserializer<LocalTime>) (json, typeOfT, context) ->
                            LocalTime.parse(json.getAsString()))

            // Instant -> "2025-11-25T09:15:30Z"
            .registerTypeAdapter(Instant.class,
                    (JsonSerializer<Instant>) (src, typeOfSrc, context) ->
                            new JsonPrimitive(src.toString()))
            .registerTypeAdapter(Instant.class,
                    (JsonDeserializer<Instant>) (json, typeOfT, context) ->
                            Instant.parse(json.getAsString()))
            
            .enableComplexMapKeySerialization()  // Enable proper serialization of complex map keys like Column
            .setPrettyPrinting()  // Format JSON for better readability
            .serializeNulls()     // Include null fields in JSON
            .create();

    // Constructeur
    public KanbanCallsDataImplementation(DataClientProvider provider) {
        this.provider = provider;
    }

    public void saveSnapshot(Kanban kanban) {
        // TODO
    }

    public void saveKanban(Kanban kanban) {
        if (provider == null) {
            throw new IllegalStateException("KanbanCallsDataImplementation: provider is null");
        }

        DataCallsComm comm = provider.getCommInterface();
        ClientModel model = provider.getMyModel();
        User modelUser = model.getLocalUser();
        List<LightKanban> availableLightKanbans = model.getAvailableLightKanbans();

        // Mettre à jour le modèle local
        model.setCurrentKanban(kanban);
        availableLightKanbans.add(kanban.getLightKanban());
        model.setAvailableLightKanbans(availableLightKanbans);

        List<Kanban> userKanbans = modelUser.getMyKanban();
        userKanbans.add(kanban);
        modelUser.setMyKanban(userKanbans);
        model.setLocalUser(modelUser);

        // Mettre à jour le provider
        provider.setMyModel(model);

        // Sauvegarde locale en JSON
        saveKanbanAsJson(kanban);

        // Envoi au serveur (tu peux commenter cette ligne tant que tu ne veux que du local)
        if (comm != null) {
            comm.sendKanban(kanban);
        }
    }

    // Sauvegarde locale JSON d'un Kanban complet dans data/kanbans
    public static void saveKanbanAsJson(Kanban kanban) {
        try {
            Path kanbanDir = Paths.get("data", "kanbans");
            Files.createDirectories(kanbanDir);
            Path file = kanbanDir.resolve(kanban.getId().toString() + ".json");

            String kanbanJson = GSON.toJson(kanban);

            Path tmp = kanbanDir.resolve(kanban.getId().toString() + ".json.tmp");
            Files.writeString(tmp, kanbanJson, StandardCharsets.UTF_8);
            Files.move(tmp, file,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                    java.nio.file.StandardCopyOption.ATOMIC_MOVE);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to save kanban as JSON", e);
        }
    }

    // Charge un Kanban depuis data/kanbans/{kanbanId}.json
    public static Kanban loadKanbanFromJson(LightKanban lightKanban) {
        try {
            Path kanbanDir = Paths.get("data", "kanbans");

            // Utilisation de l'ID contenu dans l'objet LightKanban pour le chemin du fichier
            Path file = kanbanDir.resolve(lightKanban.getId().toString() + ".json");

            if (Files.exists(file)) {
                String kanbanJson = Files.readString(file, StandardCharsets.UTF_8);
                Kanban kanban = GSON.fromJson(kanbanJson, Kanban.class);

                // Note: le champ creator est transient, il sera null ici.

                return kanban;
            } else {
                return null;
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to load kanban from JSON", e);
        }
    }

    /**
     * Charge tous les kanbans d'un utilisateur basé sur la liste d'IDs
     * Utilisé au démarrage de l'application pour charger les kanbans de l'utilisateur connecté
     */
    public static List<Kanban> loadUserKanbans(List<LightKanban> lightKanbans) {
        List<Kanban> kanbans = new ArrayList<>();

        if (lightKanbans == null || lightKanbans.isEmpty()) {
            return kanbans;
        }

        for (LightKanban lightKanban : lightKanbans) {
            try {
                Kanban kanban = loadKanbanFromJson(lightKanban);

                if (kanban != null) {
                    kanbans.add(kanban);
                } else {
                    System.err.println("Kanban non trouvé pour l'ID: " + lightKanban.getId() + " (" + lightKanban.getTitle() + ")");
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement du kanban " + lightKanban.getId() + ": " + e.getMessage());
            }
        }
        return kanbans;
    }

    public List<Snapshot> getListSnapshot() {
        // TODO: à implémenter proprement
        return new List<Snapshot>() {
            @Override
            public boolean add(Snapshot arg0) {
                throw new UnsupportedOperationException("Unimplemented method 'add'");
            }
            @Override
            public void add(int arg0, Snapshot arg1) {
                throw new UnsupportedOperationException("Unimplemented method 'add'");
            }
            @Override
            public boolean addAll(Collection<? extends Snapshot> c) {
                throw new UnsupportedOperationException("Unimplemented method 'addAll'");
            }
            @Override
            public boolean addAll(int index, Collection<? extends Snapshot> c) {
                throw new UnsupportedOperationException("Unimplemented method 'addAll'");
            }
            @Override
            public void clear() {
                throw new UnsupportedOperationException("Unimplemented method 'clear'");
            }
            @Override
            public boolean contains(Object o) {
                throw new UnsupportedOperationException("Unimplemented method 'contains'");
            }
            @Override
            public boolean containsAll(Collection<?> c) {
                throw new UnsupportedOperationException("Unimplemented method 'containsAll'");
            }
            @Override
            public Snapshot get(int index) {
                throw new UnsupportedOperationException("Unimplemented method 'get'");
            }
            @Override
            public int indexOf(Object o) {
                throw new UnsupportedOperationException("Unimplemented method 'indexOf'");
            }
            @Override
            public boolean isEmpty() {
                throw new UnsupportedOperationException("Unimplemented method 'isEmpty'");
            }
            @Override
            public Iterator<Snapshot> iterator() {
                throw new UnsupportedOperationException("Unimplemented method 'iterator'");
            }
            @Override
            public int lastIndexOf(Object o) {
                throw new UnsupportedOperationException("Unimplemented method 'lastIndexOf'");
            }
            @Override
            public ListIterator<Snapshot> listIterator() {
                throw new UnsupportedOperationException("Unimplemented method 'listIterator'");
            }
            @Override
            public ListIterator<Snapshot> listIterator(int index) {
                throw new UnsupportedOperationException("Unimplemented method 'listIterator'");
            }
            @Override
            public boolean remove(Object o) {
                throw new UnsupportedOperationException("Unimplemented method 'remove'");
            }
            @Override
            public Snapshot remove(int index) {
                throw new UnsupportedOperationException("Unimplemented method 'remove'");
            }
            @Override
            public boolean removeAll(Collection<?> c) {
                throw new UnsupportedOperationException("Unimplemented method 'removeAll'");
            }
            @Override
            public boolean retainAll(Collection<?> c) {
                throw new UnsupportedOperationException("Unimplemented method 'retainAll'");
            }
            @Override
            public Snapshot set(int arg0, Snapshot arg1) {
                throw new UnsupportedOperationException("Unimplemented method 'set'");
            }
            @Override
            public int size() {
                throw new UnsupportedOperationException("Unimplemented method 'size'");
            }
            @Override
            public List<Snapshot> subList(int fromIndex, int toIndex) {
                throw new UnsupportedOperationException("Unimplemented method 'subList'");
            }
            @Override
            public Object[] toArray() {
                throw new UnsupportedOperationException("Unimplemented method 'toArray'");
            }
            @Override
            public <T> T[] toArray(T[] arg0) {
                throw new UnsupportedOperationException("Unimplemented method 'toArray'");
            }
        };
    }

    public Snapshot getSnapshot(Snapshot snap) {
        // TODO
        return new Snapshot();
    }

    public void deleteSnapshot(Snapshot snap) {
        // TODO
    }





    // getters / setters
    public DataClientProvider getProvider() {
        return this.provider;
    }

    public void setProvider(DataClientProvider provider) {
        this.provider = provider;
    }
}