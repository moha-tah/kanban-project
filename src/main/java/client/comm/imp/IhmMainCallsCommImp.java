package client.comm.imp;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.MainApp;
import client.comm.CommCoreClient;
import client.comm.messages.AskAddListModifiers;
import client.comm.messages.ConnectionRequest;
import client.comm.messages.Logout;
import client.comm.messages.NotifyDecision;
import client.comm.messages.PermissionResponse;
import client.comm.messages.RequestKanban;
import client.comm.messages.RequestModification;
import client.comm.messages.RequestPermission;
import client.interfaces.IhmMainCallsComm;
import common.dataClasses.Kanban; // Import ajouté
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.User;
import common.dataClasses.Modification;

/**
 * Implémentation de l'interface {@link IhmMainCallsComm}.
 * 
 * Cette classe gère tous les appels de communication depuis l'interface utilisateur
 * principale vers la couche de communication. Elle permet d'envoyer des messages
 * au serveur pour gérer les connexions, déconnexions, permissions, kanbans, etc.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see IhmMainCallsComm
 * @see CommCoreClient
 */
public class IhmMainCallsCommImp implements IhmMainCallsComm {
    /**
     * Logger pour les messages de log de cette classe.
     */
    private static final Logger LOGGER = Logger.getLogger(IhmMainCallsCommImp.class.getName());
    
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
    public IhmMainCallsCommImp(CommCoreClient commCore) {
        this.commCore = Objects.requireNonNull(commCore);
    }

    /**
     * Déconnecte un utilisateur du serveur.
     * 
     * Cette méthode envoie un message {@link Logout} au serveur et ferme
     * proprement la connexion socket côté client. En cas d'erreur réseau,
     * la déconnexion locale est effectuée de toute façon.
     * 
     * @param user L'utilisateur à déconnecter (ne doit pas être null)
     */
    @Override
    public void logout(LightUser user) {
        if (user == null) {
            LOGGER.warning("Tentative de déconnexion avec utilisateur null");
            return;
        }

        LOGGER.info(() -> "Sending logout request for user: " + user.getUsername());

        try {
            // Création et envoi du message de déconnexion
            Logout msg = new Logout(user);

            if (commCore.getMsgSender() != null) {
                commCore.sendMessage(msg);
                LOGGER.info("Logout message sent successfully");
            } else {
                LOGGER.warning("Message sender not initialized");
            }

            // Fermer la connexion socket proprement côté client
            commCore.disconnect();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Network error during logout", e);
            // En cas d'erreur réseau, déconnexion locale
            commCore.disconnect();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during logout", e);
            commCore.disconnect();
        }
    }

    /**
     * Demande la liste des modificateurs pour un utilisateur.
     * 
     * Cette méthode est actuellement non implémentée.
     * 
     * @param LightUserId L'utilisateur pour lequel demander la liste des modificateurs
     */
    @Override
    public void askListModifiers(LightUser LightUserId) {
        // Breakpoint suggestion: inspect LightUserId
    }

