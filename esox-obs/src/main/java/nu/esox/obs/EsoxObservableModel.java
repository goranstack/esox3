package nu.esox.obs;



public class EsoxObservableModel extends EsoxCascadingObservable implements EsoxObservableModelIF
{
    public final void touch( String event )
    {
        fire( new EventObservation( event ) );
    }

    public final void touch( String propertyName, Object newValue, Object oldValue )
    {
        fire( new ValueChangedObservation( propertyName, newValue, oldValue ) );
    }




    
    
    protected abstract class Observation extends EsoxObservable.Observation implements EsoxObservableModelIF.ObservationIF
    {
    }

    protected class EventObservation extends Observation implements EsoxObservableModelIF.EventObservationIF
    {
        private final String m_event;

        EventObservation( String event )
        {
            m_event = event;
        }

        public final String getEvent() { return m_event; }

        public void dispatch( EsoxObservableModelIF.DispatcherIF dispatcher ) { dispatcher.occured( getSource(), getEvent() ); }

        public String toString() { return getSource() + ".occured(" + getEvent() + ")"; }
    }
    
    protected class ValueChangedObservation extends Observation implements EsoxObservableModelIF.ValueChangedObservationIF
    {
        private final String m_propertyName;
        private final Object m_newValue;
        private final Object m_oldValue;
        
        
        ValueChangedObservation( String propertyName, Object newValue, Object oldValue )
        {
            m_propertyName = propertyName;
            m_newValue = newValue;
            m_oldValue = oldValue;
        }
        
        public String getPropertyName() { return m_propertyName; }
        public Object getNewValue() { return m_newValue; }
        public Object getOldValue() { return m_oldValue; }

        public void dispatch( EsoxObservableModelIF.DispatcherIF dispatcher ) { dispatcher.valueChanged( getSource(), getPropertyName(), getNewValue(), getOldValue() ); }

        public String toString() { return getSource() + ".value-changed(" + getPropertyName() + ":" + getOldValue() + "->" + getNewValue() + ")"; }
    }
}
