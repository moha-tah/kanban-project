package client.data;

import java.util.List;

import client.interfaces.MainCallsDataClient;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import java.util.List;
import java.util.UUID;

public class MainCallsDataImplementation implements MainCallsDataClient {
    private DataClientProvider provider;

    //Constructeur
    public MainCallsDataImplementation(DataClientProvider provider) {
        
    }

    @Override
    public List<LightKanban> getMyListLightKanbans(){
        return provider.getMyModel().getMyLightKanbans();
    }

    @Override
    public LightUser getMyLightUser(){
        return provider.getMyModel().getLocalUser();
    }

    @Override
    public void saveUser(){}

    @Override
    public boolean authentify(String username, String password){
        return new Boolean("TRUE");  //TODO 
    }

    @Override
    public void exportProfile(UUID lightUserId, String path){}

    @Override
    public void importMyProfile(String path){}

    @Override
    public void sendCreateProfile(List<?> profileDetails){}



    //getters
    public DataClientProvider getProvider() {
        return this.provider;
    }
    //setters
    public void setProvider(DataClientProvider provider) {
        this.provider = provider;
    }



     
}
