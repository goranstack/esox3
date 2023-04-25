package nu.esox.gui.layout;


import java.io.Serializable;


@SuppressWarnings("serial")
public class EsoxSizeRequirements implements Serializable
{
    public int minimum;
    public int preferred;
    public int maximum;

    
    public EsoxSizeRequirements()
    {
        minimum = 0;
        preferred = 0;
        maximum = 0;
    }

    public EsoxSizeRequirements( int min, int pref, int max, float a )
    {
        minimum = min;
        preferred = pref;
        maximum = max;
    }

    public String toString()
    {
        return "[" + minimum + "," + preferred + "," + maximum + "]";
    }
}
