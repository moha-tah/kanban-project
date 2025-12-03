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
            // Deliver profile to the UI
            if (clientContext != null &&
                clientContext.getMainInterface() != null) {

                clientContext.getMainInterface().displayProfile(profile);
            }
        }
        catch (Throwable t) {
            java.util.logging.Logger.getLogger(MessageResponseProfile.class.getName())
                .log(java.util.logging.Level.SEVERE,
                     "Error handling MessageResponseProfile", t);
        }

        return Optional.empty(); // no further messages
    }
}
