package client.comm.messages;

import java.util.Optional;

import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import server.ServerContext;
import server.interfaces.CommCallsDataServer;

public class SendNewKanban extends Message {
    private static final long serialVersionUID = 1L;

    private final Kanban newKanban; // Le kanban complet créé par l'utilisateur

    public SendNewKanban(Kanban newKanban) {
        this.newKanban = newKanban;
    }

    @Override
    public Optional<Message> handle() {

        try {
            // Accès direct au contexte serveur (méthode statique)
            CommCallsDataServer dataServer = ServerContext.getData();

            if (dataServer != null) {
                System.out.println("SERVEUR: Demande de création de Kanban reçue.");

                LightKanban created = dataServer.saveKanban(newKanban);

                if (created != null) {
                    System.out.println("SERVEUR: Kanban créé (ID=" + created.getId() + ")");

                    // Message de retour vers le client
                    return Optional.of(new NotifyKanbanCreated(created));
                }
            }

        } catch (Throwable ignored) {
        }

        return Optional.empty();
    }
}