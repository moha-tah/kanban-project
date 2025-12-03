package client.comm.messages;

import java.util.Optional;
import common.dataClasses.LightUser;

public class MessageResponseProfile extends Message {
    private static final long serialVersionUID = 1L;

    private final LightUser profile;

    public MessageResponseProfile(LightUser profile) {
        this.profile = profile;
    }

    @Override
    public Optional<Message> handle() {
        try {
            // Access client context through the getter (required!)
            var ctx = getClientContext();

            if (ctx != null && ctx.getMainInterface() != null) {
                ctx.getMainInterface().displayProfile(profile);
            }

        } catch (Throwable t) {
            java.util.logging.Logger.getLogger(MessageResponseProfile.class.getName())
                .log(java.util.logging.Level.SEVERE,
                     "Error handling MessageResponseProfile", t);
        }

        // No response message needed
        return Optional.empty();
    }
}
