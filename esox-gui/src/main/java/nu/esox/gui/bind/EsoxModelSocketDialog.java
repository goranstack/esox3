package nu.esox.gui.bind;

import java.awt.*;
import javax.swing.*;


@SuppressWarnings("serial")
public class EsoxModelSocketDialog extends JDialog
{
    private final EsoxModelSocketIF m_modelSocket = new EsoxModelSocket();
 
    public EsoxModelSocketDialog() { super(); }
    public EsoxModelSocketDialog( Dialog owner, boolean modal ) { super(owner, modal); }
    public EsoxModelSocketDialog( Dialog owner, String title, boolean modal ) { super(owner, title, modal); }
    public EsoxModelSocketDialog( Dialog owner, String title ) { super(owner, title); }
    public EsoxModelSocketDialog( Dialog owner ) { super(owner); }
    public EsoxModelSocketDialog( Frame owner, boolean modal ) {  super(owner, modal);  }
    public EsoxModelSocketDialog( Frame arg0, String arg1, boolean arg2 ) { super(arg0, arg1, arg2); }
    public EsoxModelSocketDialog( Frame owner, String title ) { super(owner, title); }
    public EsoxModelSocketDialog( Frame owner ) { super(owner); }

    public EsoxModelSocketIF getModelSocket() { return m_modelSocket; }
}
