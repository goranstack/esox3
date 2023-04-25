package nu.esox.gui.bind;

import java.awt.Color;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import nu.esox.obs.*;


public class EsoxProjector extends EsoxObservableModelIF.Dispatcher
{
	public interface ExtractAspectValue { Object extract( EsoxObservableModelIF model ); }
	public interface ProjectAspectValue { void project( Object value ); }

	
    private final EsoxModelSocketIF m_modelSocket;
    private final ExtractAspectValue m_extract;
    private final ProjectAspectValue m_project;
    private final String m_aspectName;
    private Object m_nullProjection;
    private Object m_noModelProjection;
    
    private boolean m_isProjectionDirty = false;

    
    public EsoxProjector( EsoxModelSocketIF modelSocket,
    		             ExtractAspectValue extract,
    		             String aspectName,
    		             ProjectAspectValue project )
    {
    	this( modelSocket, extract, aspectName, project, null, null );
    }

    public EsoxProjector( EsoxModelSocketIF modelSocket,
    		             ExtractAspectValue extract,
    		             String aspectName,
    		             ProjectAspectValue project,
    		             Object nullProjection,
    		             Object noModelProjection )
    {
        if ( nullProjection == null ) nullProjection = EQUALS_NOTHING;

        m_modelSocket = modelSocket;
        m_extract = extract;
        m_project = project;
        m_aspectName = aspectName;
        m_nullProjection = nullProjection;
        m_noModelProjection = noModelProjection;

        if ( m_modelSocket.getModel() != null ) m_modelSocket.getModel().addObserver( this );
        m_modelSocket.addObserver( this );
        updateProjection();  
    }

    
    
    // Submodel

    public static EsoxProjector createSubmodelProjector( EsoxModelSocketIF modelSocket, EsoxModelSocketIF subModelSocket )
    {
    	return createSubmodelProjector( modelSocket, ( EsoxObservableModelIF model ) -> { return model; }, null, subModelSocket );
    }
    
