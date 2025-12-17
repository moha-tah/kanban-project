package client.comm.messages;

import java.io.Serializable;
import java.util.Optional;

import client.ClientContext;

/**
 * Classe de base pour tous les messages échangés entre le client et le serveur.
 * 
 * Chaque message concret doit redéfinir la méthode {@link #handle} pour traiter
 * le message et éventuellement produire un message de réponse. Retourner un
 * Optional vide signifie qu'aucune réponse n'est envoyée.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 */
public abstract class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Contexte client transitoire qui ne sera PAS sérialisé avec le message.
     * 
     * La couche de communication doit définir ce contexte côté récepteur
     * avant d'appeler {@link #handle()}.
     */
    private transient ClientContext clientContext;
    
    /**
     * Traite ce message et retourne éventuellement un message de réponse à envoyer.
     * 
     * Cette méthode doit être implémentée par chaque sous-classe pour définir
     * le comportement spécifique du message.
     * 
     * @return Optional contenant un message de réponse à envoyer, ou vide si aucune réponse
     * @throws Exception si le traitement échoue
     */
    public abstract Optional<Message> handle() throws Exception;

    /**
     * Définit le contexte client pour ce message.
     * 
     * Ce champ est transitoire et ne sera pas sérialisé lorsque le message
     * est envoyé sur le réseau. Il est utilisé uniquement à l'exécution.
     * 
     * @param context Le contexte client à définir (peut être null)
     */
    public void setClientContext(ClientContext context) {
        this.clientContext = context;
    }

    /**
     * Récupère le contexte client de ce message.
     * 
     * @return Le contexte client, ou null s'il n'a pas été défini
     */
    public ClientContext getClientContext() {
        return this.clientContext;
    }   

}
