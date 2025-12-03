package client.comm.messages;

import java.util.Optional;

public class NotifyDeleteKanbanError extends Message {
  private static final long serialVersionUID = 1L;

  private final String errorMessage;

  public NotifyDeleteKanbanError(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  @Override
  public Optional<Message> handle() {
    // Gestion côté client de l'erreur de suppression
    System.err.println("ERREUR CLIENT: Échec de la suppression du kanban : " + errorMessage);

    // TODO: Notifier l'IHM pour afficher un message d'erreur à l'utilisateur

    return Optional.empty();
  }
}
