package client.comm;

import java.util.UUID;
import common.dataClasses.User;
import common.dataClasses.LightUser;

public class CommCreateProfile {
    // Simulated Server: create user
    public LightUser createProfile(User params) {
        System.out.println("[Comm] → Server(stub) : createProfile(params)");
        return new LightUser(UUID.randomUUID(), params.getUsername());
    }
}
