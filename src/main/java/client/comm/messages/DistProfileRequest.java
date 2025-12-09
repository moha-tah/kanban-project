package client.comm.messages;

import java.util.Optional;
import java.util.UUID;

/**
 * Client -> Server: request distant user's profile by UUIDs.
 */
public class DistProfileRequest extends Message {
    private static final long serialVersionUID = 1L;

    private final UUID requesterId;
    private final UUID requestedUserId;

    public DistProfileRequest(UUID requesterId, UUID requestedUserId) {
        this.requesterId = requesterId;
        this.requestedUserId = requestedUserId;
    }

    public UUID requesterId() { return requesterId; }
    public UUID requestedUserId() { return requestedUserId; }

    @Override
    public Optional<Message> handle() {
        // Executed on SERVER side
        try {
            var data = server.ServerContext.getData();
            if (data == null) {
                java.util.logging.Logger.getLogger(DistProfileRequest.class.getName())
                        .severe("Server data interface is null");
                return Optional.empty();
            }
            // First, try the server-side full user cache for uniform behavior
            common.dataClasses.User full = null;
            try {
                server.data.DataServProvider provider = server.ServerContext.getProvider();
                if (provider != null) {
                    server.data.ServerModel srvModel = provider.getModel();
                    if (srvModel != null && srvModel.getConnectedUsersFull() != null) {
                        full = srvModel.getConnectedUsersFull().get(requestedUserId);
                    }
                }
                java.util.logging.Logger.getLogger(DistProfileRequest.class.getName())
                    .info("[SERVER] DistProfileRequest cache lookup: "
                            + (full != null ? "HIT" : "MISS")
                            + " for userId=" + requestedUserId);
            } catch (Throwable ignored) {}

            if (full == null) {
                // Fallback: find LightUser, then enrich from JSON files
                var users = data.getUsersList();
                common.dataClasses.LightUser found = null;
                if (users != null) {
                    for (common.dataClasses.LightUser u : users) {
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
                    java.util.logging.Logger.getLogger(DistProfileRequest.class.getName())
                        .info("[SERVER] JSON fallback used for user=" + (found != null ? found.getUsername() : "<null>")
                                + ", kanbans=" + (full != null && full.getMyKanban() != null ? full.getMyKanban().size() : 0));
                    if (full == null) {
                        String uname = found.getUsername();
                        full = new common.dataClasses.User(found.getId(), uname, uname, "", null);
                    }
                } else {
                    java.util.logging.Logger.getLogger(DistProfileRequest.class.getName())
                        .warning("[SERVER] Requested user not found in connected list: " + requestedUserId);
                }
            } else {
                // If full user came from cache but has no kanbans populated, enrich from JSON
                try {
                    if (full.getMyKanban() == null || full.getMyKanban().isEmpty()) {
                        String rawUserJson = readUserJson(full.getId());
                        java.util.List<common.dataClasses.Kanban> created = loadUserKanbans(rawUserJson);
                        if (created != null && !created.isEmpty()) {
                            full.setMyKanban(created);
                            java.util.logging.Logger.getLogger(DistProfileRequest.class.getName())
                                .info("[SERVER] Cache enrichment: added " + created.size() + " kanbans for user=" + full.getUsername());
                        }
                    }
                } catch (Throwable ignored) {}
            }
            // Build answer (may be null if not found)
            java.util.logging.Logger.getLogger(DistProfileRequest.class.getName())
                .info("[SERVER] Forwarding profile answer to requester=" + requesterId
                        + ", user=" + (full != null ? full.getUsername() : "<null>")
                        + ", kanbans=" + (full != null && full.getMyKanban() != null ? full.getMyKanban().size() : 0));
            return Optional.of(new ForwardProfileAnswer(requesterId, full));
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(DistProfileRequest.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Error handling DistProfileRequest", e);
            return Optional.empty();
        }
    }

    private static String readUserJson(UUID userId) {
        try {
            java.nio.file.Path dir = java.nio.file.Paths.get("data", "users");
            if (!java.nio.file.Files.isDirectory(dir)) return null;
            try (java.util.stream.Stream<java.nio.file.Path> stream = java.nio.file.Files.list(dir)) {
                for (java.nio.file.Path p : (java.util.List<java.nio.file.Path>)stream.toList()) {
                    String content = java.nio.file.Files.readString(p);
                    // Primitive field extraction; robust enough for flat JSON
                    String idStr = extractJsonString(content, "id");
                    if (idStr == null) continue;
                    try {
                        UUID parsed = java.util.UUID.fromString(idStr);
                        if (!parsed.equals(userId)) continue;
                    } catch (IllegalArgumentException ex) { continue; }
                    return content;
                }
            }
        } catch (Exception ignore) {}
        return null;
    }

    private static common.dataClasses.User buildUserFromJson(UUID userId, String fallbackUsername, String content) {
        if (content == null) return null;
        String uName = coalesce(extractJsonString(content, "username"), fallbackUsername);
        String first = extractJsonString(content, "firstName");
        String last  = extractJsonString(content, "lastName");
        String birth = extractJsonString(content, "birthDate");
        String avatar = extractJsonString(content, "avatar");

        java.time.LocalDate birthDate = null;
        try { if (birth != null && !birth.isBlank()) birthDate = java.time.LocalDate.parse(birth); } catch (Throwable ignored) {}

        common.dataClasses.User user = new common.dataClasses.User(userId, uName, first, last, birthDate);
        if (avatar != null && !avatar.isBlank()) {
            try { user.setAvatar(avatar); } catch (Throwable ignored) {}
        }
        return user;
    }

    private static java.util.List<common.dataClasses.Kanban> loadUserKanbans(String content) {
        if (content == null) return null;
        java.util.List<common.dataClasses.Kanban> list = new java.util.ArrayList<>();
        // Extract kanbanIds array: naive scan for UUID strings inside [ ... ] after "kanbanIds"
        int keyIdx = content.indexOf("\"kanbanIds\"");
        if (keyIdx < 0) return list;
        int arrStart = content.indexOf('[', keyIdx);
        int arrEnd = content.indexOf(']', arrStart);
        if (arrStart < 0 || arrEnd < 0 || arrEnd <= arrStart) return list;
        String arraySlice = content.substring(arrStart + 1, arrEnd);
        String[] parts = arraySlice.split(",");
        for (String part : parts) {
            String idStr = part.replaceAll("[^0-9a-fA-F-]", "").trim();
            if (idStr.length() < 36) continue;
            try {
                java.util.UUID kid = java.util.UUID.fromString(idStr);
                common.dataClasses.Kanban k = readKanbanById(kid);
                if (k != null) list.add(k);
            } catch (IllegalArgumentException ignored) {}
        }
        return list;
    }

    private static common.dataClasses.Kanban readKanbanById(java.util.UUID id) {
        try {
            java.nio.file.Path dir = java.nio.file.Paths.get("data", "kanbans");
            if (!java.nio.file.Files.isDirectory(dir)) return null;
            try (java.util.stream.Stream<java.nio.file.Path> stream = java.nio.file.Files.list(dir)) {
                for (java.nio.file.Path p : (java.util.List<java.nio.file.Path>)stream.toList()) {
                    String content = java.nio.file.Files.readString(p);
                    String idStr = extractJsonString(content, "id");
                    if (idStr == null) continue;
                    try {
                        java.util.UUID parsed = java.util.UUID.fromString(idStr);
                        if (!parsed.equals(id)) continue;
                    } catch (IllegalArgumentException ex) { continue; }
                    String title = extractJsonString(content, "title");
                    common.dataClasses.Kanban k = new common.dataClasses.Kanban(id, title != null ? title : "Kanban");
                    // Optionally parse visibility or other fields here
                    return k;
                }
            }
        } catch (Exception ignore) {}
        return null;
    }

    private static String extractJsonString(String json, String key) {
        if (json == null) return null;
        String pattern = "\"" + key + "\"" + "\\s*:\\s*" + "\""; // "key": "
        int idx = json.indexOf(pattern);
        if (idx < 0) return null;
        int start = idx + pattern.length();
        int end = json.indexOf("\"", start);
        if (end < 0) return null;
        return json.substring(start, end);
    }

    private static String coalesce(String a, String b) { return (a != null && !a.isBlank()) ? a : b; }
}
