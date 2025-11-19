package client.comm.imp;

import java.util.UUID;
import java.util.Objects;
import java.io.IOException;

import client.comm.CommCoreClient;
import client.interfaces.DataCallsComm;
import common.dataClasses.Kanban;
import client.comm.messages.SendNewKanban;

public class DataCallsCommImp implements DataCallsComm {
    private final CommCoreClient commCore;

    public DataCallsCommImp(CommCoreClient commCore) {
        this.commCore = Objects.requireNonNull(commCore);
    }

    @Override
    public void askDeleteKanban(UUID LightKanbanId, UUID LightUserId) {
    }

    @Override
    public void sendKanban(Kanban kanban) { // Ou "uploadKanbans" selon ton interface exacte
        System.out.println("COMM IMP: Envoi du nouveau Kanban au serveur...");

        // 1. Création du message
        SendNewKanban msg = new SendNewKanban(kanban);

        // 2. Envoi via le Core
        try {
            if (commCore.getMsgSender() != null) {
                commCore.sendMessage(msg);
            }
        } catch (IOException e) {
            java.util.logging.Logger.getLogger(DataCallsCommImp.class.getName())
                    .log(java.util.logging.Level.SEVERE, "DataCallsCommImp: Echec envoi nouveau Kanban", e);
        }
    }
}