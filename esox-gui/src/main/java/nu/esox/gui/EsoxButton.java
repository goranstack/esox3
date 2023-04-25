package nu.esox.gui;

import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class EsoxButton extends JButton
{
    public EsoxButton( String text, Icon icon, ActionListener actionListener )
    {
        super( text, icon );
        addActionListener( actionListener );
    }
}
