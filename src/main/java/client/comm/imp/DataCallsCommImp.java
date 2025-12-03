package client.comm.imp;

import java.util.UUID;
import java.util.Objects;
import java.io.IOException;

import client.comm.CommCoreClient;
import client.interfaces.DataCallsComm;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.Modification;
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
                commCore.getMsgSender().send(msg);
            }
        } catch (IOException e) {
            java.util.logging.Logger.getLogger(DataCallsCommImp.class.getName())
                    .log(java.util.logging.Level.SEVERE, "DataCallsCommImp: Echec envoi nouveau Kanban", e);
        }
    }

    // -- Modifier Kanban - Save in Servidor --
    @Override
    public void saveModifiedKanban(Modification modification, LightKanban kanban) {
        System.out.println("COMM IMP: Sending modified Kanban to server...");
        
        MessageSaveModifiedKanban msg = new MessageSaveModifiedKanban(kanban, modification);
        
        try {
             if (commCore.getMsgSender() != null) {
                 commCore.getMsgSender().send(msg);
             }
        } catch (IOException e) {
            java.util.logging.Logger.getLogger(DataCallsCommImp.class.getName())
                 .log(java.util.logging.Level.SEVERE, "DataCallsCommImp: Failed to send modified Kanban", e);
        }
    }

}
     

     //Voir Profile Distant 
     @Override
     public void getDistantProfile(UUID targetUserId) {
        UUID requesterId = commCore.getClientContext() .getLocalUser() .getId();
        
        MessageRequestProfile msg = new MessageRequestProfile(targetUserId, requesterId);
        
        try {
             if (commCore.getMsgSender() != null) {
                commCore.getMsgSender().send(msg);
             }
        } catch (IOException e) {
        java.util.logging.Logger.getLogger(DataCallsCommImp.class.getName())
            .log(java.util.logging.Level.SEVERE,
                 "Failed to send distant profile request", e);
    }
}
