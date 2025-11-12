package client.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import client.interfaces.KanbanCallsDataClient;
import common.dataClasses.Kanban;
import common.dataClasses.Snapshot;

public class KanbanCallsDataImplementation implements  KanbanCallsDataClient {
    private DataClientProvider provider;

    //Constructeur
    public KanbanCallsDataImplementation(DataClientProvider provider) {

    }

    public void saveSnapshot(Kanban kanban){
        //TODO
    }

    public List<Snapshot> getListSnapshot(){
        //TODO
        return new List<Snapshot>() {

            @Override
            public boolean add(Snapshot arg0) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'add'");
            }

            @Override
            public void add(int arg0, Snapshot arg1) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'add'");
            }

            @Override
            public boolean addAll(Collection<? extends Snapshot> c) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'addAll'");
            }

            @Override
            public boolean addAll(int index, Collection<? extends Snapshot> c) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'addAll'");
            }

            @Override
            public void clear() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'clear'");
            }

            @Override
            public boolean contains(Object o) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'contains'");
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'containsAll'");
            }

            @Override
            public Snapshot get(int index) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'get'");
            }

            @Override
            public int indexOf(Object o) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'indexOf'");
            }

            @Override
            public boolean isEmpty() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'isEmpty'");
            }

            @Override
            public Iterator<Snapshot> iterator() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'iterator'");
            }

            @Override
            public int lastIndexOf(Object o) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'lastIndexOf'");
            }

            @Override
            public ListIterator<Snapshot> listIterator() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'listIterator'");
            }

            @Override
            public ListIterator<Snapshot> listIterator(int index) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'listIterator'");
            }

            @Override
            public boolean remove(Object o) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'remove'");
            }

            @Override
            public Snapshot remove(int index) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'remove'");
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'removeAll'");
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'retainAll'");
            }

            @Override
            public Snapshot set(int arg0, Snapshot arg1) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'set'");
            }

            @Override
            public int size() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'size'");
            }

            @Override
            public List<Snapshot> subList(int fromIndex, int toIndex) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'subList'");
            }

            @Override
            public Object[] toArray() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'toArray'");
            }

            @Override
            public <T> T[] toArray(T[] arg0) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'toArray'");
            }
            
        };
    }

    public Snapshot getSnapshot(Snapshot snap){
        //TODO
        return new Snapshot();
    }

    public void deleteSnapshot(Snapshot snap){
        //TODO
    }

    public void getModified(UUID modificationID, UUID kanbanID){
        //TODO
    }

    //getters
    public DataClientProvider getProvider() {
        return this.provider;
    }
    //setters
    public void setProvider(DataClientProvider provider) {
        this.provider = provider;
    }
}
 