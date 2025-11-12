package src.main.java.client.src.data;

public class CommCallsDataClientImplementation {
    private DataClientProvider provider;

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

    //autres methodes
    List<LightUsers> getUsersList() {
        DataClientProvider prov = this.getProvider();
        ClientModel model = prov.getMyModel();
        return model.getConnectedUsers();
    }
}
