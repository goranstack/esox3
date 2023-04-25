package nu.esox.gui.layout;

import java.awt.*;



public class EsoxRowLayout implements LayoutManager2
{
    public static final String FILL = "FILL";
    public static final double LEFT = 0.0;
    public static final double CENTER = 0.5;
    public static final double RIGHT = 1.0;

    private static final boolean DEBUG = false;

    private int m_spacing;
    private boolean m_sticky = false;
    private boolean m_stretch = false;
    private double m_justification;
    private double m_alignment;
    private Component m_fillComponent;

    private transient EsoxSizeRequirements[] m_w;
    private transient EsoxSizeRequirements m_H;
    private transient EsoxSizeRequirements m_W;

    
    public EsoxRowLayout()
    {
        this( 0 );
    }
    
    public EsoxRowLayout( int spacing )
    {
        this( spacing, false );
    }
    
    public EsoxRowLayout( int spacing, boolean sticky )
    {
        this( spacing, sticky, LEFT );
    }
    
    public EsoxRowLayout( int spacing, boolean sticky, boolean stretch )
    {
        this( spacing, sticky, stretch, LEFT, 0 );
    }
    
    public EsoxRowLayout( boolean sticky )
    {
        this( 0, sticky );
    }
    
    public EsoxRowLayout( boolean sticky, boolean stretch )
    {
        this( 0, sticky, stretch );
    }
    
    public EsoxRowLayout( boolean sticky, double justification )
    {
        this( 0, sticky, justification );
    }
    
    public EsoxRowLayout( int spacing, boolean sticky, double justification )
    {
        this( spacing, sticky, false, justification, 0 );
    }
    
    public EsoxRowLayout( int spacing, boolean sticky, boolean stretch, double justification, double alignment )
    {
        m_spacing = spacing;
        m_sticky = sticky;
        m_stretch = stretch;
        m_justification = justification;
        m_alignment = alignment;
    }


    public void addLayoutComponent(Component comp, Object constraints)
    {
        if
            ( constraints == FILL )
        {
            m_fillComponent = comp;
        }
    }

    public void addLayoutComponent(String name, Component comp)
    {
        if
            ( name == FILL )
        {
            m_fillComponent = comp;
        }
    }

    void checkRequests( Container target )
    {
        if
            ( m_W == null )
        {
            int I = target.getComponentCount();

            m_H = new EsoxSizeRequirements();
            m_W = new EsoxSizeRequirements();
            m_w = new EsoxSizeRequirements[ I ];
            for
                ( int i = 0; i < I; i++ )
            {
                m_w[ i ] = new EsoxSizeRequirements();
            }
		
            for
                ( int i = 0; i < I; i++ )
            {
                Component c = target.getComponent( i );
                if ( ! c.isVisible() ) continue;
			
                Dimension min = c.getMinimumSize();
                Dimension typ = c.getPreferredSize();
                Dimension max = c.getMaximumSize();
			
                m_w[ i ].minimum =   (int) min.width;
                m_w[ i ].preferred = (int) typ.width;
                m_w[ i ].maximum =   (int) max.width;

                m_H.minimum =   (int) Math.max( (long) m_H.minimum,   min.height );
                m_H.preferred = (int) Math.max( (long) m_H.preferred, typ.height );
                m_H.maximum =   (int) Math.max( (long) m_H.maximum,   max.height );

                m_W.minimum += m_w[ i ].minimum + ( ( i == 0 ) ? 0 : m_spacing );
                m_W.preferred += m_w[ i ].preferred + ( ( i == 0 ) ? 0 : m_spacing );
                m_W.maximum += m_w[ i ].maximum + ( ( i == 0 ) ? 0 : m_spacing );

                if ( m_W.minimum < 0 ) m_W.minimum = Integer.MAX_VALUE;
                if ( m_W.preferred < 0 ) m_W.preferred = Integer.MAX_VALUE;
                if ( m_W.maximum < 0 ) m_W.maximum = Integer.MAX_VALUE;
            }
        }
    }

    public float getLayoutAlignmentX(Container target)
    {
        checkRequests(target);
        return 0;//m_W.alignment;
    }

    public float getLayoutAlignmentY(Container target)
    {
        checkRequests(target);
        return 0;//m_H.alignment;
    }

    public void invalidateLayout(Container target)
    {
        m_W = null;
        m_w = null;
        m_H = null;
    }

