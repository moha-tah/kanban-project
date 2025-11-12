package client.src.comm.imp;

import java.util.UUID;
import java.util.Arrays;
import java.util.Objects;
import java.io.IOException;

import client.src.interfaces.DataCallsComm;
import client.src.comm.CommCoreClient;
import common.src.dataClasses.Kanban;

public class DataCallsCommImp implements DataCallsComm {
    private final CommCoreClient comm;

    public DataCallsCommImp(CommCoreClient comm) {
        this.comm = Objects.requireNonNull(comm);
    }

    @Override
    public void askDeleteKanban(UUID LightKanbanId, UUID LightUserId) {
        try {
            comm.sendMessage(Arrays.asList("askDeleteKanban", LightKanbanId, LightUserId));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendKanban(Kanban Kanban) {
        try {
            comm.sendMessage(Arrays.asList("sendKanban", Kanban));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}