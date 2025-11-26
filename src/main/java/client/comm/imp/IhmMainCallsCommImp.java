package client.comm.imp;

import client.comm.CommCoreClient;
import client.comm.messages.RequestPermission;
import client.comm.messages.PermissionResponse;
import client.comm.messages.NotifyDecision;
import client.interfaces.IhmMainCallsComm;

import java.util.Objects;
import java.util.UUID;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.comm.messages.ConnectionRequest;
import client.comm.messages.AskAddListModifiers;
import client.comm.messages.RequestKanban;
import client.comm.messages.LogoutMessage; // Import ajouté
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

public class IhmMainCallsCommImp implements IhmMainCallsComm {
    private static final Logger LOGGER = Logger.getLogger(IhmMainCallsCommImp.class.getName());
    private final CommCoreClient commCore;

    public IhmMainCallsCommImp(CommCoreClient commCore) {
        this.commCore = Objects.requireNonNull(commCore);
    }

    @Override
    public void logout(LightUser user) {
        if (user == null) return;

        LOGGER.info(() -> "Sending logout request for user: " + user.getUsername());

        // Création et envoi du message de déconnexion
        LogoutMessage msg = new LogoutMessage(user);

        try {
            if (commCore.getMsgSender() != null) {
                commCore.sendMessage(msg);

                // Fermer la connexion socket proprement côté client
                commCore.disconnect();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error sending logout message", e);
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
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in sendPermissionRequest", e);
        }
    }

    @Override
    public void sendPermissionResponse(LightUser LightUserId, LightKanban LightKanbanId, boolean accepted) {
        try {
            PermissionResponse msg = new PermissionResponse(LightUserId, LightKanbanId, accepted);
            commCore.sendMessage(msg);
            LOGGER.fine(() -> "sendPermissionResponse user=" + LightUserId + " kanban=" + LightKanbanId + " accepted=" + accepted);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in sendPermissionResponse", e);
        }
    }

    @Override
    public void connectServer(LightUser user, List<LightKanban> kanbans) {
        LOGGER.fine(() -> "Sending ConnectionRequest with " + (kanbans != null ? kanbans.size() : 0) + " kanbans");
        ConnectionRequest msg = new ConnectionRequest(user, kanbans);
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
            LOGGER.fine(() -> "notifyDecision user=" + LightUserId + " kanban=" + LightKanbanId + " accepted=" + accepted);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in notifyDecision", e);
        }
    }

    @Override
    public void notifyEditions(LightKanban LightKanban) {
        // Place breakpoint to inspect LightKanban state before sending any edition notifications
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
        } catch (Exception e) {
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
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in getKanban", e);
        }
    }

    public void sendRequestModification(LightUser LightUser, UUID CardId, Object newStatus) {
        // Breakpoint: inspect parameters to trace modification request
    }
}