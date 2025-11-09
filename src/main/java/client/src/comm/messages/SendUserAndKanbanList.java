package client.src.comm.messages;

import java.util.List;
import java.util.Optional;
import common.src.dataClasses.LightUser;
import common.src.dataClasses.LightKanban;

/**
 * Server -> Client: sends the current users and kanbans lists.
 */
public class SendUserAndKanbanList extends Message {
    private static final long serialVersionUID = 1L;

    private final List<LightUser> users;
    private final List<LightKanban> kanbans;

    public SendUserAndKanbanList(List<LightUser> users, List<LightKanban> kanbans) {
        this.users = users;
        this.kanbans = kanbans;
    }

    public List<LightUser> getUsers() {
        return users;
    }

    public List<LightKanban> getKanbans() {
        return kanbans;
    }

    @Override
    public Optional<Message> handle() throws Exception {
        // Client-side will consume this to update its state; no reply needed.
        return Optional.empty();
    }
}
