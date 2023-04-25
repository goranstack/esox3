package nu.esox.gui.bind;

import javax.swing.JTextField;

@SuppressWarnings("serial")
public class EsoxTextField extends JTextField
{
	public EsoxTextField( int columns, EsoxModelSocketIF modelSocket, EsoxProjector.ExtractAspectValue get, String aspectName, EsoxInjector.InjectAspectValue set, boolean addFocusBehavior )
	{
		super( columns );
		prepare( this, modelSocket, get, aspectName, set, addFocusBehavior );
	}
	
	public EsoxTextField( String s, EsoxModelSocketIF modelSocket, EsoxProjector.ExtractAspectValue get, String aspectName, EsoxInjector.InjectAspectValue set, boolean addFocusBehavior )
	{
		super( s );
		prepare( this, modelSocket, get, aspectName, set, addFocusBehavior );
	}
	
	
	public static JTextField prepare( JTextField tf, EsoxModelSocketIF modelSocket, EsoxProjector.ExtractAspectValue get, String aspectName, EsoxInjector.InjectAspectValue set, boolean addFocusBehavior )
	{
        EsoxProjector.createTextFieldTextProjector( modelSocket, get, aspectName, tf );
        EsoxInjector.createTextFieldInjector( modelSocket, set, tf, addFocusBehavior ); 
        return tf;
	}
}