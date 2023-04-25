package nu.esox.obs;

public class EsoxPredicate extends EsoxObservableModel implements EsoxPredicateIF
{
    private boolean m_isTrue;
	
	
    public EsoxPredicate()
    {
        this( false );
    }
	
    public EsoxPredicate( boolean isTrue )
    {
        m_isTrue = isTrue;
    }
	
    public final boolean isTrue() { return m_isTrue; }
	
    public final void set( boolean isTrue )
    {
        if ( m_isTrue == isTrue ) return;
        m_isTrue = isTrue;
        touch( PROPERTY_IS_TRUE, m_isTrue, ! m_isTrue );
    }




    public static class Not extends EsoxPredicate implements EsoxObservableIF.ObserverIF
    {
        private final EsoxPredicateIF m_operand;

        public Not( EsoxPredicateIF operand )
        {
            m_operand = operand;
            m_operand.addObserver( this );
            set( ! m_operand.isTrue() );
        }
    
        public void handle( EsoxObservableIF.ObservationIF ... observations )
        {
            set( ! m_operand.isTrue() );
        }
    }



    public static class And extends EsoxPredicate implements EsoxObservableIF.ObserverIF
    {
        private final EsoxPredicateIF [] m_operands;

        public And( EsoxPredicateIF ... operands )
        {
            m_operands = operands;
            for ( EsoxPredicateIF o : m_operands ) o.addObserver( this );
            refresh();
        }
    
        public void handle( EsoxObservableIF.ObservationIF ... observations )
        {
            refresh();
        }

        private void refresh()
        {
            for
                ( EsoxPredicateIF o : m_operands )
            {
                if
                    ( ! o.isTrue() )
                {
                    set( false );
                    return;
                }
            }
            
            set( true );
        }
    }



    public static class Or extends EsoxPredicate implements EsoxObservableIF.ObserverIF
    {
        private final EsoxPredicateIF [] m_operands;

        public Or( EsoxPredicateIF ... operands )
        {
            m_operands = operands;
            for ( EsoxPredicateIF o : m_operands ) o.addObserver( this );
            refresh();
        }
    
        public void handle( EsoxObservableIF.ObservationIF ... observations )
        {
            refresh();
        }

        private void refresh()
        {
            for
                ( EsoxPredicateIF o : m_operands )
            {
                if
                    ( o.isTrue() )
                {
                    set( true );
                    return;
                }
            }
            
            set( false );
        }
    }
}
