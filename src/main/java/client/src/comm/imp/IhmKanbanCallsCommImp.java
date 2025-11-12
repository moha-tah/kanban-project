package client.src.comm.imp;

import client.src.interfaces.IhmKanbanCallsComm;
import client.src.comm.CommCoreClient;
import java.util.Objects;
import java.util.UUID;

public class IhmKanbanCallsCommImp implements IhmKanbanCallsComm {
    private final CommCoreClient comm;

    public IhmKanbanCallsCommImp(CommCoreClient comm) {
        this.comm = Objects.requireNonNull(comm);
    }

    @Override
    public void closingKanban(UUID LightKanbanId, UUID LightUserId) {
    }
}