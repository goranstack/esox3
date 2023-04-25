package nu.esox.gui.bind;

import javax.swing.JSlider;


@SuppressWarnings("serial")
public class EsoxSlider extends JSlider
{
	public EsoxSlider( EsoxModelSocketIF modelSocket, EsoxProjector.ExtractAspectValue get, String aspectName, EsoxInjector.InjectAspectValue set )
	{
        prepare( this, modelSocket, get, aspectName, set );        		
	}

	
	public static JSlider prepare( JSlider s, EsoxModelSocketIF modelSocket, EsoxProjector.ExtractAspectValue get, String aspectName, EsoxInjector.InjectAspectValue set )
	{
        EsoxProjector.createSliderValueProjector( modelSocket, get, aspectName, s );
        EsoxInjector.createSliderInjector( modelSocket, set, s );        		
        return s;
	}
}