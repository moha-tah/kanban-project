package client.ihmMain;

import client.data.DataCreateProfile;
import common.dataClasses.LightUser;
import common.dataClasses.User;

public class IhmMainCreateProfile {

    private final DataCreateProfile data;

    public IhmMainCreateProfile(DataCreateProfile data) {
        this.data = data;
    }

    // handler button "Créer"
    public LightUser createProfile(User params) {
        System.out.println("[IHM-Main] → Data : sendCreateProfile(params)");
        if (params == null) {
            throw new IllegalArgumentException("Params cannot be null");
        }

        if (params.getUsername() == null || params.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }

        return data.sendCreateProfile(params);
    }
}
