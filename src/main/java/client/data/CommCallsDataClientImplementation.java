package client.data;

import src.main.java.client.src.interfaces.ComCallsDataClient;
import src.main.java.common.src.dataClasses.LightKanban;
import src.main.java.common.src.dataClasses.LightUser;

import java.util.List;

public class CommCallsDataClientImplementation implements ComCallsDataClient{
    private DataClientProvider provider;

    public void updateUserList(List<LightUser> users, List<LightKanban> kanbans){
        provider.getMyModel().setMyLightKanbans(kanbans);
        provider.getMyModel().setConnectedUsers(users);
    }

    //Constructeur
    public CommCallsDataClientImplementation(DataClientProvider provider) {
        
    }
    //getters
    public DataClientProvider getProvider() {
        return this.provider;
    }
    //setters
    public void setProvider(DataClientProvider provider) {
        this.provider = provider;
    }
}
