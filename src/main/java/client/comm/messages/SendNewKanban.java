package client.comm.messages;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
// RETIRÉ : import server.ServerContext; (Cause du crash client)
// RETIRÉ : import server.interfaces.CommCallsDataServer;

/**
 * Message envoyé par un client pour créer un nouveau kanban sur le serveur.
 * 
 * Ce message est traité côté serveur qui sauvegarde le kanban et déclenche
 * un broadcast pour notifier tous les clients. Le serveur répond avec
 * un message {@link NotifyKanbanCreated} pour confirmer la création.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see Message
 * @see NotifyKanbanCreated
 */
public class SendNewKanban extends Message {
    private static final long serialVersionUID = 1L;

    /**
     * Le kanban complet à créer sur le serveur.
     */
    private final Kanban newKanban;

    /**
     * Constructeur du message de création de kanban.
     * 
     * @param newKanban Le kanban complet à créer (ne doit pas être null)
     */
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
                    } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        System.err.println("SERVEUR: Impossible de déclencher le broadcast : " + e.getMessage());
                    }

                    return Optional.of(new NotifyKanbanCreated(created));
                }
            }
        } catch (ClassNotFoundException e) {
            // Normal : Le client n'a pas ServerContext, on ignore.
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException t) {
            java.util.logging.Logger.getLogger(SendNewKanban.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Erreur traitement SendNewKanban", t);
        }
        return Optional.empty();
    }
}