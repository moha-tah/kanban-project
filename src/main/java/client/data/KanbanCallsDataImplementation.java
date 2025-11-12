package src.main.java.client.src.data;
import src.main.java.client.src.interfaces.DataCallsComm;
import src.main.java.client.src.interfaces.DataClientCallsKanban;
import src.main.java.client.src.interfaces.DataClientCallsMain;

public class DataClientProvider {

    private ClientModel myModel;
    private KanbanCallsDataImplementation toKabanImpl;
    private MainCallsDataImplementation toMainImpl;
    private CommCallsDataClientImplementation toCommImpl;
    private DataCallsComm commInterface;
    private DataClientCallsKanban kanbanInterface;
    private DataClientCallsMain mainInterface;

    // Constructeur
    public DataClientProvider() 
            {
        this.myModel = new ClientModel();
        this.toKabanImpl = new KanbanCallsDataImplementation(this);
        this.toMainImpl = new MainCallsDataImplementation(this);
        this.toCommImpl = new CommCallsDataClientImplementation(this);
    }

    // Getters
    public ClientModel getMyModel() {
        return this.myModel;
    }
    public KanbanCallsDataImplementation getToKabanImpl() {
        return this.toKabanImpl;
    }
    public MainCallsDataImplementation getToMainImpl() {
        return this.toMainImpl;
    }
    public CommCallsDataClientImplementation getToCommImpl() {
        return this.toCommImpl;
    }
    public DataCallsComm getCommInterface() {
        return this.commInterface;
    }
    public DataClientCallsKanban getKanbanInterface() {
        return this.kanbanInterface;
    }
    public DataClientCallsMain getMainInterface() {
        return this.mainInterface;
    }

    //setters
    public void setMyModel(ClientModel model) {
        this.myModel = model;
    }
    public void setToKabanImpl(KanbanCallsDataImplementation impl) {
        this.toKabanImpl = impl;
    }
    public void setToMainImpl(MainCallsDataImplementation impl) {
        this.toMainImpl = impl;
    }
    public void setToCommImpl(CommCallsDataClientImplementation impl) {
        this.toCommImpl = impl;
    }
    public void setCommInterface(DataCallsComm commInterface) {
        this.commInterface = commInterface;
    }
    public void setKanbanInterface(DataClientCallsKanban kanbanInterface) {
        this.kanbanInterface = kanbanInterface;
    }
    public void setMainInterface(DataClientCallsMain mainInterface) {
        this.mainInterface = mainInterface;
    }

    //methodes


}
 