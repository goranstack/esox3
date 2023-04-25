package nu.esox.gui;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;


@SuppressWarnings("serial")
public class EsoxSpinnerNumberEditor extends AbstractCellEditor implements TableCellEditor
{
    private final JSpinner m_spinner;
    
    public EsoxSpinnerNumberEditor( int start, int min, int max, int step )
    {
        m_spinner = new JSpinner( new SpinnerNumberModel( start, min, max, step ) );
    }
    
    
    @Override
    public Object getCellEditorValue()
    {
        return m_spinner.getValue();
    }


    @Override
    public Component getTableCellEditorComponent( JTable t, Object value, boolean arg2, int arg3, int arg4 )
    {
        m_spinner.setValue( value );
        return m_spinner;
    }
}
