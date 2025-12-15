package client.interfaces;

import java.time.LocalDate;
import java.util.List;

import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.User;


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

    void addAuthorizedUserToKanban(LightUser user, LightKanban kanban);

    User getLocalUser ();

    Void modifyLocalUser (String newFirstName, String newLastName, LocalDate newBirthDate, String newAvatar, String newUsername);
    void deleteLocalProfile();
}
