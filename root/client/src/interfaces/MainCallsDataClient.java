import java.util.List;
import java.util.UUID;

public interface MainCallsDataClient {

    void saveUser();

    AuthResult authentify(String username, String password);

    LightUser getLightUser();

    List<LightKanban> getLightKanbans();

    void exportProfile(UUID lightUserId, String path);

    void importMyProfile(String path);

    void sendCreateProfile(List<?> profileDetails);
}
