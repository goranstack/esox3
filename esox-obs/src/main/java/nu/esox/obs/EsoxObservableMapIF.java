package nu.esox.obs;

import java.util.*;


public interface EsoxObservableMapIF<K,V> extends EsoxObservableIF, Map<K,V>
{
    interface ObservationIF<K,V> extends EsoxObservableIF.ObservationIF
    {
        EsoxObservableMapIF<K,V> getMap();
        void dispatch( DispatcherIF<K,V> dispatcher );
    }
    
    interface ChangedObservationIF<K,V> extends ObservationIF<K,V>
    {
        V getValue();
        EsoxObservableIF.ObservationIF getObservation();
    }
  
    interface RemovedObservationIF<K,V> extends ObservationIF<K,V>
    {
        K getKey();
        V getValue();
    }
 
    interface ClearedObservationIF<K,V> extends ObservationIF<K,V>
    {
    }
     
    interface PutObservationIF<K,V> extends ObservationIF<K,V>
    {
        K getKey();
        V getValue();
        V getPreviousValue();
    }
   

    interface DispatcherIF<K,V>
    {
        void changed( EsoxObservableMap<K,V> map, V value, EsoxObservableIF.ObservationIF observation );
        void put( EsoxObservableMap<K,V> map, K key, V value, V previousValue );
        void removed( EsoxObservableMap<K,V> map, K key, V value );
        void cleared( EsoxObservableMap<K,V> map );
    }

    class Dispatcher<K,V> implements DispatcherIF<K,V>, EsoxObservableIF.ObserverIF
    {
        public void begin() {}
        public void changed( EsoxObservableMap<K,V> map, V value, EsoxObservableIF.ObservationIF observation ) {}
        public void put( EsoxObservableMap<K,V> map, K key, V value, V previousValue ) {}
        public void removed( EsoxObservableMap<K,V> map, K key, V value ) {}
        public void cleared( EsoxObservableMap<K,V> map ) {}
        public void end() {}

        @SuppressWarnings( "unchecked" )
        public void handle( EsoxObservableIF.ObservationIF ... observations )
        {
            begin();
            for ( EsoxObservableIF.ObservationIF ev : observations ) ( (ObservationIF<K,V>) ev ).dispatch( this );
            end();
        }
    }
}
