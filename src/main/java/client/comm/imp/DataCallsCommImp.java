package client.comm.imp;

import java.util.Objects;
import java.io.IOException;

import client.comm.CommCoreClient;
import client.interfaces.DataCallsComm;
import common.dataClasses.Kanban;
import client.comm.messages.AskDeleteKanban;
import client.comm.messages.SendNewKanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

public class DataCallsCommImp implements DataCallsComm {
    private final CommCoreClient commCore;

    public DataCallsCommImp(CommCoreClient commCore) {
        this.commCore = Objects.requireNonNull(commCore);
    }

    @Override
    public void askDeleteKanban(LightKanban LightKanbanId, LightUser LightUserId) {
        System.out.println("COMM IMP: Envoi de la demande de suppression du Kanban : " +
                (LightKanbanId != null ? LightKanbanId.getTitle() : "Inconnu"));

        AskDeleteKanban msg = new AskDeleteKanban(LightUserId, LightKanbanId);

        // Envoi via le Core
        try {
            if (commCore.getMsgSender() != null) {
                commCore.getMsgSender().send(msg);
            }
        } catch (IOException e) {
            java.util.logging.Logger.getLogger(DataCallsCommImp.class.getName())
                    .log(java.util.logging.Level.SEVERE,
                            "DataCallsCommImp: Échec de l'envoi de la demande de suppression", e);
        }
    }

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