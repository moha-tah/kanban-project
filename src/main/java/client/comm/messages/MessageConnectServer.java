package client.comm.messages; // <-- adapte ce package à ton projet

import java.util.List;
import java.util.Optional;

import client.MainApp;
import client.ihmMain.MainCore;
import client.interfaces.CommClientCallsMain;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import javafx.application.Platform;

/**
 * Message envoyé par le serveur au client pour confirmer la connexion
 * et lui renvoyer son profil + la liste de ses Kanbans.
 *
 */
public class MessageConnectServer extends Message {

    private final LightUser user;
    private final List<LightKanban> kanbans;

    public MessageConnectServer(LightUser user, List<LightKanban> kanbans) {
        this.user = user;
        this.kanbans = kanbans;
    }

    public LightUser getUser() {
        return user;
    }

    public List<LightKanban> getKanbans() {
        return kanbans;
    }

    @Override
    public Optional<Message> handle() {
        MainCore core = MainApp.getCore();
        if (core == null) {
            System.err.println("[MessageConnectServer] MainCore is null");
            return Optional.empty();
        }

        core.setMe(user);
        if (kanbans != null) {
            core.addKanbans(kanbans);
        }

        CommClientCallsMain callbacks = core.getCOMMService();
        if (callbacks != null) {
            callbacks.connectionRequest(user, kanbans);
        }

        Platform.runLater(core::showHomeView);

        return Optional.empty();
    }
}