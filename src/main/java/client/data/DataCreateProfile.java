package client.data;

import common.dataClasses.LightUser;
import common.dataClasses.User;
import client.comm.CommCreateProfile;

public class DataCreateProfile {

    private final CommCreateProfile comm;

    public DataCreateProfile(CommCreateProfile comm) {
        this.comm = comm;
    }

    public LightUser sendCreateProfile(User params) {
        System.out.println("[Data] → Comm : createProfile(params)");
        // no storage
        return comm.createProfile(params);
    }
}
