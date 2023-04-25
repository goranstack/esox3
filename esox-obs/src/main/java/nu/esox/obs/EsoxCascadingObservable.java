package nu.esox.obs;


public class EsoxCascadingObservable extends EsoxObservable
{
    private ObserverIF m_observer = null;

    protected final void observe( EsoxObservableIF e )
    {
        if
            ( m_observer == null )
        {
            m_observer = new ObserverIF() { public void handle( EsoxObservableIF.ObservationIF ... observations ) { EsoxCascadingObservable.this.handle( observations ); } };
        }

        e.addObserver( m_observer );
    }

    protected final void unobserve( EsoxObservableIF e )
    {
        e.removeObserver( m_observer );
    }



    private void handle( EsoxObservableIF.ObservationIF ... observations )
    {
        beginTransaction();
        for ( EsoxObservableIF.ObservationIF observation : observations ) fire( cascade( observation ) );
        endTransaction();
    }

    protected EsoxObservableIF.ObservationIF cascade( EsoxObservableIF.ObservationIF observation )
    {
        return ( (EsoxObservable.Observation) observation ).setSource( getEffectiveSource() );
    }
    
    protected EsoxObservableIF getEffectiveSource() { return this; }
}
