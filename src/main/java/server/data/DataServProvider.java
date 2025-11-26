package server.data;

public class DataServProvider {

    private ComCallsDataServImplementation dataCallsComServ;
    private ServerModel myModel;
    public DataServProvider() {
        this.myModel = new ServerModel();
    }

    public static DataServProvider newDataServProvider() {
        return new DataServProvider();
    }
   
    public void setDataCallsComServ(ComCallsDataServImplementation dccs) {
        this.dataCallsComServ = dccs;
    }

    public ComCallsDataServImplementation getDataCallsComServ() {
        return dataCallsComServ;
    }

    public void setModel(ServerModel model) {
        this.myModel = model;
    }

    public ServerModel getModel() {
        return myModel;
    }
   
}


