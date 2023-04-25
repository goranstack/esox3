package nu.esox.gui.bind;

import javax.swing.JSpinner;

@SuppressWarnings("serial")
public class EsoxSpinner extends JSpinner
{
	public EsoxSpinner( EsoxModelSocketIF modelSocket, EsoxProjector.ExtractAspectValue get, String aspectName, EsoxInjector.InjectAspectValue set )
	{
        prepare( this, modelSocket, get, aspectName, set );        		
	}

	
	public static JSpinner prepare( JSpinner s, EsoxModelSocketIF modelSocket, EsoxProjector.ExtractAspectValue get, String aspectName, EsoxInjector.InjectAspectValue set )
	{
        EsoxProjector.createSpinnerValueProjector( modelSocket, get, aspectName, s );
        EsoxInjector.createSpinnerInjector( modelSocket, set, s );        		
        return s;
	}
}