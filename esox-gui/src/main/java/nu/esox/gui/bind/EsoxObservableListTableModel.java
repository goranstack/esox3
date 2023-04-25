package nu.esox.gui.bind;


import javax.swing.event.*;
import javax.swing.table.*;
import nu.esox.obs.*;


@SuppressWarnings("serial")
public abstract class EsoxObservableListTableModel<T> extends AbstractTableModel
{
    private EsoxObservableListIF<T> m_data;
    private boolean m_isEditable;
   
    
    public EsoxObservableListTableModel( EsoxObservableListIF<T> data, boolean isEditable )
    {
        setData( data );
        m_isEditable = isEditable;
    }
    
    public EsoxObservableListTableModel( EsoxObservableListIF<T> data )
    {
        this( data, true );
    }
    
    public EsoxObservableListTableModel()
    {
        this( null, true );
    }

    
    public java.util.List<T> getData()
    {
        return m_data;
    }

    public void setData( EsoxObservableListIF<T> data )
    {
        if ( m_data != null ) m_data.removeObserver( m_observer );
        m_data = data;
        if ( ( m_data != null ) && ( getTableModelListeners().length > 0 ) ) m_data.addObserver( m_observer );
        postSetData();
        fireTableDataChanged();
    }

    protected void postSetData() {}

    public void addTableModelListener( TableModelListener l )
    {
        if ( ( m_data != null ) && ( getTableModelListeners().length == 0 ) ) m_data.addObserver( m_observer );
        super.addTableModelListener( l );
    }

    public void removeTableModelListener( TableModelListener l )
    {
        super.removeTableModelListener( l );
        if ( ( m_data != null ) && ( getTableModelListeners().length == 0 ) ) m_data.removeObserver( m_observer );
    }

    
    
    private final EsoxObservableListIF.Dispatcher<T> m_observer =
    		new EsoxObservableListIF.Dispatcher<T>()
    		{
    	        public void changed( EsoxObservableList<T> list, T element, EsoxObservableIF.ObservationIF observation )
    	        {
    	        	int i = m_data.indexOf( element );
                    fireTableRowsUpdated( i, i );
    	        }
    	        public void set( EsoxObservableList<T> list, int index, T element, T previousElement ) { fireTableRowsUpdated( index, index ); }
    	        public void added( EsoxObservableList<T> list, int index, T element ) { fireTableRowsInserted( index, index ); }
    	        public void removed( EsoxObservableList<T> list, int index, T element ) { fireTableRowsDeleted( index, index ); }
    	        public void cleared( EsoxObservableList<T> list ) { fireTableDataChanged(); }
    		};
    

    
    public static abstract class Column<T>
    {
        private String m_name;
        private final Class<?> m_class;
        private final boolean m_isEditable;

        public Column( String name, Class<?> c, boolean isEditable )
        {
            m_name = name;
            m_class = c;
            m_isEditable = isEditable;
        }

        public String getName() { return m_name; }
        public final Class<?> getColumnClass() { return m_class; }
        public boolean isEditable() { return m_isEditable; }
        
        private void setName( String name ) { m_name = name; }
        
        public abstract Object getValue( T target );
        public void setValue( T target, Object value )
        {
            assert false;
        }
    };



    
    protected abstract Column<T> [] getColumns();

    



    
    public int getRowCount()
    {
        return ( m_data == null ) ? 0 : m_data.size();
    }
    
    public Object getValueAt( int row, int column )
    {
        return ( m_data == null ) ? null : getColumns()[ column ].getValue( m_data.get( row ) );
    }

    public String getColumnName( int column )
    {
        return getColumns()[ column ].getName();
    }
    
    public int getColumnCount()
    {
        return getColumns().length;
    }

    public Class<?> getColumnClass( int column )
    {
        return getColumns()[ column ].getColumnClass();
    }
    
    public boolean isCellEditable( int row, int column )
    {
        return m_isEditable && getColumns()[ column ].isEditable();
    }

    public void setValueAt( Object value, int row, int column )
    {
        getColumns()[ column ].setValue( m_data.get( row ), value );
    }
    
    

    public void setColumnName( String name, int column )
    {
        getColumns()[ column ].setName( name );
        fireTableCellUpdated( -1, column );
    }
    
}
