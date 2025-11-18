package client.comm.messages;

import client.comm.CommCoreClient;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class MessageConnectionRequest extends Message implements Serializable {

    private final LightUser user;
    private final List<LightKanban> kanbans;

    public MessageConnectionRequest(LightUser user, List<LightKanban> kanbans) {
        this.user = user;
        this.kanbans = kanbans;
    }

    @Override
    public Optional<Message> handle() {
        return Optional.empty();
    }

    public LightUser getUser() { return user; }
    public List<LightKanban> getKanbans() { return kanbans; }
}