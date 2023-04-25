package nu.esox.gui;

import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public class EsoxTable extends JTable
{
    private int m_preferredVisisbleRowCount;
    private int m_margin;
    private final Object [] m_values;
    
    
    public EsoxTable( int preferredVisisbleRowCount, int margin, Object ... values  )
    {
        this( null, preferredVisisbleRowCount, margin, values );
    }
    
    public EsoxTable( TableModel model, int preferredVisisbleRowCount, int margin, Object ... values  )
    {
        super( model );
        m_preferredVisisbleRowCount = preferredVisisbleRowCount;
        m_margin = margin;
        m_values = values;
    }
    
    @Override
    public void tableChanged( TableModelEvent ev )
    {
        super.tableChanged( ev );           
        if ( m_values != null ) try { prepare( this, m_preferredVisisbleRowCount, m_margin, m_values ); } catch ( Throwable t ) { t.printStackTrace(); }
    }
   
    
    public static void prepare( JTable t, int rows, int margin, Object ... values )
    {
        int H = 0;
        
        Object previousValue = "";
        
        for
            ( int i = 0; i < t.getColumnCount(); i++ )
        {
            Object value = ( i < values.length ) ? values[ i ] : previousValue;
            previousValue = value;

            
            TableColumn c = t.getColumnModel().getColumn( i );
//            Dimension d = t.getCellRenderer( -1, i ).getTableCellRendererComponent( t, c.getIdentifier(), true, true, -1, i ).getPreferredSize();
            Dimension d = t.getDefaultRenderer( String.class ).getTableCellRendererComponent( t, c.getIdentifier(), true, true, -1, i ).getPreferredSize();
            H = Math.max( H, d.height );
            int w = 
                Math.max( t.getCellRenderer( 0, i ).getTableCellRendererComponent( t, value, true, true, 0, i ).getPreferredSize().width,
                          d.width ) + margin;
            c.setPreferredWidth( w );
            c.setMinWidth( w );
            c.setMaxWidth( w );
        }
        
        
        
        if
            ( values.length > 0 )
        {
            t.getTableHeader().setPreferredSize( new Dimension( t.getTableHeader().getPreferredSize().width, H ) );
            t.setPreferredScrollableViewportSize( new java.awt.Dimension( t.getPreferredSize().width, t.getRowHeight() * rows ) );
        }
    }
}
