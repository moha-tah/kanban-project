package client.comm.imp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.comm.CommCoreClient;
import client.comm.messages.CloseKanban;
import client.comm.messages.SendKanban;
import client.comm.messages.SendNewKanban;
import client.comm.messages.SendUpdateUserList;
import client.interfaces.DataCallsComm;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.User;

public class DataCallsCommImp implements DataCallsComm {
    private final CommCoreClient commCore;
    private static final Logger LOGGER = Logger.getLogger(DataCallsCommImp.class.getName());

    public DataCallsCommImp(CommCoreClient commCore) {
        this.commCore = Objects.requireNonNull(commCore);
    }

    @Override
    public void askDeleteKanban(LightKanban lightKanbanId, LightUser lightUserId) {
        // Envoi de la demande de fermeture/suppression au serveur
        try {
            if (commCore.getMsgSender() != null) {
                // On utilise CloseKanban pour signaler la suppression/fermeture
                CloseKanban msg = new CloseKanban(lightKanbanId, lightUserId);
                commCore.getMsgSender().send(msg);
                LOGGER.info("COMM IMP: Demande de suppression du Kanban envoyée.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "DataCallsCommImp: Echec envoi demande suppression Kanban", e);
        }
    }

    @Override
    public void sendKanban(Kanban kanban) {
        // Cette méthode est utilisée pour mettre à jour un Kanban existant (ex: ajout d'un user)
        // ou envoyer un état complet.
        try {
            if (commCore.getMsgSender() != null) {
                // Utilisation de SendKanban pour les mises à jour génériques
                SendKanban msg = new SendKanban(kanban);
                commCore.getMsgSender().send(msg);
                LOGGER.info("COMM IMP: Kanban envoyé/mis à jour au serveur.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "DataCallsCommImp: Echec envoi Kanban", e);
        }
    }

    public void sendNewKanban(Kanban kanban) {
        // Méthode spécifique si besoin de distinguer création vs update
        try {
            if (commCore.getMsgSender() != null) {
                SendNewKanban msg = new SendNewKanban(kanban);
                commCore.getMsgSender().send(msg);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "DataCallsCommImp: Echec envoi nouveau Kanban", e);
        }
    }

    @Override
    public void addAuthorizedUser(LightKanban kanbanId, LightUser userId) {
        try {
            // Envoi basique sous forme de liste d'objets (protocole simple)
            commCore.sendMessage(java.util.Arrays.asList("addAuthorizedUser", kanbanId, userId));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "DataCallsCommImp: Echec envoi addAuthorizedUser", e);
        }
    }

    @Override
    public void sendUpdateUserList(User user) {
        try {
            if (commCore.getMsgSender() != null) {
                // 1. Conversion des Kanbans complets en LightKanbans
                List<LightKanban> lightKanbans = new ArrayList<>();
                if (user.getMyKanban() != null) {
                    for (Kanban k : user.getMyKanban()) {
                        lightKanbans.add(k.getLightKanban());
                    }
                }

                // 2. Création du message de mise à jour
                // On passe 'user' car User étend généralement LightUser
                SendUpdateUserList msg = new SendUpdateUserList(user, lightKanbans);

                // 3. Envoi
                commCore.getMsgSender().send(msg);
                LOGGER.info("COMM IMP: Profil mis à jour envoyé au serveur.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "DataCallsCommImp: Echec envoi mise à jour profil", e);
        }
    }
}