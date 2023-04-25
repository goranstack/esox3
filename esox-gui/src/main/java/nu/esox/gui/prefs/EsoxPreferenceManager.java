package nu.esox.gui.prefs;

import java.awt.Component;
import java.util.prefs.Preferences;

import javax.swing.JComponent;

public class EsoxPreferenceManager
{
    public final static String DECORATION = "DECORATION";
    
    public static void save( Component root, String name )
    {
        Preferences p = Preferences.userRoot().node( name );
        save( root, p );
    }
    
    public static void load( Component root, String name )
    {
        Preferences p = Preferences.userRoot().node( name );
        load( root, p );
    }
    
    private static void save( Component C, Preferences p )
    {
        
        if ( C instanceof EsoxPreferenceOwnerIF ) ( (EsoxPreferenceOwnerIF) C ).save( p );
        if 
            ( C instanceof JComponent ) 
        {
//            EsoxPreferenceDecorationIF po = (EsoxPreferenceDecorationIF) ( (JComponent) C ).getClientProperty( DECORATION );
//            if ( po != null ) po.save( C, p );
            for ( Component c : ( (JComponent) C ).getComponents() ) save( (JComponent) c, p );
        }
    }
    
    private static void load( Component C, Preferences p )
    {
        if ( C instanceof EsoxPreferenceOwnerIF ) ( (EsoxPreferenceOwnerIF) C ).load( p );
        if ( C instanceof JComponent ) for ( Component c : ( (JComponent) C ).getComponents() ) load( (JComponent) c, p );
    }
}
