package nu.esox.obs;



public abstract class EsoxObservableProperty extends EsoxObservableModel
{
    private final String m_name;


    public EsoxObservableProperty( String name )
    {
        m_name = name;
    }


    public final String getName() { return m_name; }

    public final void touch( Object newValue, Object oldValue )
    {
        touch( m_name, newValue, oldValue );
    }



    



    
    
    public static class Boolean extends EsoxObservableProperty
    {
        private boolean m_value;

        public Boolean( String name, boolean value )
        {
            super( name );
            m_value = value;
        }

        public final boolean get() { return m_value; }

        public final void set( boolean value )
        {
            if
                ( m_value != value )
            {
                m_value = value;
                touch( m_value, ! m_value );
            }
        }

        public String toString() { return getName() + "=" + get(); }
    }



    
    public static class Integer extends EsoxObservableProperty
    {
        private int m_value;

        public Integer( String name, int value )
        {
            super( name );
            m_value = value;
        }

        public final int get() { return m_value; }

        public final void set( int value )
        {
            if
                ( m_value != value )
            {
                int old = m_value;
                m_value = value;
                touch( m_value, old );
            }
        }

        public String toString() { return getName() + "=" + get(); }
    }



    
    public static class Double extends EsoxObservableProperty
    {
        private double m_value;

        public Double( String name, double value )
        {
            super( name );
            m_value = value;
        }

        public final double get() { return m_value; }

        public final void set( double value )
        {
            if
                ( m_value != value )
            {
                double old = m_value;
                m_value = value;
                touch( m_value, old );
            }
        }

        public String toString() { return getName() + "=" + get(); }
    }



    
    public static class Reference<T> extends EsoxObservableProperty
    {
        private T m_value;

        public Reference( String name, T value )
        {
            super( name );
            m_value = value;
        }

        public final T get() { return m_value; }

        public final void set( T value )
        {
            if
                ( m_value != value )
            {
                T old = m_value;
                m_value = value;
                touch( m_value, old );
            }
        }

        public String toString() { return getName() + "=" + get(); }
    }
    
}
