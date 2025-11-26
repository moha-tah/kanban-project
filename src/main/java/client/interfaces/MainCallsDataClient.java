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

    void exportProfile(LightUser lightUserId, String path);

    void importMyProfile(String path);

    LightUser sendCreateProfile(String login, String password, String name, String surname,
                                        int age, String avatar, String role, String permissions,
                                        String contacts, String kanbanList, String status);
}
