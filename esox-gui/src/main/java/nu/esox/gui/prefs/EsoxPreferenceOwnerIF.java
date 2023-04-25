package nu.esox.gui.prefs;

import java.util.prefs.Preferences;

public interface EsoxPreferenceOwnerIF
{
    void save( Preferences prefs );
    void load( Preferences prefs );
}
