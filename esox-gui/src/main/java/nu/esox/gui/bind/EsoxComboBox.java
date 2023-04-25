package nu.esox.gui.bind;

import javax.swing.JComboBox;

@SuppressWarnings("serial")
public class EsoxComboBox<T> extends JComboBox<T>
{
	public EsoxComboBox( EsoxModelSocketIF modelSocket, EsoxProjector.ExtractAspectValue get, String aspectName, EsoxInjector.InjectAspectValue set )
	{
        prepare( this, modelSocket, get, aspectName, set );        		
	}

	
	public static JComboBox<?> prepare( JComboBox<?> cb, EsoxModelSocketIF modelSocket, EsoxProjector.ExtractAspectValue get, String aspectName, EsoxInjector.InjectAspectValue set )
	{
        EsoxProjector.createComboBoxValueProjector( modelSocket, get, aspectName, cb );
        EsoxInjector.createComboBoxInjector( modelSocket, set, cb );        		
        return cb;
	}
}