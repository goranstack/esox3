package nu.esox.obs;

import java.lang.reflect.*;


public class EsoxPropertyPredicate extends EsoxPredicate implements EsoxPredicateIF, EsoxObservableIF.ObserverIF
{
    private final EsoxObservableModelIF m_model;
    private final Method m_getPropertyValueMethod;
    private final Condition m_condition;
	
	
    public EsoxPropertyPredicate( EsoxObservableModelIF model, String getPropertyValueMethodName, Condition condition )
    {
        super( false );

        m_model = model;
        
        try
        {
            m_getPropertyValueMethod = m_model.getClass().getMethod( getPropertyValueMethodName );
        }
        catch ( NoSuchMethodException ex )
        {
            throw new Error( "No such method: " + m_model.getClass() + "." + getPropertyValueMethodName + "()" );
        }

        m_condition = condition;
        m_model.addObserver( this );
        handle();
    }
	
    public void handle( EsoxObservableIF.ObservationIF ... observations )
    {
        set( invokeCondition() );
    }

    private boolean invokeCondition()
    {
        boolean tmp = m_getPropertyValueMethod.canAccess( m_model );
        try
        {
            m_getPropertyValueMethod.setAccessible( true );
            return m_condition.evaluate( m_getPropertyValueMethod.invoke( m_model ) );
        }
        catch ( IllegalAccessException ex )
        {
            throw new Error( "Method not accessible: " + m_getPropertyValueMethod );
        }
        catch ( InvocationTargetException ex )
        {
            throw new Error( "Invocation failure: " + m_getPropertyValueMethod, ex );
        }
        catch ( Throwable ex )
        {
            throw new Error( "Failure: " + m_getPropertyValueMethod, ex );
        }
        finally
        {
            m_getPropertyValueMethod.setAccessible( tmp );
        }
    }


    public interface Condition
    {
        boolean evaluate( Object propertyValue );
    }


    public static class Same implements Condition
    {
        private final Object m_value;

        public Same( Object value )
        {
            m_value = value;
        }

        public boolean evaluate( Object propertyValue )
        {
            return propertyValue == m_value;
        }
    }

    public static class Equals implements Condition
    {
        private final Object m_value;

        public Equals( Object value )
        {
            m_value = value;
        }

        public boolean evaluate( Object propertyValue )
        {
            if ( propertyValue == m_value ) return true;
            if ( propertyValue == null ) return m_value.equals( propertyValue );
            return propertyValue.equals( m_value );
        }   
    }
}
