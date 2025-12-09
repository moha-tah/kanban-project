package client.comm.messages;

import java.util.Optional;

import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
// RETIRÉ : import server.ServerContext; (Cause du crash client)
// RETIRÉ : import server.interfaces.CommCallsDataServer;

public class SendNewKanban extends Message {
    private static final long serialVersionUID = 1L;

    private final Kanban newKanban;

    public SendNewKanban(Kanban newKanban) {
        this.newKanban = newKanban;
    }

    @Override
    public Optional<Message> handle() {
        try {
            // On charge dynamiquement ServerContext car le Client ne connaît pas cette classe
            Class<?> contextClass = Class.forName("server.ServerContext");
            java.lang.reflect.Method getDataMethod = contextClass.getMethod("getData");
            Object dataServerObj = getDataMethod.invoke(null);

            if (dataServerObj != null) {
                // On utilise l'interface via cast ou réflexion si le package server.interfaces est partagé
                server.interfaces.CommCallsDataServer dataServer = (server.interfaces.CommCallsDataServer) dataServerObj;

                System.out.println("SERVEUR: Réception nouveau Kanban : " + newKanban.getTitle());
                LightKanban created = dataServer.saveKanban(newKanban);

                if (created != null) {
                    try {
                        Class<?> commClass = Class.forName("server.comm.CommCoreServer");
                        java.lang.reflect.Method triggerMethod = commClass.getMethod("triggerBroadcast");
                        triggerMethod.invoke(null);
                        System.out.println("SERVEUR: Broadcast déclenché après création.");
                    } catch (Exception e) {
                        System.err.println("SERVEUR: Impossible de déclencher le broadcast : " + e.getMessage());
                    }

                    return Optional.of(new NotifyKanbanCreated(created));
                }
            }
        } catch (ClassNotFoundException e) {
            // Normal : Le client n'a pas ServerContext, on ignore.
        } catch (Throwable t) {
            java.util.logging.Logger.getLogger(SendNewKanban.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Erreur traitement SendNewKanban", t);
        }
        return Optional.empty();
    }
}