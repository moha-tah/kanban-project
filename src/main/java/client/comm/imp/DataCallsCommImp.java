package client.comm.imp;

import java.util.Objects;
import java.io.IOException;

import client.comm.CommCoreClient;
import client.interfaces.DataCallsComm;
import common.dataClasses.Kanban;
import client.comm.messages.SendNewKanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

/**
 * Implémentation de l'interface {@link DataCallsComm}.
 * 
 * Cette classe gère les appels de communication depuis la couche données
 * vers la couche de communication. Elle permet d'envoyer des kanbans
 * et de gérer les utilisateurs autorisés.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see DataCallsComm
 * @see CommCoreClient
 */
public class DataCallsCommImp implements DataCallsComm {
    /**
     * Client de communication utilisé pour envoyer les messages au serveur.
     */
    private final CommCoreClient commCore;

    /**
     * Constructeur de l'implémentation.
     * 
     * @param commCore Le client de communication à utiliser (ne doit pas être null)
     * @throws NullPointerException si commCore est null
     */
    public DataCallsCommImp(CommCoreClient commCore) {
        this.commCore = Objects.requireNonNull(commCore);
    }

    /**
     * Demande la suppression d'un kanban au serveur.
     * 
     * Cette méthode est actuellement non implémentée.
     * 
     * @param LightKanbanId Le kanban à supprimer
     * @param LightUserId L'utilisateur qui demande la suppression
     */
    @Override
    public void askDeleteKanban(LightKanban LightKanbanId, LightUser LightUserId) {
    }

    /**
     * Envoie un kanban complet au serveur.
     * 
     * Cette méthode crée et envoie un message {@link SendNewKanban} au serveur
     * pour synchroniser un kanban. Les erreurs réseau sont loggées mais
     * n'interrompent pas l'exécution.
     * 
     * @param kanban Le kanban complet à envoyer au serveur (ne doit pas être null)
     */
    @Override
    public void sendKanban(Kanban kanban) { // Ou "uploadKanbans" selon ton interface exacte
        System.out.println("COMM IMP: Envoi du nouveau Kanban au serveur...");

        // 1. Création du message
        SendNewKanban msg = new SendNewKanban(kanban);

        // 2. Envoi via le Core
        try {
            if (commCore.getMsgSender() != null) {
                commCore.getMsgSender().send(msg);
            }
        } catch (IOException e) {
            java.util.logging.Logger.getLogger(DataCallsCommImp.class.getName())
                    .log(java.util.logging.Level.SEVERE, "DataCallsCommImp: Echec envoi nouveau Kanban", e);
        }
    }

    /**
     * Ajoute un utilisateur autorisé à un kanban.
     * 
     * Cette méthode envoie un message au serveur pour ajouter un utilisateur
     * à la liste des utilisateurs autorisés à accéder à un kanban.
     * Les erreurs réseau sont loggées mais n'interrompent pas l'exécution.
     * 
     * @param kanbanId Le kanban pour lequel ajouter l'utilisateur
     * @param userId L'utilisateur à autoriser
     */
    @Override
    public void addAuthorizedUser(LightKanban kanbanId, LightUser userId) {
        try {
            commCore.sendMessage(java.util.Arrays.asList("addAuthorizedUser", kanbanId, userId));
        } catch (IOException e) {
            java.util.logging.Logger.getLogger(DataCallsCommImp.class.getName())
                    .log(java.util.logging.Level.SEVERE, "DataCallsCommImp: Echec envoi addAuthorizedUser", e);
        }
    }
}