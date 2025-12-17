package client.data;
import client.interfaces.DataCallsComm;
import client.interfaces.DataClientCallsKanban;
import client.interfaces.DataClientCallsMain;

/**
 * Fournisseur de services de la couche données côté client.
 * 
 * Cette classe centralise l'accès au modèle de données et aux différentes
 * implémentations des interfaces de communication entre les couches.
 * Elle initialise et coordonne tous les composants de la couche données.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see ClientModel
 * @see KanbanCallsDataImplementation
 * @see MainCallsDataImplementation
 * @see CommCallsDataClientImplementation
 */
public class DataClientProvider {

    /**
     * Modèle de données client contenant toutes les données de l'application.
     */
    private ClientModel myModel;
    
    /**
     * Implémentation des appels depuis la couche Kanban vers la couche données.
     */
    private KanbanCallsDataImplementation toKabanImpl;
    
    /**
     * Implémentation des appels depuis la couche Main vers la couche données.
     */
    private MainCallsDataImplementation toMainImpl;
    
    /**
     * Implémentation des appels depuis la couche Communication vers la couche données.
     */
    private CommCallsDataClientImplementation toCommImpl;
    
    /**
     * Interface de communication vers la couche Communication.
     */
    private DataCallsComm commInterface;
    
    /**
     * Interface de communication vers la couche Kanban.
     */
    private DataClientCallsKanban kanbanInterface;
    
    /**
     * Interface de communication vers la couche Main.
     */
    private DataClientCallsMain mainInterface;

    /**
     * Constructeur du fournisseur de services données.
     * 
     * Initialise le modèle de données et toutes les implémentations
     * des interfaces de communication entre les couches.
     */
    public DataClientProvider()
    {
        this.myModel = new ClientModel();
        this.toKabanImpl = new KanbanCallsDataImplementation(this);
        this.toMainImpl = new MainCallsDataImplementation(this);
        this.toCommImpl = new CommCallsDataClientImplementation(this);
    }

    /**
     * Récupère le modèle de données client.
     * 
     * @return Le modèle de données client
     */
    public ClientModel getMyModel() {
        return this.myModel;
    }
    
    /**
     * Récupère l'implémentation des appels depuis la couche Kanban.
     * 
     * @return L'implémentation des appels Kanban
     */
    public KanbanCallsDataImplementation getToKabanImpl() {
        return this.toKabanImpl;
    }
    
    /**
     * Récupère l'implémentation des appels depuis la couche Main.
     * 
     * @return L'implémentation des appels Main
     */
    public MainCallsDataImplementation getToMainImpl() {
        return this.toMainImpl;
    }
    
    /**
     * Récupère l'implémentation des appels depuis la couche Communication.
     * 
     * @return L'implémentation des appels Communication
     */
    public CommCallsDataClientImplementation getToCommImpl() {
        return this.toCommImpl;
    }
    
    /**
     * Récupère l'interface de communication vers la couche Communication.
     * 
     * @return L'interface de communication, ou null si non définie
     */
    public DataCallsComm getCommInterface() {
        return this.commInterface;
    }
    
    /**
     * Récupère l'interface de communication vers la couche Kanban.
     * 
     * @return L'interface de communication Kanban, ou null si non définie
     */
    public DataClientCallsKanban getKanbanInterface() {
        return this.kanbanInterface;
    }
    
    /**
     * Récupère l'interface de communication vers la couche Main.
     * 
     * @return L'interface de communication Main, ou null si non définie
     */
    public DataClientCallsMain getMainInterface() {
        return this.mainInterface;
    }

    /**
     * Définit le modèle de données client.
     * 
     * @param model Le modèle de données à définir (ne doit pas être null)
     */
    public void setMyModel(ClientModel model) {
        this.myModel = model;
    }
    
    /**
     * Définit l'implémentation des appels depuis la couche Kanban.
     * 
     * @param impl L'implémentation à définir (ne doit pas être null)
     */
    public void setToKabanImpl(KanbanCallsDataImplementation impl) {
        this.toKabanImpl = impl;
    }
    
    /**
     * Définit l'implémentation des appels depuis la couche Main.
     * 
     * @param impl L'implémentation à définir (ne doit pas être null)
     */
    public void setToMainImpl(MainCallsDataImplementation impl) {
        this.toMainImpl = impl;
    }
    
    /**
     * Définit l'implémentation des appels depuis la couche Communication.
     * 
     * @param impl L'implémentation à définir (ne doit pas être null)
     */
    public void setToCommImpl(CommCallsDataClientImplementation impl) {
        this.toCommImpl = impl;
    }
    
    /**
     * Définit l'interface de communication vers la couche Communication.
     * 
     * @param commInterface L'interface de communication à définir (peut être null)
     */
    public void setCommInterface(DataCallsComm commInterface) {
        this.commInterface = commInterface;
    }
    
    /**
     * Définit l'interface de communication vers la couche Kanban.
     * 
     * @param kanbanInterface L'interface de communication Kanban à définir (peut être null)
     */
    public void setKanbanInterface(DataClientCallsKanban kanbanInterface) {
        this.kanbanInterface = kanbanInterface;
    }
    
    /**
     * Définit l'interface de communication vers la couche Main.
     * 
     * @param mainInterface L'interface de communication Main à définir (peut être null)
     */
    public void setMainInterface(DataClientCallsMain mainInterface) {
        this.mainInterface = mainInterface;
    }


}
 