    public static EsoxProjector createSubmodelProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, EsoxModelSocketIF subModelSocket )
    {
    	return new EsoxProjector( modelSocket, extract, aspectName, ( Object value ) -> { subModelSocket.attach( (EsoxObservableModelIF) value ); } );
    }
   
    
    // JTextField
    public static EsoxProjector createTextFieldTextProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, JTextField tf )
    {
    	return new EsoxProjector( modelSocket, extract, aspectName, ( Object value ) -> { tf.setText( value.toString() ); }, "", "" );
    }
    
    
    // Tool tip
    public static EsoxProjector createToolTipProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, JComponent l )
    {
    	return new EsoxProjector( modelSocket, extract, aspectName, ( Object value ) -> { l.setToolTipText( value.toString() ); }, "", "" );
    }
    
    
    // JComboBox
    public static EsoxProjector createComboBoxValueProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, JComboBox<?> cb )
    {
    	return new EsoxProjector( modelSocket, extract, aspectName, ( Object value ) -> { cb.setSelectedItem( value ); }, null, null );
    }
    
    // JSpinner
    public static EsoxProjector createSpinnerValueProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, JSpinner s )
    {
    	return new EsoxProjector( modelSocket, extract, aspectName, ( Object value ) -> { s.setValue( value ); }, null, null );
    }
    
    
    // JSlider
    public static EsoxProjector createSliderValueProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, JSlider s )
    {
    	return new EsoxProjector( modelSocket, extract, aspectName, ( Object value ) -> { s.setValue( (int) (Number) value ); }, 0, 0 );
    }
    
    public static EsoxProjector createSliderMinimumProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, JSlider s )
    {
    	return new EsoxProjector( modelSocket, extract, aspectName, ( Object value ) -> { s.setMinimum( (int) (Number) value ); }, 0, 0 );
    }
    
    public static EsoxProjector createSliderMaximumProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, JSlider s )
    {
    	return new EsoxProjector( modelSocket, extract, aspectName, ( Object value ) -> { s.setMaximum( (int) (Number) value ); }, 0, 0 );
    }
    
    // JLabel
    public static EsoxProjector createLabelTextProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, JLabel l )
    {
    	return new EsoxProjector( modelSocket, extract, aspectName, ( Object value ) -> { l.setText( value.toString() ); }, "", "" );
    }
    
    public static EsoxProjector createLabelIconProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, JLabel l )
    {
    	return new EsoxProjector( modelSocket, extract, aspectName, ( Object value ) -> { l.setIcon( (javax.swing.Icon) value ); l.repaint(); }, null, null );
    }
    
    public static EsoxProjector createLabelRepaintProjector( EsoxModelSocketIF modelSocket, String aspectName, JLabel l )
    {
    	return new EsoxProjector( modelSocket, ( EsoxObservableModelIF model ) -> { return null; }, aspectName, ( Object value ) -> { l.repaint(); }, null, null );
    }
    
    
   
    // AbstractButton
    public static EsoxProjector createButtonTextProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, AbstractButton b )
    {
    	return new EsoxProjector( modelSocket, extract, aspectName, ( Object value ) -> { b.setText( value.toString() ); }, "", "" );
    }
   
    public static EsoxProjector createButtonSelectedProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, AbstractButton b )
    {
    	return new EsoxProjector( modelSocket, extract, aspectName, ( Object value ) -> { b.setSelected( (boolean) value ); }, false, false );
    }
   
    // Border
    public static EsoxProjector createBorderTitleProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, TitledBorder b )
    {
    	return new EsoxProjector( modelSocket, extract, aspectName, ( Object value ) -> { b.setTitle( value.toString() ); }, "", "" );
    }
    
    // Action
    public static EsoxProjector createActionNameProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, AbstractAction a )
    {
    	return new EsoxProjector( modelSocket, extract, aspectName, ( Object value ) -> { a.putValue( Action.NAME, value.toString() ); }, "", "" );
    }
    
    public static EsoxProjector createActionEnabledProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, AbstractAction ... as  )
    {
    	return new EsoxProjector( modelSocket, extract, null, ( Object value ) -> { for ( AbstractAction a : as ) a.setEnabled( (boolean) value ); }, false, false );
    }
    
    public static EsoxProjector createActionEnabledProjector( EsoxPredicateIF predicate, AbstractAction ... as )
    {
    	return new EsoxProjector( new EsoxModelSocket( predicate ), ( EsoxObservableModelIF model ) -> { return ( (EsoxPredicateIF) model ).isTrue(); }, null, ( Object value ) -> { for ( AbstractAction a : as ) a.setEnabled( (boolean) value ); }, false, false );
    }

    
    // Tabbed pane
    public static EsoxProjector createTabbedPaneTextProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, JTabbedPane tp, int column )
    {
    	return new EsoxProjector( modelSocket, extract, aspectName, ( Object value ) -> { tp.setTitleAt( column, value.toString() ); }, "", "" );
    }
    
    public static EsoxProjector createTabbedPaneEnabledProjector( EsoxPredicateIF predicate, JTabbedPane tp, int column )
    {
    	return new EsoxProjector( new EsoxModelSocket( predicate ), ( EsoxObservableModelIF model ) -> { return ( (EsoxPredicateIF) model ).isTrue(); }, null, ( Object value ) -> { tp.setEnabledAt( column, (boolean) value ); }, false, false );
    }
    
    // Table
    public static EsoxProjector createTableHeaderProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, EsoxObservableListTableModel<?> tm, int column )
    {
    	return new EsoxProjector( modelSocket, extract, aspectName, ( Object value ) -> { tm.setColumnName( value.toString(), column ); }, "", "" );
    }
    
    
    // Dialog
    public static EsoxProjector createDialogTitleProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, JDialog d )
    {
    	return new EsoxProjector( modelSocket, extract, aspectName, ( Object value ) -> { d.setTitle( value.toString() ); }, "", "" );      
    }
    
    // Progress bar
    public static EsoxProjector createProgressBarValueProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, JProgressBar b )
    {
    	return new EsoxProjector( modelSocket, extract, aspectName, ( Object value ) -> { b.setValue( ( value instanceof Number ) ? (int) (Number) value : 0 ); }, 0, 0 );      
    }
    
    public static EsoxProjector createProgressBarMaximumProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, JProgressBar b )
    {
    	return new EsoxProjector( modelSocket, extract, aspectName, ( Object value ) -> { b.setMaximum( ( value instanceof Number ) ? (int) (Number) value : 0 ); }, 0, 0 );      
    }
    
    public static EsoxProjector createProgressBarIntermediateProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, JProgressBar b )
    {
    	return new EsoxProjector( modelSocket, extract, aspectName, ( Object value ) -> { b.setIndeterminate( ( value instanceof Boolean ) ? (Boolean) value : false ); }, false, false );      
    }

    
    
    public static EsoxProjector createForegroundProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, JComponent c )
    {
    	return new EsoxProjector( modelSocket, extract, aspectName, ( Object value ) -> { c.setForeground( ( value instanceof Color ) ? (Color) value : null ); }, null, null );      
    }
    
    public static EsoxProjector createBackgroundProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, JComponent c )
    {
    	return new EsoxProjector( modelSocket, extract, aspectName, ( Object value ) -> { c.setBackground( ( value instanceof Color ) ? (Color) value : null ); }, null, null );      
    }
    
    public static EsoxProjector createEnabledProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, JComponent c )
    {
    	return new EsoxProjector( modelSocket, extract, null, ( Object value ) -> { c.setEnabled( (boolean) value ); }, false, false );
    }

    public static EsoxProjector createEnabledProjector( EsoxPredicateIF predicate, JComponent c )
    {
    	return new EsoxProjector( new EsoxModelSocket( predicate ), ( EsoxObservableModelIF model ) -> { return ( (EsoxPredicateIF) model ).isTrue(); }, null, ( Object value ) -> { c.setEnabled( (boolean) value ); }, false, false );
    }
    
    public static EsoxProjector createVisibleProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue extract, String aspectName, JComponent c )
    {
    	return new EsoxProjector( modelSocket, extract, null, ( Object value ) -> { c.setVisible( (boolean) value ); }, false, false );
    }

    public static EsoxProjector createVisibleProjector( EsoxPredicateIF predicate, JComponent c )
    {
    	return new EsoxProjector( new EsoxModelSocket( predicate ), ( EsoxObservableModelIF model ) -> { return ( (EsoxPredicateIF) model ).isTrue(); }, null, ( Object value ) -> { c.setVisible( (boolean) value ); }, false, false );
    }


    public EsoxProjector setNullProjection( Object nullProjection )
    {
        m_nullProjection = nullProjection;
        return this;
    }

    public EsoxProjector setNoModelProjection( Object noModelProjection )
    {
        m_noModelProjection = noModelProjection;
        return this;
    }
    
