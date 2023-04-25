package nu.esox.gui.layout;

  /*
    Copyright (C) 2002  Dennis Malmström (dennis.malmstrom@telia.com)

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.
    
    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
  */

import java.awt.*;



public class EsoxColumnLayout implements LayoutManager2
{
    public static final Object FILL = "FILL";
	
    public static enum Width { OWN, SIBBLINGS, PARENT };
    
    private static final boolean DEBUG = false;

    private int m_spacing;
    private Width m_width = Width.OWN;   
    private double m_alignmentY;
    private double m_alignmentX;
    private Component m_fillComponent;

    private transient EsoxSizeRequirements[] m_h;
    private transient EsoxSizeRequirements m_W;
    private transient EsoxSizeRequirements m_H;

    
    public EsoxColumnLayout()
    {
        this( 0, Width.OWN, 0, 0 );
    }
       
    public EsoxColumnLayout( int spacing )
    {
        this( spacing, Width.OWN, 0, 0 );
    }
       
    public EsoxColumnLayout( int spacing, Width width, double alignmentY, double alignmentX )
    {
        m_spacing = spacing;
        m_width = width;
        m_alignmentY = alignmentY;
        m_alignmentX = alignmentX;
    }

    public EsoxColumnLayout setFillX( Width fx )
    {
    	m_width = fx;
    	return this;
    }

    public EsoxColumnLayout setAlignmentX( double a )
    {
    	m_alignmentX = a;
    	return this;
    }

    public EsoxColumnLayout setAlignmentY( double a )
    {
    	m_alignmentY = a;
    	return this;
    }
    
    public void addLayoutComponent( Component comp, Object constraints )
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
            ( m_H == null )
        {
            int I = target.getComponentCount();

            m_W = new EsoxSizeRequirements();
            m_H = new EsoxSizeRequirements();
            m_h = new EsoxSizeRequirements[ I ];
            for
                ( int i = 0; i < I; i++ )
            {
                m_h[ i ] = new EsoxSizeRequirements();
            }
		
            for
                ( int i = 0; i < I; i++ )
            {
                Component c = target.getComponent( i );
                if ( ! c.isVisible() ) continue;
                
                Dimension min = c.getMinimumSize();
                Dimension typ = c.getPreferredSize();
                Dimension max = c.getMaximumSize();
			
                m_h[ i ].minimum =   (int) min.height;
                m_h[ i ].preferred = (int) typ.height;
                m_h[ i ].maximum =   (int) max.height;

                m_W.minimum =   (int) Math.max( (long) m_W.minimum,   min.width );
                m_W.preferred = (int) Math.max( (long) m_W.preferred, typ.width );
                m_W.maximum =   (int) Math.max( (long) m_W.maximum,   max.width );

                m_H.minimum += m_h[ i ].minimum + ( ( i == 0 ) ? 0 : m_spacing );
                m_H.preferred += m_h[ i ].preferred + ( ( i == 0 ) ? 0 : m_spacing );
                m_H.maximum += m_h[ i ].maximum + ( ( i == 0 ) ? 0 : m_spacing );

                if ( m_H.minimum < 0 ) m_H.minimum = Integer.MAX_VALUE;
                if ( m_H.preferred < 0 ) m_H.preferred = Integer.MAX_VALUE;
                if ( m_H.maximum < 0 ) m_H.maximum = Integer.MAX_VALUE;
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
        m_H = null;
        m_h = null;
        m_W = null;
    }
    
    @SuppressWarnings("unused")
    public void layoutContainer( Container target )
    {
        checkRequests( target );

        Insets in = target.getInsets();

        int W =
            ( m_width == Width.PARENT )
            ?
            ( target.getWidth() - ( in.left + in.right ) )
            :
            ( Math.min( m_W.preferred, target.getWidth() - ( in.left + in.right ) ) );
                
        int Y = ( m_fillComponent != null ) ? 0 : (int) ( m_alignmentY * ( target.getHeight() - ( in.top + in.bottom ) - m_H.preferred ) );
        Y += in.top;
        
        int fillComponentIndex = -1;
        int X = (int) Math.min( in.left, Short.MAX_VALUE );

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

            int y = Math.min( Y, Short.MAX_VALUE );
            int w = ( m_width == Width.OWN ) ? c.getPreferredSize().width : Math.min( W, Short.MAX_VALUE );
            int h = Math.min( m_h[ i ].preferred, Short.MAX_VALUE );
            Y += h + m_spacing;
            int x = (int) ( X + getAlignmentfor( c ) * ( W - w ) );

            if
                ( DEBUG && target.getName() != null )
            {
                System.err.println( x + " " + y + " " + w + " " + h + " ::: " + c.getName() + " (" + c.getClass() + ")" );
            }

            c.setBounds( x, y, w, h );
        }

        if
            ( m_fillComponent != null )
        {
            Y -= m_spacing;
            int H = target.getHeight() - in.bottom;
            if
                ( H > Y )
            {
                Dimension d = m_fillComponent.getSize();
                int dH = H - Y;
                d.height += dH;
                m_fillComponent.setSize( d );

                if
                    ( DEBUG && target.getName() != null )
                {
                    System.err.println( "FILL " + d.height + " ::: " + m_fillComponent.getName() + " (" + m_fillComponent.getClass() + ")" );
                }
                
                for
                    ( int i = fillComponentIndex + 1; i < I; i++ )
                {
                    Component c = target.getComponent( i );
                    Point p = c.getLocation();
                    p.y += dH;
                    c.setLocation( p );

                    if
                        ( DEBUG && target.getName() != null )
                    {
                        System.err.println( "FILL " + p.y + " ::: " + c.getName() + " (" + c.getClass() + ")" );
                    }
                }
            }
        }
    }

    private double getAlignmentfor( Component c )
    {
        return Double.isNaN( m_alignmentX ) ? c.getAlignmentX() : m_alignmentX;
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
