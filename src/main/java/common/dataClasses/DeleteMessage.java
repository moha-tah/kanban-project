package common.dataClasses;

import java.util.List;
import java.util.UUID;

public class DeleteMessage extends Modification {
    UUID message;
    Message previousMessage = null;

    // Constructeur
    public DeleteMessage(UUID message) {
        super();
        this.message = message;
    }

    // Getters
    public UUID getMessage() {
        return message;
    }
    public Message getPreviousMessage() {
        return previousMessage;
    }

    // Setters
    public void setMessage(UUID message) {
        this.message = message;
    }
    public void setPreviousMessage(Message previousMessage) {
        this.previousMessage = previousMessage;
    }

    @Override
    public Kanban execute(Kanban targetKanban) {
        List<Message> messageList = targetKanban.getMessages();
        this.previousMessage = messageList.stream()
                .filter(m -> m.getId().equals(message))
                .findFirst()
                .orElse(null);
        messageList.removeIf(m -> m.getId().equals(message));
        targetKanban.setMessages(messageList);
        return targetKanban;
    }

    @Override
    public Kanban undo(Kanban targetKanban) {
        CreateMessage undoModification = new CreateMessage(previousMessage);
        return undoModification.execute(targetKanban);
    }

    @Override
    public String toString() {
        return "DeleteMessage{" +
                "id=" + getId() +
                ", message=" + message +
                '}';
    }



}
