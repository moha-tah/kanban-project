package src.main.java.client.src.data;

public class KanbanCallsDataImplementation {
    private DataClientProvider provider;
    
    //Constructeur
    public KanbanCallsDataImplementation(DataClientProvider provider) {
        
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
