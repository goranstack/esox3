package nu.esox.obs;

import java.util.*;


public class EsoxObservableList<T> extends EsoxCascadingObservable implements EsoxObservableListIF<T>
{
    private final List<T> m_delegate = new ArrayList<T>();

    

	@SuppressWarnings("hiding") public <T> T[] toArray( T[] a ) { return m_delegate.toArray( a ); }  
    public T get( int i ) { return m_delegate.get( i ); }
    public List<T> subList( int from, int to ) { return m_delegate.subList( from, to ); } 
    public Object[] toArray() { return m_delegate.toArray(); } 
    public boolean contains( Object o ) { return m_delegate.contains( o ); } 
    public boolean containsAll( Collection<?> c ) { return m_delegate.containsAll( c ); } 
    public boolean equals( Object o ) { return m_delegate.equals( o ); } 
    public boolean isEmpty() { return m_delegate.isEmpty(); } 
    public int hashCode() { return m_delegate.hashCode(); } 
    public int indexOf( Object o ) { return m_delegate.indexOf( o ); } 
    public int lastIndexOf( Object o ) { return m_delegate.lastIndexOf( o ); } 
    public int size() { return m_delegate.size(); } 

    public String toString() { return m_delegate.toString(); }

    
    public boolean add( T e )
    {
        boolean b = m_delegate.add( e );
        added( e );
        fire( new AddedObservation( e, size() - 1 ) );
        return b;
    }
    
    public void add( int i, T e )
    {
        m_delegate.add( i, e );
        added( e );
        fire( new AddedObservation( e, i ) );
    }

    public boolean addAll( Collection<? extends T> c )
    {
        int i = size();
        boolean b = m_delegate.addAll( c );
        for
            ( T e : c )
        {
            added( e );
            fire( new AddedObservation( e, i++ ) );
        }
        return b;
    }
    
    public boolean addAll( int i, Collection<? extends T> c )
    {
        boolean b = m_delegate.addAll( i, c );
        for
            ( T e : c )
        {
            added( e );
            fire( new AddedObservation( e, i++ ) );
        }
        return b;
    }


    
    public T remove( int i )
    {
        T e = m_delegate.remove( i );
        removed( e );
        fire( new RemovedObservation( e, i ) );
        return e;
    }

    @SuppressWarnings( "unchecked" )
        public boolean remove( Object e )
    {
        int i = indexOf( e );
        boolean b = m_delegate.remove( e );
        if
            ( b )
        {
            removed( e );
            fire( new RemovedObservation( (T) e, i ) );
        }
        return b;
    } 

    public boolean removeAll( Collection<?> c )
    {
        beginTransaction();
        for
            ( int i = 0; i < size(); i++ )
        {
            T e = get( i );
            removed( e );
            if ( c.contains( e ) ) fire( new RemovedObservation( e, i ) );
        }
        
        boolean b = m_delegate.removeAll( c );
        endTransaction();
        return b;
    }
    
    public boolean retainAll( Collection<?> c )
    {
        beginTransaction();
        for
            ( int i = 0; i < size(); i++ )
        {
            T e = get( i );
            removed( e );
            if ( ! c.contains( e ) ) fire( new RemovedObservation( e, i ) );
        }
        
        boolean b = m_delegate.retainAll( c );
        endTransaction();
        return b;
    } 

    public void clear()
    {
        boolean b = isEmpty();
        for ( T e : this ) removed( e );
        m_delegate.clear();
        if ( ! b ) fire( new ClearedObservation() );
    }

    public T set( int i, T e )
    {
        T p = m_delegate.set( i, e );
        fire( new SetObservation( p, e, i ) );
        return p;
    }
    
    public Iterator<T> iterator()
    {
        return new MyIterator();
    }
    
    public ListIterator<T> listIterator()
    {
        return new MyListIterator();
    }
    
    public ListIterator<T> listIterator( int i )
    {
        return new MyListIterator( i );
    }



    private class MyIterator implements Iterator<T>
    {
        private final Iterator<T> m_delegate;
        private int m_i = 0;

        MyIterator()
        {
            m_delegate = EsoxObservableList.this.m_delegate.iterator();
        }

        public boolean hasNext() { return m_delegate.hasNext(); }
        
        public T next()
        {
            T e = m_delegate.next();
            m_i++;
            return e;
        }
        
        public void remove()
        {
            T e = get( m_i );
            m_delegate.remove();
            removed( e );
            fire( new RemovedObservation( e, m_i ) );
        }
    }


    private class MyListIterator implements ListIterator<T>
    {
        private final ListIterator<T> m_delegate;
        private int m_i = 0;

        MyListIterator()
        {
            m_delegate = EsoxObservableList.this.m_delegate.listIterator();
        }

