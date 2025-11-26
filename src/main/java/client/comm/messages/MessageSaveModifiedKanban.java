package client.comm.messages;

import common.dataClasses.LightKanban;
import common.dataClasses.Modification;
import client.comm.messages.Message;

import java.util.UUID;

public class MessageSaveModifiedKanban extends Message {
    private final LightKanban kanban;
    private final Modification modification;

    public MessageSaveModifiedKanban(LightKanban kanban, Modification modification) {
        this.kanban = kanban;
        this.modification = modification;
    }

    @Override
    public Message handle() {
        // Send to the servidor
        return null;
    }

    public LightKanban getKanban() {
        return kanban;
    }

    public Modification getModification() {
        return modification;
    }
}
