package nu.esox.gui.bind;

import javax.swing.*;


@SuppressWarnings("serial")
public class EsoxModelSocketFrame extends JFrame
{
    private final EsoxModelSocketIF m_modelSocket = new EsoxModelSocket();
    
    
    public EsoxModelSocketFrame( String title ) { super( title ); }

    public EsoxModelSocketIF getModelSocket() { return m_modelSocket; }
}