        MyListIterator( int i )
        {
            this();
            m_i = i;
        }

        public boolean hasNext() { return m_delegate.hasNext(); }
        public boolean hasPrevious() { return m_delegate.hasPrevious(); }
        
        public int nextIndex() { return m_delegate.nextIndex(); }
        public int previousIndex() { return m_delegate.previousIndex(); }
        
        public T next()
        {
            T e = m_delegate.next();
            m_i++;
            return e;
        }
        
        public T previous()
        {
            T e = m_delegate.previous();
            m_i--;
            return e;
        }
         
        public void remove()
        {
            T e = get( m_i );
            m_delegate.remove();
            removed( e );
            fire( new RemovedObservation( e, m_i ) );
        }


        public void add( T e )
        {
            m_delegate.add( e );
            m_i++;
            added( e );
            fire( new AddedObservation( e, m_i - 1 ) );
        }
        
        public void set( T e )
        {
            T p = get( m_i );
            m_delegate.set( e );
            fire( new SetObservation( p, e, m_i ) );
        }
    }





    protected void added( Object e )
    {
        if ( e instanceof EsoxObservableIF ) observe( (EsoxObservableIF) e );
    }

    protected void removed( Object e )
    {
        if ( e instanceof EsoxObservableIF ) unobserve( (EsoxObservableIF) e );
    }


    @SuppressWarnings( "unchecked" )
    protected EsoxObservableIF.ObservationIF cascade( EsoxObservableIF.ObservationIF observation )
    {
        return new ChangedObservation( (T) observation.getSource(), observation );
    }


    
    protected abstract class Observation extends EsoxObservable.Observation implements EsoxObservableListIF.ObservationIF<T>
    {
        private Observation() {}
        public EsoxObservableList<T> getList() { return EsoxObservableList.this; }
    }


    protected abstract class ElementObservation extends Observation
    {
        private final T m_element;
        private final int m_index;
        
        
        private ElementObservation( T element, int index )
        {
            m_element = element;
            m_index = index;
        }
        
        public T getElement() { return m_element; }
        public int getIndex() { return m_index; }
    }


    protected class SetObservation extends ElementObservation implements EsoxObservableListIF.SetObservationIF<T>
    {
        private final T m_previousElement;
        
        private SetObservation( T previousElement, T element, int index )
        {
            super( element, index );
            m_previousElement = previousElement;
        }
        
        public T getPreviousElement() { return m_previousElement; }
        public void dispatch( EsoxObservableListIF.DispatcherIF<T> dispatcher ) { dispatcher.set( getList(), getIndex(), getElement(), getPreviousElement() ); }
        public String toString() { return getSource() + "element-set.(" + getIndex() + ":" + getPreviousElement() + "->" + getElement() + ")"; }
    }


    protected class ChangedObservation extends Observation implements EsoxObservableListIF.ChangedObservationIF<T>
    {
        private final T m_element;
        private final EsoxObservableIF.ObservationIF m_observation;
        
        private ChangedObservation( T element, EsoxObservableIF.ObservationIF observation )
        {
            m_element = element;
            m_observation = observation;
        }
        public T getElement() { return m_element; }
        public EsoxObservableIF.ObservationIF getObservation() { return m_observation; }
        public void dispatch( EsoxObservableListIF.DispatcherIF<T> dispatcher ) { dispatcher.changed( getList(), getElement(), getObservation() ); }
        public String toString() { return getSource() + ".element-changed(" + getObservation() + ")"; }
    }


    protected class AddedObservation extends ElementObservation implements EsoxObservableListIF.AddedObservationIF<T>
    {
        private AddedObservation( T element, int index ) { super( element, index ); }
        public void dispatch( EsoxObservableListIF.DispatcherIF<T> dispatcher ) { dispatcher.added( getList(), getIndex(), getElement() ); }
        public String toString() { return getSource() + ".element-added(" + getIndex() + ":" + getElement() + ")"; }
    }

    protected class RemovedObservation extends ElementObservation implements EsoxObservableListIF.RemovedObservationIF<T>
    {
        private RemovedObservation( T element, int index ) { super( element, index ); }
        public void dispatch( EsoxObservableListIF.DispatcherIF<T> dispatcher ) { dispatcher.removed( getList(), getIndex(), getElement() ); }
        public String toString() { return getSource() + ".element-removed(" + getIndex() + ":" + getElement() + ")"; }
    }


    protected class ClearedObservation extends Observation implements EsoxObservableListIF.ClearedObservationIF<T>
    {
        private ClearedObservation() {}
        public void dispatch( EsoxObservableListIF.DispatcherIF<T> dispatcher ) { dispatcher.cleared( getList() ); }
        public String toString() { return getSource() + ".elements-cleared()"; }
    }
}