    @SuppressWarnings("unused")
    public void layoutContainer( Container target )
    {
        checkRequests( target );

        Insets in = target.getInsets();
        
        int H =
            m_stretch
            ?
            ( target.getHeight() - ( in.top + in.bottom ) )
            :
            ( Math.min( m_H.preferred, target.getHeight() - ( in.top + in.bottom ) ) );

        int X = ( m_fillComponent != null ) ? 0 : (int) ( m_justification * ( target.getWidth() - ( in.left + in.right ) - m_W.preferred ) );
        X += in.left;
        
        int fillComponentIndex = -1;
        int Y = (int) Math.min( (int) in.top, Short.MAX_VALUE );

        if
            ( DEBUG && target.getName() != null )
        {
            System.err.println( target.getName() + ": " + target.getHeight() + "   " + in );
        }

        int I = target.getComponentCount();
        for
            ( int i = 0; i < I; i++ )
        {
            Component c = target.getComponent( i );
            if ( ! c.isVisible() ) continue;
            if ( c == m_fillComponent ) fillComponentIndex = i;

            int x = Math.min( X, Short.MAX_VALUE );
            int w = Math.min( m_w[ i ].preferred, Short.MAX_VALUE );
            int h = ( m_sticky ? Math.min( H, Short.MAX_VALUE ) : c.getPreferredSize().height );
            X += w + m_spacing;
            int y = (int) ( Y + getAlignmentfor( c ) * ( H - h ) );

            if
                ( DEBUG && target.getName() != null )
            {
                System.err.print( x + " " + y + " " + w + " " + h + " ::: " + c.getName() + " (" + c.getClass() + ")" );
            }

            c.setBounds( x, y, w, h );
        }

        if
            ( m_fillComponent != null )
        {
            X -= m_spacing;
            int W = target.getWidth() - in.right;
            if
                ( W > X )
            {
                Dimension d = m_fillComponent.getSize();
                int dW = W - X;
                d.width += dW;
                m_fillComponent.setSize( d );

                if
                    ( DEBUG && target.getName() != null )
                {
                    System.err.println( "FILL " + d.width + " ::: " + m_fillComponent.getName() + " (" + m_fillComponent.getClass() + ")" );
                }

                for
                    ( int i = fillComponentIndex + 1; i < I; i++ )
                {
                    Component c = target.getComponent( i );
                    Point p = c.getLocation();
                    p.x += dW;
                    c.setLocation( p );

                    if
                        ( DEBUG && target.getName() != null )
                    {
                        System.err.println( "FILL " + p.x + " ::: " + c.getName() + " (" + c.getClass() + ")" );
                    }
                }
            }
        }
    }

    private double getAlignmentfor( Component c )
    {
        return Double.isNaN( m_alignment ) ? c.getAlignmentY() : m_alignment;
    }
    
    public Dimension maximumLayoutSize( Container target )
    {
        checkRequests( target );
        Dimension size = new Dimension( m_W.maximum, m_H.maximum );
        Insets insets = target.getInsets();
        size.width = (int) Math.min( (long) size.width + (long) insets.left + (long) insets.right, Short.MAX_VALUE );
        size.height = (int) Math.min( (long) size.height + (long) insets.top + (long) insets.bottom, Short.MAX_VALUE );
        return size;
    }

    public Dimension minimumLayoutSize( Container target )
    {      
        checkRequests( target );
        Dimension size = new Dimension( m_W.minimum, m_H.minimum );
        Insets insets = target.getInsets();
        size.width = (int) Math.min( (long) size.width + (long) insets.left + (long) insets.right, Short.MAX_VALUE );
        size.height = (int) Math.min( (long) size.height + (long) insets.top + (long) insets.bottom, Short.MAX_VALUE );
        return size;
    }

    public Dimension preferredLayoutSize( Container target )
    {      
        checkRequests( target );
        Dimension size = new Dimension( m_W.preferred, m_H.preferred );
        Insets insets = target.getInsets();
        size.width = (int) Math.min( (long) size.width + (long) insets.left + (long) insets.right, Short.MAX_VALUE );
        size.height = (int) Math.min( (long) size.height + (long) insets.top + (long) insets.bottom, Short.MAX_VALUE );
        return size;
    }

    public void removeLayoutComponent(Component comp)
    {
        if ( m_fillComponent == comp ) m_fillComponent = null;
    }
}
