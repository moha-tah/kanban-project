package client.comm.messages;

import java.util.Optional;
import java.util.UUID;
import common.dataClasses.LightUser;

public class MessageRequestProfile extends Message {
    private final UUID targetUserId;  
    private final UUID requesterId;

    public MessageRequestProfile(UUID targetUserId, UUID requesterId) {
        this.targetUserId = targetUserId;
        this.requesterId = requesterId;
    }

    @Override
    public Optional<Message> handle() {
        try {
            Class<?> contextClass = Class.forName("server.ServerContext");
            var getData = contextClass.getMethod("getData");
            Object dataServerObj = getData.invoke(null);

            if (dataServerObj != null) {
                server.interfaces.CommCallsDataServer data =
                    (server.interfaces.CommCallsDataServer) dataServerObj;

                LightUser profile = data.getUserProfile(targetUserId);

                return Optional.of(new MessageResponseProfile(profile));
            }
        } catch (ClassNotFoundException e) {
            // normal côté client
        } catch (Throwable t) {
            java.util.logging.Logger.getLogger(MessageRequestProfile.class.getName())
                .log(java.util.logging.Level.SEVERE, "Error processing MessageRequestProfile", t);
        }

        return Optional.empty();
    }
}
