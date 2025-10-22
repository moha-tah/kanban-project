package root.client.src.interfaces;

import java.util.List;
import java.util.UUID;

import root.common.src.dataClasses.LightUser;
import root.common.src.dataClasses.LightKanban;

public interface MainCallsDataClient {

    void saveUser();

    boolean authentify(String username, String password);

    LightUser getLightUser();

    List<LightKanban> getLightKanbans();

    void exportProfile(UUID lightUserId, String path);

    void importMyProfile(String path);

    void sendCreateProfile(List<?> profileDetails);
}
