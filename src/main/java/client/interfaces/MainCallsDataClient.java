package client.interfaces;

import java.util.List;
import java.util.UUID;

import common.dataClasses.LightUser;
import common.dataClasses.LightKanban;

public interface MainCallsDataClient {

    void saveUser();

    boolean authentify(String username, String password);

    LightUser getMyLightUser();

    List<LightKanban> getMyListLightKanbans();

    void exportProfile(UUID lightUserId, String path);

    void importMyProfile(String path);

    void sendCreateProfile(List<?> profileDetails);
}
