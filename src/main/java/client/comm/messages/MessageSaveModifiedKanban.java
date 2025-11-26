package client.comm.messages;

import common.dataClasses.LightKanban;
import common.dataClasses.Modification;
import client.comm.messages.Message;

import java.util.Optional;

public class MessageSaveModifiedKanban extends Message {
    private static final long serialVersionUID = 1L;

    private final LightKanban kanban;
    private final Modification modification;

    public MessageSaveModifiedKanban(LightKanban kanban, Modification modification) {
        this.kanban = kanban;
        this.modification = modification;
    }

    @Override
    @Override
public Optional<Message> handle() {
    try {
        // Dynamically load ServerContext because client does not depend on server package
        Class<?> contextClass = Class.forName("server.ServerContext");

        // Retrieve the server-side data interface
        java.lang.reflect.Method getDataMethod = contextClass.getMethod("getData");
        Object dataServerObj = getDataMethod.invoke(null);

        if (dataServerObj != null) {

            // Cast to shared server interface
            server.interfaces.CommCallsDataServer dataServer =
                    (server.interfaces.CommCallsDataServer) dataServerObj;

            System.out.println(
                "SERVER: Applying modification to Kanban: "
                + kanban.getTitle() + " (" + kanban.getId() + ")"
            );

            // Apply modification on the server
            dataServer.saveModifiedKanban(kanban, modification);

            // No response needed in V3
        }
    } 
    catch (ClassNotFoundException e) {
        // NORMAL: When running on client side, server classes do not exist
    }
    catch (Throwable t) {
        java.util.logging.Logger.getLogger(MessageSaveModifiedKanban.class.getName())
                .log(java.util.logging.Level.SEVERE,
                     "Error while processing MessageSaveModifiedKanban", t);
    }

    return Optional.empty();
}

    public Optional<Message> handle() {
        try {
        
            Class<?> contextClass = Class.forName("server.ServerContext");
            
            // Retrieve the server-side data interface provider
            java.lang.reflect.Method getDataMethod = contextClass.getMethod("getData");
            Object dataServerObj = getDataMethod.invoke(null);

            if (dataServerObj != null) {
            
                server.interfaces.CommCallsDataServer dataServer =
                        (server.interfaces.CommCallsDataServer) dataServerObj;

                System.out.println(
                    "SERVER: Received Kanban modification for: " 
                    + kanban.getTitle() 
                    + " (" + kanban.getId() + ")"
                );

                dataServer.saveModifiedKanban(kanban, modification);

                
                // Future versions may push updates to other connected clients.
            }
        } 
        catch (ClassNotFoundException e) {
        
        }
        catch (Throwable t) {
            java.util.logging.Logger.getLogger(MessageSaveModifiedKanban.class.getName())
                .log(java.util.logging.Level.SEVERE,
                     "Error while processing MessageSaveModifiedKanban", t);
        }

        // No response required for this message in V3
        return Optional.empty();
    }

    public LightKanban getKanban() {
        return kanban;
    }

    public Modification getModification() {
        return modification;
    }
}

