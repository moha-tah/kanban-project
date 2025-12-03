package client.comm.messages;

import java.util.Optional;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

public class AskDeleteKanban extends Message {
  private static final long serialVersionUID = 1L;

  private final LightUser user;
  private final LightKanban kanban;

  public AskDeleteKanban(LightUser user, LightKanban kanban) {
    this.user = user;
    this.kanban = kanban;
  }

  @Override
  public Optional<Message> handle() {
    try {
      // Chargement dynamique de ServerContext pour éviter le crash côté client
      Class<?> contextClass = Class.forName("server.ServerContext");
      java.lang.reflect.Method getDataMethod = contextClass.getMethod("getData");
      Object dataServerObj = getDataMethod.invoke(null);

      if (dataServerObj != null) {
        // Cast vers l'interface du serveur
        server.interfaces.CommCallsDataServer dataServer = (server.interfaces.CommCallsDataServer) dataServerObj;

        System.out.println(
            "SERVEUR: Réception demande de suppression du Kanban : "
                + (kanban != null ? kanban.getTitle() : "Inconnu")
                + " par l'utilisateur : "
                + (user != null ? user.getUsername() : "Inconnu"));

        // Appel de la couche Data pour supprimer le kanban
        dataServer.askDeleteKanban(user, kanban);

        // Déclenchement du broadcast pour notifier tous les clients
        try {
          Class<?> commClass = Class.forName("server.comm.CommCoreServer");
          java.lang.reflect.Method triggerMethod = commClass.getMethod("triggerBroadcast");
          triggerMethod.invoke(null);
          System.out.println("SERVEUR: Broadcast déclenché après suppression du kanban.");
        } catch (Exception e) {
          System.err.println(
              "SERVEUR: Impossible de déclencher le broadcast : " + e.getMessage());
        }
      }
    } catch (ClassNotFoundException e) {
      // Si le client n'a pas de ServerContext, on ignore.
    } catch (Throwable t) {
      java.util.logging.Logger.getLogger(AskDeleteKanban.class.getName())
          .log(
              java.util.logging.Level.SEVERE,
              "Erreur lors du traitement de la suppression du kanban",
              t);
    }
    return Optional.empty();
  }
}
