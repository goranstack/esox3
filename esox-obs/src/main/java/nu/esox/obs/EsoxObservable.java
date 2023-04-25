package nu.esox.obs;

import java.util.*;

public abstract class EsoxObservable implements EsoxObservableIF
{
    private final LinkedList<ObserverIF> m_observers = new LinkedList<ObserverIF>();
    private final LinkedList<Operation> m_operations = new LinkedList<Operation>();
    private final LinkedList<EsoxObservableIF.ObservationIF> m_observations = new LinkedList<EsoxObservableIF.ObservationIF>();

    private int m_transactionDepth = 0;
    
    public final void addObserver( ObserverIF o )
    {
        m_operations.add( new Add( o ) );
    }

    public final void removeObserver( ObserverIF o )
    {
        m_operations.add( new Remove( o ) );
    }

    public final void beginTransaction()
    {
        m_transactionDepth++;
    }

    public final void endTransaction()
    {
        assert m_transactionDepth > 0;
        
        m_transactionDepth--;
        
        if
            ( m_transactionDepth == 0 )
        {
            if
                ( m_observations.size() == 1 )
            {
                notify( m_observations.get( 0 ) );
            } else if
                  ( ! m_observations.isEmpty() )
            {
                EsoxObservableIF.ObservationIF [] tmp = new EsoxObservableIF.ObservationIF [ m_observations.size() ];
                for ( int i = 0; i < tmp.length; i++ ) tmp[ i ] = m_observations.get( i );
                notify( tmp ); // fixit: avoid this copy???
            }
            m_observations.clear();
        }
    }
    


    
    protected final void fire( EsoxObservableIF.ObservationIF observation )
    {
        if
            ( m_transactionDepth > 0 )
        {
            m_observations.add( observation );
        } else {
            notify( observation );
        }
    }



    private void notify( EsoxObservableIF.ObservationIF ... observations )
    {
        LinkedList<ObserverIF> observers = collectObservers();
        for ( ObserverIF o : observers ) o.handle( observations );
    }

    

    private LinkedList<ObserverIF> collectObservers()
    {
        for ( Operation op : m_operations ) op.apply();
        m_operations.clear();
        return m_observers;
    }




    


    private abstract class Operation
    {
        abstract void apply();
    }

    private class Add extends Operation
    {
        private final ObserverIF m_observer;

        Add( ObserverIF observer )
        {
            m_observer = observer;
        }
        
        void apply()
        {
            if ( ! m_observers.contains( m_observer ) ) m_observers.add( m_observer );
        }
    }

    private class Remove extends Operation
    {
        private final ObserverIF m_observer;

        Remove( ObserverIF observer )
        {
            m_observer = observer;
        }
        
        void apply()
        {
            m_observers.remove( m_observer );
        }
    }





    protected class Observation implements EsoxObservableIF.ObservationIF
    {
        private EsoxObservableIF m_source = EsoxObservable.this;

        protected Observation() {}

        Observation setSource( EsoxObservableIF source ) { m_source = source; return this; }

        public final EsoxObservableIF getSource() { return m_source; }
    }





    
/*    
    public static void main( String [] args ) throws Throwable
    {
        class MyModel extends EsoxObservableModel
        {
            private final EsoxObservableProperty.Reference<String> m_name = new EsoxObservableProperty.Reference<String>( "name", "" );
            private final EsoxObservableProperty.Integer m_count = new EsoxObservableProperty.Integer( "count", 0 );

            {
                observe( m_name );
                observe( m_count );
            }
            
            public String getName() { return m_name.get(); }
            public int getCount() { return m_count.get(); }

            public void setName( String name ) { m_name.set( name ); }
            public void setCount( int count ) { m_count.set( count ); }

            public String toString() { return "{MyModel:" + m_name + "," + m_count+ "}"; }
        }
        
        EsoxObservableListIF<MyModel> l = new EsoxObservableList<MyModel>();
        EsoxObservableMapIF<String,MyModel> a = new EsoxObservableMap<String,MyModel>();
        MyModel m = new MyModel();



            
          //   m.addObserver( o1 );
          // l.addObserver( o2 );

        l.addObserver(
                      new EsoxObservableListIF.Dispatcher<MyModel>()
                      {
                          public void begin() { System.err.println( "L{" ); }
                          public void end() { System.err.println( "}" ); }
                          public void changed( EsoxObservableList<MyModel> list, MyModel element, EsoxObservableIF.ObservationIF observation ) { System.err.println( "CHANGED " + element + " " + observation ); }
                          public void set( EsoxObservableList<MyModel> list, int index, MyModel element, MyModel previousElement ) { System.err.println( "SET " + element + " (" + index + ")" ); }
                          public void added( EsoxObservableList<MyModel> list, int index, MyModel element ) { System.err.println( "ADDED " + element + " (" + index + ")" ); }
                          public void removed( EsoxObservableList<MyModel> list, int index, MyModel element ) { System.err.println( "REMOVED " + element + " (" + index + ")" ); }
                          public void cleared( EsoxObservableList<MyModel> list ) { System.err.println( "CLEARED " ); }
                     } );
         a.addObserver(
                       new EsoxObservableMapIF.Dispatcher<String,MyModel>()
                      {
                          public void begin() { System.err.println( "A{" ); }
                          public void end() { System.err.println( "}" ); }
                          public void changed( EsoxObservableMap<String,MyModel> map, MyModel value, EsoxObservableIF.ObservationIF observation ) { System.err.println( "CHANGED " + value + " " + observation ); }
                          public void put( EsoxObservableMap<String,MyModel> map, String key, MyModel value, MyModel previousValue ) { System.err.println( "PUT " + key + "=" + value ); }
                          public void removed( EsoxObservableMap<String,MyModel> map, String key, MyModel value ) { System.err.println( "REMOVED " + value + " (" + key + ")" ); }
                          public void cleared( EsoxObservableMap<String,MyModel> map ) { System.err.println( "CLEARED " ); }
                      } );
         m.addObserver(
                      new EsoxObservableModelIF.Dispatcher()
                      {
                          public void begin() { System.err.println( "M{" ); }
                          public void end() { System.err.println( "}" ); }
                          public void occured( String event ) { System.err.println( "OCCURED " + event ); }
                          public void valueChanged( String propertyName, Object newValue, Object oldValue ) { System.err.println( "CHANGED " + propertyName + " from " + oldValue + " to " + newValue ); }
                     } );


        m.setName( "Bengt" );
        m.setCount( 42 );

        m.beginTransaction();
        m.setName( "Bengt" );
        m.setCount( 42 );
        m.endTransaction();

        m.beginTransaction();
        m.setName( "Arne" );
        m.setCount( 17 );
        m.endTransaction();

        l.add( m );
        a.put( "qwert", m );
        
        m.beginTransaction();
        m.setName( "Bengt" );
        m.setCount( 42 );
        m.setCount( 47 );
        m.endTransaction();

        m.touch( "hepp" );

        l.clear();
        a.clear();
    }
    */
}
