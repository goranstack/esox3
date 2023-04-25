package nu.esox.gui.bind;

import nu.esox.obs.*;


public class EsoxAspectPredicate extends EsoxPredicate
{
    private final EsoxModelSocketIF m_modelSocket;
    private final EsoxProjector.ExtractAspectValue m_get;
    private final String m_aspectName;
    private final boolean m_noModelProjection;
    
    private boolean m_isProjectionDirty = false;
    
    private final EsoxObservableModelIF.Dispatcher m_modelObserver =
        new EsoxObservableModelIF.Dispatcher()
    	{
    		@Override
    		public void begin()
    		{
    			m_isProjectionDirty = false;
    		}
	         
    		@Override
    		public void valueChanged( EsoxObservableIF source, String propertyName, Object newValue, Object oldValue )
    		{
    			if
    				( ( m_aspectName == null ) || ( m_aspectName.equals( propertyName ) ) )
    			{
    				m_isProjectionDirty = true;
    			}     	
    		}
	           
    		@Override
    		public void end()
    		{
    			if ( m_isProjectionDirty ) set( getProjectedValue() );
    		}
    	};
   
    	
    	
    public EsoxAspectPredicate( EsoxModelSocketIF modelSocket, EsoxProjector.ExtractAspectValue get, String aspectName )
    {
        m_modelSocket = modelSocket;
        m_get = get;
        m_aspectName = aspectName;
        m_noModelProjection = false;

        if ( m_modelSocket.getModel() != null ) m_modelSocket.getModel().addObserver( m_modelObserver );
        m_modelSocket.addObserver( new EsoxObservableModelIF.Dispatcher()
							 	   {
							            @Override
							            public void valueChanged( EsoxObservableIF source, String propertyName, Object newValue, Object oldValue )
							            {
							            	if ( oldValue != null ) ( (EsoxObservableModelIF) oldValue ).removeObserver( m_modelObserver );
							            	if ( newValue != null ) ( (EsoxObservableModelIF) newValue ).addObserver( m_modelObserver );    
							            }
							              
							            @Override
							            public void end()
							            {
							                set( getProjectedValue() );
							            }
							   	   } );
    }

    
    private boolean getProjectedValue()
    {
        return ( m_modelSocket.getModel() == null ) ? m_noModelProjection : (boolean) m_get.extract( m_modelSocket.getModel() );          
    }
}
