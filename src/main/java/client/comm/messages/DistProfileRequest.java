package client.comm.messages;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import common.dataClasses.LightUser;
import common.dataClasses.User;

/**
 * Client -> Server: request distant user's profile by UUIDs.
 */
public class DistProfileRequest extends Message {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DistProfileRequest.class.getName());

    private final UUID requesterId;
    private final UUID requestedUserId;

    public DistProfileRequest(UUID requesterId, UUID requestedUserId) {
        this.requesterId = requesterId;
        this.requestedUserId = requestedUserId;
    }

    public UUID requesterId() {
        return requesterId;
    }

    public UUID requestedUserId() {
        return requestedUserId;
    }

    @Override
    public Optional<Message> handle() {
        // Executed on SERVER side
        try {
            var data = server.ServerContext.getData();
            if (data == null) {
                LOGGER.severe("Server data interface is null");
                return Optional.empty();
            }

            User full = null;

            // First, try the server-side full user cache for uniform behavior
            try {
                server.data.DataServProvider provider = server.ServerContext.getProvider();
                if (provider != null) {
                    server.data.ServerModel srvModel = provider.getModel();
                    if (srvModel != null && srvModel.getConnectedUsersFull() != null) {
                        full = srvModel.getConnectedUsersFull().get(requestedUserId);
                    }
                }
                LOGGER.info(String.format("[SERVER] DistProfileRequest cache lookup: %s for userId=%s",
                        (full != null ? "HIT" : "MISS"), requestedUserId));
            } catch (Throwable ignored) {}

            if (full == null) {
                // Fallback: find LightUser, then enrich from JSON files
                var users = data.getUsersList();
                LightUser found = null;
                if (users != null) {
                    for (LightUser u : users) {
                        if (u.getId().equals(requestedUserId)) { found = u; break; }
                    }
                }
                if (found != null) {
                    String rawUserJson = readUserJson(found.getId());
                    full = buildUserFromJson(found.getId(), found.getUsername(), rawUserJson);
                    java.util.List<common.dataClasses.Kanban> created = loadUserKanbans(rawUserJson);
                    if (full != null && created != null) {
                        full.setMyKanban(created);
                    }
                    LOGGER.info(String.format("[SERVER] JSON fallback used for user=%s, kanbans=%d",
                            found.getUsername(),
                            (full != null && full.getMyKanban() != null ? full.getMyKanban().size() : 0)));
                    if (full == null) {
                        String uname = found.getUsername();
                        full = new User(found.getId(), uname, uname, "", null);
                    }
                } else {
                    LOGGER.warning(() -> "Requested user not found in connected list: " + requestedUserId);
                }
            } else {
                // If full user came from cache but has no kanbans populated, enrich from JSON
                try {
                    if (full.getMyKanban() == null || full.getMyKanban().isEmpty()) {
                        String rawUserJson = readUserJson(full.getId());
                        java.util.List<common.dataClasses.Kanban> created = loadUserKanbans(rawUserJson);
                        if (created != null && !created.isEmpty()) {
                            full.setMyKanban(created);
                            LOGGER.info(String.format("[SERVER] Cache enrichment: added %d kanbans for user=%s",
                                    created.size(), full.getUsername()));
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
            // Build answer (may be null if not found)
            LOGGER.info(String.format("[SERVER] Forwarding profile answer to requester=%s, user=%s, kanbans=%d",
                    requesterId,
                    (full != null ? full.getUsername() : "<null>"),
                    (full != null && full.getMyKanban() != null ? full.getMyKanban().size() : 0)));
            return Optional.of(new ForwardProfileAnswer(requesterId, full));
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Error handling DistProfileRequest", e);
            return Optional.empty();
        }
    }

    private static String readUserJson(UUID userId) {
        return findJsonByIdInDir(java.nio.file.Paths.get("data", "users"), userId);
    }

    private static common.dataClasses.User buildUserFromJson(UUID userId, String fallbackUsername, String content) {
        if (content == null)
            return null;
        String uName = coalesce(extractJsonString(content, "username"), fallbackUsername);
        String first = extractJsonString(content, "firstName");
        String last = extractJsonString(content, "lastName");
        String birth = extractJsonString(content, "birthDate");
        String avatar = extractJsonString(content, "avatar");

        java.time.LocalDate birthDate = null;
        try {
            if (birth != null && !birth.isBlank())
                birthDate = java.time.LocalDate.parse(birth);
        } catch (Throwable ignored) {
        }

        common.dataClasses.User user = new common.dataClasses.User(userId, uName, first, last, birthDate);
        if (avatar != null && !avatar.isBlank()) {
            try {
                user.setAvatar(avatar);
            } catch (Throwable ignored) {
            }
        }
        return user;
    }

    private static java.util.List<common.dataClasses.Kanban> loadUserKanbans(String content) {
        if (content == null)
            return null;
        java.util.List<common.dataClasses.Kanban> list = new java.util.ArrayList<>();
        // Extract kanbanIds array: naive scan for UUID strings inside [ ... ] after
        // "kanbanIds"
        int keyIdx = content.indexOf("\"kanbanIds\"");
        if (keyIdx < 0)
            return list;
        int arrStart = content.indexOf('[', keyIdx);
        int arrEnd = content.indexOf(']', arrStart);
        if (arrStart < 0 || arrEnd < 0 || arrEnd <= arrStart)
            return list;
        String arraySlice = content.substring(arrStart + 1, arrEnd);
        String[] parts = arraySlice.split(",");
        for (String part : parts) {
            String idStr = part.replaceAll("[^0-9a-fA-F-]", "").trim();
            if (idStr.length() < 36)
                continue;
            try {
                java.util.UUID kid = java.util.UUID.fromString(idStr);
                common.dataClasses.Kanban k = readKanbanById(kid);
                if (k != null)
                    list.add(k);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return list;
    }

    private static common.dataClasses.Kanban readKanbanById(java.util.UUID id) {
        String content = findJsonByIdInDir(java.nio.file.Paths.get("data", "kanbans"), id);
        if (content == null)
            return null;
        String title = extractJsonString(content, "title");
        return new common.dataClasses.Kanban(id, title != null ? title : "Kanban");
    }

    // Generic helper to reduce duplicated directory scan code
    private static String findJsonByIdInDir(java.nio.file.Path dir, java.util.UUID id) {
        try {
            if (!java.nio.file.Files.isDirectory(dir))
                return null;
            try (java.util.stream.Stream<java.nio.file.Path> stream = java.nio.file.Files.list(dir)) {
                java.util.List<java.nio.file.Path> paths = stream.toList();
                for (java.nio.file.Path p : paths) {
                    String content = java.nio.file.Files.readString(p);
                    String idStr = extractJsonString(content, "id");
                    if (idStr == null)
                        continue;
                    try {
                        java.util.UUID parsed = java.util.UUID.fromString(idStr);
                        if (parsed.equals(id)) {
                            return content;
                        }
                    } catch (IllegalArgumentException ex) {
                        // ignore non-UUID id fields
                    }
                }
            }
        } catch (IOException ignore) {
        }
        return null;
    }

    private static String extractJsonString(String json, String key) {
        if (json == null)
            return null;
        String pattern = "\"" + key + "\"" + "\\s*:\\s*" + "\""; // "key": "
        int idx = json.indexOf(pattern);
        if (idx < 0)
            return null;
        int start = idx + pattern.length();
        int end = json.indexOf("\"", start);
        if (end < 0)
            return null;
        return json.substring(start, end);
    }

    private static String coalesce(String a, String b) {
        return (a != null && !a.isBlank()) ? a : b;
    }
}
