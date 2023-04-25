package nu.esox.obs;

import java.util.*;


public class EsoxObservableMap<K,V> extends EsoxCascadingObservable implements EsoxObservableMapIF<K,V>
{
    private final Map<K,V> m_delegate = new HashMap<K,V>();

    public Set<Map.Entry<K,V>>	entrySet() { return m_delegate.entrySet(); }
    public Set<K> keySet() { return m_delegate.keySet(); }
    public Collection<V> values() { return m_delegate.values(); }
    public V get( Object key ) { return m_delegate.get( key ); }
    public boolean containsKey( Object o ) { return m_delegate.containsKey( o ); } 
    public boolean containsValue( Object o ) { return m_delegate.containsValue( o ); } 
    public boolean equals( Object o ) { return m_delegate.equals( o ); } 
    public boolean isEmpty() { return m_delegate.isEmpty(); } 
    public int hashCode() { return m_delegate.hashCode(); } 
    public int size() { return m_delegate.size(); } 

    public String toString() { return m_delegate.toString(); }

    
    public V	put( K key, V value )
    {
        if ( m_delegate.get( key ) == value ) return null;
        V old = m_delegate.put( key, value );
        added( value );
        fire( new PutObservation( key, old, value ) );
        return old;
    }
    
    @SuppressWarnings( "unchecked" )
    public V	remove( Object key )
    {
        if ( ! m_delegate.containsKey( key ) ) return null;
        V v = m_delegate.remove( key );
        removed( v );
        fire( new RemovedObservation( (K) key, v ) );
        return v;
    }
    
    public void putAll( Map<? extends K,? extends V> m )
    {
        throw new UnsupportedOperationException();
    }
    

    public void clear()
    {
        boolean b = isEmpty();
        for ( V e : m_delegate.values() ) removed( e );
        m_delegate.clear();
        if ( ! b ) fire( new ClearedObservation() );
    }





    private void added( Object e )
    {
        if ( e instanceof EsoxObservableIF ) observe( (EsoxObservableIF) e );
    }

    private void removed( Object e )
    {
        if ( e instanceof EsoxObservableIF ) unobserve( (EsoxObservableIF) e );
    }


    @SuppressWarnings( "unchecked" )
    protected EsoxObservableIF.ObservationIF cascade( EsoxObservableIF.ObservationIF observation )
    {
        return new ChangedObservation( (V) observation.getSource(), observation );
    }


    
    protected abstract class Observation extends EsoxObservable.Observation implements EsoxObservableMapIF.ObservationIF<K,V>
    {
        private Observation() {}
        public EsoxObservableMap<K,V> getMap() { return EsoxObservableMap.this; }
    }



    protected class PutObservation extends Observation implements EsoxObservableMapIF.PutObservationIF<K,V>
    {
        private final K m_key;
        private final V m_value;
        private final V m_previousValue;
     
        private PutObservation( K key, V previousValue, V value )
        {
            m_key = key;
            m_value = value;
            m_previousValue = previousValue;
        }
     
        public K getKey() { return m_key; }
        public V getValue() { return m_value; }
        public V getPreviousValue() { return m_previousValue; }
        public void dispatch( EsoxObservableMapIF.DispatcherIF<K,V> dispatcher ) { dispatcher.put( getMap(), getKey(), getValue(), getPreviousValue() ); }
        public String toString() { return getSource() + "value-put.(" + getKey() + ":" + getPreviousValue() + "->" + getValue() + ")"; }
    }


    protected class RemovedObservation extends Observation implements EsoxObservableMapIF.RemovedObservationIF<K,V>
    {
        private final K m_key;
        private final V m_value;
     
        private RemovedObservation( K key, V value )
        {
            m_key = key;
            m_value = value;
        }
     
        public K getKey() { return m_key; }
        public V getValue() { return m_value; }
        public void dispatch( EsoxObservableMapIF.DispatcherIF<K,V> dispatcher ) { dispatcher.removed( getMap(), getKey(), getValue() ); }
        public String toString() { return getSource() + "value-removed.(" + getKey() + ":" + getValue() + ")"; }
    }


    protected class ChangedObservation extends Observation implements EsoxObservableMapIF.ChangedObservationIF<K,V>
    {
        private final V m_value;
        private final EsoxObservableIF.ObservationIF m_observation;
     
        private ChangedObservation( V value, EsoxObservableIF.ObservationIF observation )
        {
            m_value = value;
            m_observation = observation;
        }
        public V getValue() { return m_value; }
        public EsoxObservableIF.ObservationIF getObservation() { return m_observation; }
        public void dispatch( EsoxObservableMapIF.DispatcherIF<K,V> dispatcher ) { dispatcher.changed( getMap(), getValue(), getObservation() ); }
        public String toString() { return getSource() + ".value-changed(" + getObservation() + ")"; }
    }




    protected class ClearedObservation extends Observation implements EsoxObservableMapIF.ClearedObservationIF<K,V>
    {
        private ClearedObservation() {}
        public void dispatch( EsoxObservableMapIF.DispatcherIF<K,V> dispatcher ) { dispatcher.cleared( getMap() ); }
        public String toString() { return getSource() + ".values-cleared()"; }
    }
}

