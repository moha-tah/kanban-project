package client.comm.imp;

import client.comm.CommCoreClient;
import client.comm.messages.MessageConnectionRequest;
import client.interfaces.IhmMainCallsComm;


import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.List;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

public class IhmMainCallsCommImp implements IhmMainCallsComm {
    private final CommCoreClient comm;

    public IhmMainCallsCommImp(CommCoreClient comm) {
        this.comm = Objects.requireNonNull(comm);
    }


    @Override
    public void logout(UUID LightUserId) {
    }

    @Override
    public void askListModifiers(UUID LightUserId) {
    }

    @Override
    public void askAddListModifiers(UUID LightUserId) {
        askListModifiers(LightUserId);
    }

    @Override
    public void sendPermissionRequest(UUID LightUserId, UUID LightKanbanId) {
    }

    @Override
    public void sendPermissionResponse(UUID LightUserId, UUID LightKanbanId, boolean accepted) {
    }

    @Override
    public void connectServer(LightUser user, List<LightKanban> kanbans) {
        try {
            MessageConnectionRequest msg = new MessageConnectionRequest(user, kanbans);
            comm.sendMessage(msg);
        } catch (IOException e) {
            System.err.println("[COMM] Erreur lors de l'envoi de MessageConnectionRequest : " + e.getMessage());
        }
    }

    @Override
    public void connectionRequest(LightUser user, List<LightKanban> kanbans) {
        connectServer(user, kanbans);
    }

    @Override
    public void notifyDecision(UUID LightUserId, UUID LightKanbanId, boolean accepted) {
    }

    @Override
    public void notifyEditions(LightKanban LightKanban) {
    }

    @Override
    public void askKanban(UUID LightKanbanId) {
    }
    @Override
    public void connectToServer(UUID LightUserId, List<LightKanban> listKanbans) {
    }
    @Override
    public void getKanban(UUID LightKanbanId) {
    }

    public void sendRequestModification(LightUser LightUser, UUID CardId, Object newStatus) {
    }
}