package nu.esox.gui.bind;

import javax.swing.JLabel;

@SuppressWarnings("serial")
public class EsoxLabel extends JLabel
{
	public EsoxLabel( EsoxModelSocketIF modelSocket, EsoxProjector.ExtractAspectValue get, String aspectName )
	{
        prepare( this, modelSocket, get, aspectName );
	}
	
	public static JLabel prepare( JLabel l, EsoxModelSocketIF modelSocket, EsoxProjector.ExtractAspectValue get, String aspectName )
	{
        EsoxProjector.createLabelTextProjector( modelSocket, get, aspectName, l );
        return l;
	}
}