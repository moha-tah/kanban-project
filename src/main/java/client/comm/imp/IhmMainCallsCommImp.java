package client.comm.imp;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.comm.CommCoreClient;
import client.comm.messages.ConnectionRequest;
import client.MainApp;
import common.dataClasses.User;
import client.comm.messages.AskAddListModifiers;
import client.comm.messages.ConnectionRequest;
import client.comm.messages.Logout;
import client.comm.messages.NotifyDecision;
import client.comm.messages.PermissionResponse;
import client.comm.messages.RequestKanban;
import client.comm.messages.RequestModification;
import client.comm.messages.RequestPermission;
import client.interfaces.IhmMainCallsComm; // Import ajouté
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.Modification;

public class IhmMainCallsCommImp implements IhmMainCallsComm {
    private static final Logger LOGGER = Logger.getLogger(IhmMainCallsCommImp.class.getName());
    private final CommCoreClient commCore;

    public IhmMainCallsCommImp(CommCoreClient commCore) {
        this.commCore = Objects.requireNonNull(commCore);
    }

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

    @Override
    public void askListModifiers(LightUser LightUserId) {
        // Breakpoint suggestion: inspect LightUserId
    }

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

    @Override
    public boolean connect(String host, int port) {
        // Breakpoint: inspect host/port before connection attempt
        return commCore.connect_host_port(host, port);
    }

    @Override
    public void connectionRequest(LightUser user, List<LightKanban> kanbans) {
        connectServer(user, kanbans);
    }

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

    @Override
    public void askKanban(LightKanban LightKanbanId) {
        // Add implementation + breakpoint to trace request flow
    }

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