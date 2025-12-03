package client.comm.imp;

import client.comm.CommCoreClient;
import client.comm.messages.RequestModification;
import client.interfaces.IhmKanbanCallsComm;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import common.dataClasses.Modification;

public class IhmKanbanCallsCommImp implements IhmKanbanCallsComm {
    private static final Logger LOGGER = Logger.getLogger(IhmKanbanCallsCommImp.class.getName());
    private final CommCoreClient comm;

    public IhmKanbanCallsCommImp(CommCoreClient comm) {
        this.comm = Objects.requireNonNull(comm);
    }

    @Override
    public void closingKanban(LightKanban LightKanbanId, LightUser LightUserId) {
        // TODO: Implémenter la fermeture de kanban
    }

    @Override
    public void sendRequestModification(LightUser user,Modification myModification ) {
        if (user == null || myModification == null ) {
            LOGGER.warning("Paramètres invalides pour sendRequestModification");
            return;
        }

        /*LOGGER.info(() -> "Envoi demande de modification carte " + cardId + " vers " + newStatus + 
                     " par " + user.getUsername());

        try {
            RequestModification msg = new RequestModification(user, myModification);
            
            if (comm.getMsgSender() != null) {
                comm.sendMessage(msg);
                LOGGER.fine("Demande de modification envoyée avec succès");
            } else {
                LOGGER.warning("Message sender non initialisé");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur réseau lors de l'envoi de la modification", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur inattendue lors de la modification", e);
        }*/
    }
}