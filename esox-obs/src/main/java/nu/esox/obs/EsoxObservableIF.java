package nu.esox.obs;

public interface EsoxObservableIF
{
    interface ObserverIF
    {
        void handle( ObservationIF ... observations );
    }
    
    interface ObservationIF
    {
        EsoxObservableIF getSource();
    }
    
    void addObserver( ObserverIF o );
    void removeObserver( ObserverIF o );
    
    void beginTransaction();
    void endTransaction();
}