    /**
     * Demande l'ajout d'un utilisateur à la liste des modificateurs d'un kanban.
     * 
     * Cette méthode envoie un message {@link AskAddListModifiers} au serveur
     * pour demander l'ajout d'un utilisateur à la liste des personnes autorisées
     * à modifier un kanban.
     * 
     * @param userId L'utilisateur à ajouter comme modificateur
     * @param kanbanId Le kanban pour lequel ajouter le modificateur
     */
    @Override
    public void askAddListModifiers(LightUser userId, LightKanban kanbanId) {
        LOGGER.fine(() -> "Sending AskAddListModifiers user=" + userId + " kanban=" + kanbanId);
        AskAddListModifiers msg = new AskAddListModifiers(userId, kanbanId);
        try {
            if (commCore.getMsgSender() != null) {
                commCore.sendMessage(msg);
                LOGGER.fine(() -> "AskAddListModifiers sent: " + msg);
            } else {
                LOGGER.warning("Message sender not initialized for AskAddListModifiers");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error sending AskAddListModifiers", e);
        }
    }

    /**
     * Envoie une demande de permission d'accès à un kanban.
     * 
     * Cette méthode crée et envoie un message {@link RequestPermission} au serveur
     * pour demander l'autorisation d'accéder à un kanban.
     * 
     * @param LightUserId L'utilisateur qui demande la permission
     * @param LightKanbanId Le kanban pour lequel la permission est demandée
     */
    @Override
    public void sendPermissionRequest(LightUser LightUserId, LightKanban LightKanbanId) {
        try {
            RequestPermission msg = new RequestPermission(LightUserId.getId(), LightKanbanId.getId());
            commCore.sendMessage(msg);
            LOGGER.fine(() -> "sendPermissionRequest user=" + LightUserId + " kanban=" + LightKanbanId);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error in sendPermissionRequest", e);
        }
    }

    /**
     * Envoie une réponse à une demande de permission.
     * 
     * Cette méthode crée et envoie un message {@link PermissionResponse} au serveur
     * pour répondre à une demande de permission d'accès à un kanban.
     * 
     * @param LightUserId L'utilisateur qui répond à la demande
     * @param LightKanbanId Le kanban concerné par la demande
     * @param accepted true si la permission est accordée, false sinon
     */
    @Override
    public void sendPermissionResponse(LightUser LightUserId, LightKanban LightKanbanId, boolean accepted) {
        try {
            PermissionResponse msg = new PermissionResponse(LightUserId, LightKanbanId, accepted);
            commCore.sendMessage(msg);
            LOGGER.fine(() -> "sendPermissionResponse user=" + LightUserId + " kanban=" + LightKanbanId + " accepted="
                    + accepted);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error in sendPermissionResponse", e);
        }
    }

    /**
     * Connecte un utilisateur au serveur avec ses kanbans.
     * 
     * Cette méthode crée et envoie un message {@link ConnectionRequest} au serveur
     * pour établir la connexion d'un utilisateur. Elle récupère également l'utilisateur
     * complet depuis le port de données local si disponible.
     * 
     * @param user L'utilisateur à connecter
     * @param kanbans Liste des kanbans de l'utilisateur à synchroniser avec le serveur
     */
    @Override
    public void connectServer(LightUser user, List<LightKanban> kanbans) {
        LOGGER.fine(() -> "Sending ConnectionRequest with " + (kanbans != null ? kanbans.size() : 0) + " kanbans");
        User fullUser = null;
        try {
            if (MainApp.getCore() != null && MainApp.getCore().getDataPort() != null) {
                fullUser = MainApp.getCore().getDataPort().getLocalUser();
            }
        } catch (Throwable ignored) {
        }
        ConnectionRequest msg = new ConnectionRequest(user, kanbans, fullUser);
        try {
            if (commCore.getMsgSender() != null) {
                commCore.sendMessage(msg);
            } else {
                LOGGER.warning("Cannot send ConnectionRequest (sender not initialized)");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Network error during connectServer", e);
        }
    }

    /**
     * Établit une connexion au serveur avec l'adresse et le port spécifiés.
     * 
     * @param host L'adresse du serveur (ex: "localhost" ou une adresse IP)
     * @param port Le port sur lequel se connecter
     * @return true si la connexion a réussi, false sinon
     */
    @Override
    public boolean connect(String host, int port) {
        // Breakpoint: inspect host/port before connection attempt
        return commCore.connect_host_port(host, port);
    }

    /**
     * Envoie une demande de connexion au serveur.
     * 
     * Cette méthode est un alias de {@link #connectServer(LightUser, List)}.
     * 
     * @param user L'utilisateur à connecter
     * @param kanbans Liste des kanbans de l'utilisateur
     */
    @Override
    public void connectionRequest(LightUser user, List<LightKanban> kanbans) {
        connectServer(user, kanbans);
    }

    /**
     * Notifie le serveur d'une décision concernant une demande de permission.
     * 
     * Cette méthode envoie un message {@link NotifyDecision} au serveur pour
     * informer de l'acceptation ou du refus d'une demande de permission d'accès.
     * 
     * @param LightUserId L'utilisateur qui prend la décision
     * @param LightKanbanId Le kanban concerné par la décision
     * @param accepted true si la demande est acceptée, false sinon
     */
    @Override
    public void notifyDecision(LightUser LightUserId, LightKanban LightKanbanId, boolean accepted) {
        try {
            NotifyDecision msg = new NotifyDecision(LightUserId, LightKanbanId, accepted);
            commCore.sendMessage(msg);
            LOGGER.fine(
                    () -> "notifyDecision user=" + LightUserId + " kanban=" + LightKanbanId + " accepted=" + accepted);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error in notifyDecision", e);
        }
    }

    /**
     * Demande un kanban au serveur.
     * 
     * Cette méthode est actuellement non implémentée.
     * 
     * @param LightKanbanId L'identifiant du kanban à demander
     */
    @Override
    public void askKanban(LightKanban LightKanbanId) {
        // Add implementation + breakpoint to trace request flow
    }

    /**
     * Envoie un nouveau kanban au serveur.
     * 
     * Cette méthode crée et envoie un message {@link client.comm.messages.SendNewKanban}
     * au serveur pour créer un nouveau kanban sur le serveur.
     * 
     * @param kanban Le kanban complet à envoyer au serveur (ne doit pas être null)
     */
    @Override
    public void sendNewKanban(Kanban kanban) {
        try {
            LOGGER.fine(() -> "Sending new Kanban title=" + kanban.getTitle());
            client.comm.messages.SendNewKanban msg = new client.comm.messages.SendNewKanban(kanban);
            if (commCore.getMsgSender() != null) {
                commCore.sendMessage(msg);
            } else {
                LOGGER.warning("Message sender not initialized for SendNewKanban");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error in sendNewKanban", e);
        }
    }

    /**
     * Demande un kanban complet au serveur.
     * 
     * Cette méthode crée et envoie un message {@link RequestKanban} au serveur
     * pour récupérer un kanban complet à partir de sa version légère.
     * 
     * @param LightKanbanId La version légère du kanban à récupérer
     * @param LightUserId L'utilisateur qui demande le kanban
     */
    @Override
    public void getKanban(LightKanban LightKanbanId, LightUser LightUserId) {
        LOGGER.fine(() -> "getKanban request title=" + LightKanbanId.getTitle() + " id=" + LightKanbanId.getId());
        RequestKanban msg = new RequestKanban(LightKanbanId, LightUserId);
        try {
            commCore.sendMessage(msg);
            LOGGER.fine("RequestKanban sent");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error in getKanban", e);
        }
    }

    /**
     * Demande le profil distant d'un utilisateur au serveur.
     * 
     * Cette méthode crée et envoie un message {@link client.comm.messages.DistProfileRequest}
     * au serveur pour récupérer le profil public d'un autre utilisateur.
     * 
     * @param requester L'utilisateur qui fait la demande (ne doit pas être null)
     * @param requestedUserId L'identifiant UUID de l'utilisateur dont on veut le profil (ne doit pas être null)
     */
    @Override
    public void requestDistantProfile(LightUser requester, java.util.UUID requestedUserId) {
        if (requester == null || requestedUserId == null) {
            LOGGER.warning("Invalid parameters for requestDistantProfile");
            return;
        }
        try {
            client.comm.messages.DistProfileRequest msg = new client.comm.messages.DistProfileRequest(requester.getId(),
                    requestedUserId);
            commCore.sendMessage(msg);
            LOGGER.info(() -> "DistProfileRequest sent: requester=" + requester.getUsername() + ", target="
                    + requestedUserId);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Network error during requestDistantProfile", e);
        }
    }

    /**
     * Envoie une demande de modification au serveur.
     * 
     * Cette méthode crée et envoie un message {@link RequestModification}
     * au serveur pour demander l'application d'une modification sur une carte
     * du kanban. Les erreurs réseau sont loggées mais n'interrompent pas l'exécution.
     * 
     * @param user L'utilisateur qui demande la modification (ne doit pas être null)
     * @param myModification La modification à appliquer (ne doit pas être null)
     */
    public void sendRequestModification(LightUser user, Modification myModification) {
        if (user == null || myModification == null) {
            LOGGER.warning("Paramètres invalides pour sendRequestModification");
            return;
        }

        LOGGER.info(() -> "Envoi demande de modification carte ");

        try {
            RequestModification msg = new RequestModification(user, myModification);

            if (commCore.getMsgSender() != null) {
                commCore.sendMessage(msg);
                LOGGER.fine("Demande de modification envoyée avec succès");
            } else {
                LOGGER.warning("Message sender non initialisé");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur réseau lors de l'envoi de la modification", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur inattendue lors de la modification", e);
        }
    }
}