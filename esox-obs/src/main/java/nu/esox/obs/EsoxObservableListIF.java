package nu.esox.obs;

import java.util.*;


public interface EsoxObservableListIF<T> extends EsoxObservableIF, List<T>
{
    interface ObservationIF<T> extends EsoxObservableIF.ObservationIF
    {
        EsoxObservableListIF<T> getList();
        void dispatch( DispatcherIF<T> dispatcher );
    }
    
    interface ChangedObservationIF<T> extends ObservationIF<T>
    {
        T getElement();
        EsoxObservableIF.ObservationIF getObservation();
    }
    
    interface AddedObservationIF<T> extends ObservationIF<T>
    {
        int getIndex();
        T getElement();
    }
    
    interface RemovedObservationIF<T> extends ObservationIF<T>
    {
        int getIndex();
        T getElement();
    }
    
    interface ClearedObservationIF<T> extends ObservationIF<T>
    {
    }
     
    interface SetObservationIF<T> extends ObservationIF<T>
    {
        int getIndex();
        T getElement();
        T getPreviousElement();
    }
   

    interface DispatcherIF<T>
    {
        void changed( EsoxObservableList<T> list, T element, EsoxObservableIF.ObservationIF observation );
        void set( EsoxObservableList<T> list, int index, T element, T previousElement );
        void added( EsoxObservableList<T> list, int index, T element );
        void removed( EsoxObservableList<T> list, int index, T element );
        void cleared( EsoxObservableList<T> list );
    }

    class Dispatcher<T> implements DispatcherIF<T>, EsoxObservableIF.ObserverIF
    {
        public void begin() {}
        public void changed( EsoxObservableList<T> list, T element, EsoxObservableIF.ObservationIF observation ) {}
        public void set( EsoxObservableList<T> list, int index, T element, T previousElement ) {}
        public void added( EsoxObservableList<T> list, int index, T element ) {}
        public void removed( EsoxObservableList<T> list, int index, T element ) {}
        public void cleared( EsoxObservableList<T> list ) {}
        public void end() {}

        @SuppressWarnings( "unchecked" )
        public void handle( EsoxObservableIF.ObservationIF ... observations )
        {
            begin();
            for ( EsoxObservableIF.ObservationIF ev : observations ) ( (ObservationIF<T>) ev ).dispatch( this );
            end();
        }
    }
}