//    protected final ModelSocketIF getModelSocket() { return m_modelSocket; }
//    protected final Object getNullProjection() { return m_nullProjection; }
    
    @Override
    public void begin()
    {
        m_isProjectionDirty = false;
    }
    
    @Override
    public void valueChanged( EsoxObservableIF source, String propertyName, Object newValue, Object oldValue )
    {
        if      ( source == m_modelSocket )            modelSocketValueChanged( newValue,  oldValue );
        else if ( source == m_modelSocket.getModel() ) modelValueChanged( propertyName );
        else                                           someOtherValueChanged( source, propertyName, newValue, oldValue );
    }
      
    @Override
    public void end()
    {
        if ( m_isProjectionDirty ) updateProjection();
    }

 
    protected final void invalidateProjection()
    {
        m_isProjectionDirty = true;
    }
    
    private void modelSocketValueChanged( Object newValue, Object oldValue )
    {
        if ( oldValue != null ) ( (EsoxObservableModelIF) oldValue ).removeObserver( this );
        if ( newValue != null ) ( (EsoxObservableModelIF) newValue ).addObserver( this );
        invalidateProjection();      
    }
    
    private void modelValueChanged( String propertyName )
    {
        if
            ( ( m_aspectName == null ) || ( m_aspectName.equals( propertyName ) ) )
        {
            invalidateProjection();
        }
    }
    
    protected void someOtherValueChanged( EsoxObservableIF source, String propertyName, Object newValue, Object oldValue )
    {
    }

    
    
    
    protected final Object extractProjectedValue()
    {
        if
            ( m_modelSocket.getModel() == null )
        {
            return m_noModelProjection;
        } else {
            Object aspectValue = m_extract.extract( m_modelSocket.getModel() );
          
            if
                ( ( aspectValue == null ) && ( m_nullProjection != EQUALS_NOTHING ) )
            {
                return m_nullProjection;
            } else {
                return deriveProjection( aspectValue );
            }
        }
    }

    protected final void updateProjection()
    {
        m_project.project( extractProjectedValue() );
    }
 


    protected Object deriveProjection( Object aspectValue )
    {
        return aspectValue;
    }



    private static final Object EQUALS_NOTHING =
        new Object()
        {
            public boolean equals( Object o ) { return false; }
        };
        
}
