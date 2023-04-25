package nu.esox.gui.bind;

import nu.esox.obs.*;


public class EsoxModelSocket extends EsoxObservableModel implements EsoxModelSocketIF
{
    private EsoxPredicate m_hasModel = new EsoxPredicate( false );
    private EsoxObservableModelIF m_model;

    public EsoxModelSocket()
    {
        this( null );
    }

    public EsoxModelSocket( EsoxObservableModelIF model )
    {
        attach( model );
    }
    
    public final EsoxObservableModelIF getModel() { return m_model; }
    public final boolean isOccupied() { return m_model != null; }
    
    public EsoxPredicateIF getIsOccupied()
    {
        if ( m_hasModel == null ) m_hasModel = new EsoxPredicate( isOccupied() );
        return m_hasModel;
    }
    
    public final void attach( EsoxObservableModelIF model )
    {
        if
            ( m_model != model )
        {
            EsoxObservableModelIF old = m_model;
            m_model = model;
             m_hasModel.set( m_model != null );
            touch( PROPERTY_MODEL, m_model, old );
        }
    }
}
