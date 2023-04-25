package nu.esox.gui.bind;

import javax.swing.JCheckBox;

@SuppressWarnings("serial")
public class EsoxCheckBox extends JCheckBox
{
	public EsoxCheckBox( String label, EsoxModelSocketIF modelSocket, EsoxProjector.ExtractAspectValue get, String aspectName, EsoxInjector.InjectAspectValue set )
	{
		super( label );
        prepare( this, modelSocket, get, aspectName, set );        		
	}

	
	public static JCheckBox prepare( JCheckBox cb, EsoxModelSocketIF modelSocket, EsoxProjector.ExtractAspectValue get, String aspectName, EsoxInjector.InjectAspectValue set )
	{
        EsoxProjector.createButtonSelectedProjector( modelSocket, get, aspectName, cb );
        EsoxInjector.createToggleButtonInjector( modelSocket, set, cb );        		
        return cb;
	}
}