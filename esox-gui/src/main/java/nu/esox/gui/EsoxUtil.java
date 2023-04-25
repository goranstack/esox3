package nu.esox.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

public class EsoxUtil
{
    public static Component findByName( Component root, String name )
    {
        //if ( root.getName() != null ) System.err.println( root.getName() + " ? " + name );
        if ( name.equals(  root.getName() ) ) return root;
        
        if
            ( root instanceof Container )
        {
            for
                ( Component c : ( (Container) root ).getComponents() )
            {
                Component tmp = findByName( c, name );
                if ( tmp != null ) return tmp;
            }
        }
        
        return null;
    }
    
    public static final FocusListener TEXTFIELD_FOCUS =
    		new FocusListener()
		    {
				public void focusLost( FocusEvent ev ) { ( (JTextField) ev.getComponent() ).postActionEvent(); }    
				public void focusGained( FocusEvent ev ) { ( (JTextField) ev.getComponent() ).selectAll(); } 		
		    };
}
