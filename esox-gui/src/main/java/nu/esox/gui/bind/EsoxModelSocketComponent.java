package nu.esox.gui.bind;

import javax.swing.*;


@SuppressWarnings("serial")
public class EsoxModelSocketComponent extends JComponent
{
    private final EsoxModelSocketIF m_modelSocket = new EsoxModelSocket();
    
    
    public EsoxModelSocketComponent() {}

    public EsoxModelSocketIF getModelSocket() { return m_modelSocket; }
}
