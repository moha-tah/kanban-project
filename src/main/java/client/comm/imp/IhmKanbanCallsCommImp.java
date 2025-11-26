package client.comm.imp;

import client.comm.CommCoreClient;
import client.interfaces.IhmKanbanCallsComm;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

import java.util.Objects;
import java.util.UUID;

public class IhmKanbanCallsCommImp implements IhmKanbanCallsComm {
    private final CommCoreClient comm;

    public IhmKanbanCallsCommImp(CommCoreClient comm) {
        this.comm = Objects.requireNonNull(comm);
    }

    @Override
    public void closingKanban(LightKanban LightKanbanId, LightUser LightUserId) {
    }
}