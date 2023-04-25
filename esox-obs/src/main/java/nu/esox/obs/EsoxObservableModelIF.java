package nu.esox.obs;

public interface EsoxObservableModelIF extends EsoxObservableIF
{   
    interface ObservationIF extends EsoxObservableIF.ObservationIF
    {
        void dispatch( DispatcherIF dispatcher );
    }
    
    interface EventObservationIF extends ObservationIF
    {
        String getEvent();
    }

    interface ValueChangedObservationIF extends ObservationIF
    {
        String getPropertyName();
        Object getNewValue();
        Object getOldValue();
     }

    interface DispatcherIF
    {
        void occured( EsoxObservableIF source, String event );
        void valueChanged( EsoxObservableIF source, String propertyName, Object newValue, Object oldValue );
    }


    class Dispatcher implements DispatcherIF, ObserverIF
    {
        public void begin() {}
        public void occured( EsoxObservableIF source, String event ) {}
        public void valueChanged( EsoxObservableIF source, String propertyName, Object newValue, Object oldValue ) {}
        public void end() {}

        public void handle( EsoxObservableIF.ObservationIF ... observations )
        {
            begin();
            for ( EsoxObservableIF.ObservationIF ev : observations ) ( (EsoxObservableModelIF.ObservationIF) ev ).dispatch( this );
            end();
        }
    }

    
    void touch( String event );
    void touch( String valueName, Object newValue, Object oldValue );
}
