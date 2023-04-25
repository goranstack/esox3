package nu.esox.gui.bind;

import java.awt.*;
import javax.swing.*;


@SuppressWarnings("serial")
public class EsoxModelSocketPanel extends JPanel
{
    private final EsoxModelSocketIF m_modelSocket = new EsoxModelSocket();
    
    
    public EsoxModelSocketPanel() {}
    public EsoxModelSocketPanel( LayoutManager l ) { super( l ); }

    public EsoxModelSocketIF getModelSocket() { return m_modelSocket; }
}
