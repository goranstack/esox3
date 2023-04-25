package nu.esox.gui.prefs;

import java.util.prefs.Preferences;

public interface EsoxPreferenceDecorationIF
{
    void save( Preferences prefs );
    void load( Preferences prefs );
}
