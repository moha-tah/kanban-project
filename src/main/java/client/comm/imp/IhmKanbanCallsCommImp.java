package client.comm.imp;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.comm.CommCoreClient;
import client.comm.messages.CloseKanban;
import client.comm.messages.RequestModification;
import client.interfaces.IhmKanbanCallsComm;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.Modification;

/**
 * Implémentation de l'interface {@link IhmKanbanCallsComm}.
 * 
 * Cette classe gère les appels de communication depuis l'interface utilisateur
 * du kanban vers la couche de communication. Elle permet d'envoyer des messages
 * au serveur pour fermer un kanban ou demander des modifications.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see IhmKanbanCallsComm
 * @see CommCoreClient
 */
public class IhmKanbanCallsCommImp implements IhmKanbanCallsComm {
    /**
     * Logger pour les messages de log de cette classe.
     */
    private static final Logger LOGGER = Logger.getLogger(IhmKanbanCallsCommImp.class.getName());
    
    /**
     * Client de communication utilisé pour envoyer les messages au serveur.
     */
    private final CommCoreClient comm;

    /**
     * Constructeur de l'implémentation.
     * 
     * @param comm Le client de communication à utiliser (ne doit pas être null)
     * @throws NullPointerException si comm est null
     */
    public IhmKanbanCallsCommImp(CommCoreClient comm) {
        this.comm = Objects.requireNonNull(comm);
    }

    /**
     * Ferme la visualisation d'un kanban et notifie le serveur.
     * 
     * Cette méthode envoie un message {@link CloseKanban} au serveur pour
     * indiquer que l'utilisateur ne visualise plus ce kanban. Les erreurs
     * réseau sont loggées mais n'interrompent pas l'exécution.
     * 
     * @param lightKanban Le kanban à fermer (ne doit pas être null)
     * @param lightUser L'utilisateur qui ferme le kanban (ne doit pas être null)
     */
    @Override
    public void closingKanban(LightKanban lightKanban, LightUser lightUser) {
        if (lightKanban == null || lightUser == null) {
            LOGGER.warning("Paramètres invalides pour closingKanban");
            return;
        }

        LOGGER.log(Level.INFO, "Fermeture de la visualisation Kanban {0} pour l'utilisateur {1}", 
                   new Object[]{lightKanban.getId(), lightUser.getUsername()});

        try {
            CloseKanban msg = new CloseKanban(lightKanban, lightUser);
            
            if (comm.getMsgSender() != null) {
                comm.sendMessage(msg);
                LOGGER.fine("Demande de fermeture de Kanban envoyée avec succès au serveur");
            } else {
                LOGGER.warning("Message sender non initialisé");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur réseau lors de la fermeture du Kanban", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur inattendue lors de la fermeture du Kanban", e);
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
     * @param modification La modification à appliquer (ne doit pas être null)
     */
    @Override
    public void sendRequestModification(LightUser user, Modification modification) {
        if (user == null || modification == null) {
            LOGGER.warning("Paramètres invalides pour sendRequestModification");
            return;
        }

        LOGGER.log(Level.INFO, "Envoi demande de modification carte {0} vers {1} par {2}", new Object[]{modification.getId(), modification.getTargetKanban().getTitle(), user.getUsername()});

        try {
            RequestModification msg = new RequestModification(user, modification);
            
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
        }
    }
}