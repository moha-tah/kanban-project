package client.comm.imp;

import client.comm.CommCoreClient;
import client.comm.messages.MessageConnectionRequest;
import client.interfaces.IhmMainCallsComm;


import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.io.IOException;
import java.util.List;

import client.comm.messages.ConnectionRequest;
import client.comm.messages.AskAddListModifiers;
import client.comm.messages.RequestKanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

public class IhmMainCallsCommImp implements IhmMainCallsComm {
    private final CommCoreClient commCore;

    public IhmMainCallsCommImp(CommCoreClient commCore) {
        this.commCore = Objects.requireNonNull(commCore);
    }


    @Override
    public void logout(UUID LightUserId) {
    }

    @Override
    public void askListModifiers(UUID LightUserId) {
    }

    @Override
    public void askAddListModifiers(UUID userId, UUID kanbanId) {
        System.out.println("COMM IMP: Envoi demande ajout modificateur...");
        
        AskAddListModifiers msg = new AskAddListModifiers(userId, kanbanId);
        try {
            if (commCore.getMsgSender() != null) {
                commCore.sendMessage(msg);
                System.out.println("COMM IMP: Demande envoyée." + msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendPermissionRequest(UUID LightUserId, UUID LightKanbanId) {
    }

    @Override
    public void sendPermissionResponse(UUID LightUserId, UUID LightKanbanId, boolean accepted) {
    }


    @Override
    public void connectServer(LightUser user, List<LightKanban> kanbans) {

        // 1. On encapsule les données dans le Message qu'on vient de créer
        ConnectionRequest msg = new ConnectionRequest(user, kanbans);

        // 2. On envoie le message au serveur
        try {
            if (commCore.getMsgSender() != null) {
                commCore.sendMessage(msg);
            } else {
                System.err.println("ERREUR: Impossible d'envoyer la demande de connexion (Socket non connecté ?)");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERREUR: Problème réseau lors de la connexion.");
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
    public void getKanban(UUID LightKanbanId, UUID LightUserId) {
        // 1. Création du message
        RequestKanban msg = new RequestKanban(LightUserId, LightKanbanId);
        
        // 2. Envoi réseau
        try {
            commCore.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendRequestModification(LightUser LightUser, UUID CardId, Object newStatus) {
    }
}