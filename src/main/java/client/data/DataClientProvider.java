package client.data;
import client.interfaces.DataCallsComm;
import client.interfaces.DataClientCallsKanban;
import client.interfaces.DataClientCallsMain;
import client.interfaces.MainCallsDataClient;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataClientProvider implements MainCallsDataClient {

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

    private LightUser currentUser;
    private final List<LightKanban> myKanbans = new ArrayList<>();

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

    @Override
    public void saveUser() {
        if (currentUser != null) {
            myModel.saveUser(currentUser);
        }
    }

    @Override
    public boolean authentify(String username, String password) {
        return password != null && password.length() >= 6;
    }

    @Override
    public LightUser getMyLightUser() {
        return currentUser;
    }

    @Override
    public List<LightKanban> getMyListLightKanbans() {
        return new ArrayList<>(myKanbans);
        //todo
    }

    @Override
    public void exportProfile(UUID lightUserId, String path) {
        // todo
    }

    @Override
    public void importMyProfile(String path) {

    }

    @Override
    public void sendCreateProfile(List<?> profileDetails) {

    }

    //methodes


}
 