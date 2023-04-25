package nu.esox.gui.bind;


import javax.swing.*;
import javax.swing.event.*;
import nu.esox.obs.*;


public abstract class EsoxTableSelectionProjector implements ListSelectionListener
{
    private final JTable m_table;
    private final EsoxModelSocketIF m_modelSocket;
    private boolean m_isUpdating = false;


    public EsoxTableSelectionProjector( JTable table, EsoxModelSocketIF modelSocket )
    {
    	m_modelSocket = modelSocket;
        m_table = table;
        m_table.getSelectionModel().addListSelectionListener( this );
    }
    
	@Override
    public void valueChanged( ListSelectionEvent ev )
    {
        if ( ev.getValueIsAdjusting() ) return;

        if ( m_isUpdating ) return;
        m_isUpdating = true;
        
        if
            ( m_table.getSelectedRowCount() != 1 )
        {
        	m_modelSocket.attach( null );
        } else {
        	m_modelSocket.attach( getItemOfRow( m_table, m_table.getSelectedRow() ) );
        }
        m_isUpdating = false;
    }

    protected abstract EsoxObservableModelIF getItemOfRow( JTable t, int row );
}
