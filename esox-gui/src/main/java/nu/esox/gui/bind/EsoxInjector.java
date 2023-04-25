package nu.esox.gui.bind;

import java.awt.event.*;
import java.lang.reflect.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;


public class EsoxInjector
{
	public interface InjectAspectValue
	{
	    void set( Object model, Object aspectValue );
	}
	
    private final EsoxModelSocketIF m_modelSocket;
    private final InjectAspectValue m_inject;
    
    
    public static EsoxInjector createTextFieldInjector( EsoxModelSocketIF modelSlot, InjectAspectValue set, JTextField tf, boolean addFocusBehaviour )
    {
    	EsoxInjector a = new EsoxInjector( modelSlot, set );
        
        tf.addActionListener( ( ActionEvent ev ) -> { a.injectAspectValue( tf.getText() ); } );
        if ( addFocusBehaviour ) tf.addFocusListener( new FocusListener()
        		{
        			public void focusLost( FocusEvent ev ) { tf.postActionEvent(); }    
        			public void focusGained( FocusEvent ev ) { tf.selectAll(); } 		
        		} );
        
        return a;
    }
    
    public static EsoxInjector createToggleButtonInjector( EsoxModelSocketIF modelSlot, InjectAspectValue set, JToggleButton b )
    {
    	EsoxInjector a = new EsoxInjector( modelSlot, set );        
        b.addActionListener( ( ActionEvent ev ) -> { a.injectAspectValue( b.isSelected() ); } );        
        return a;
    }
    
    public static EsoxInjector createComboBoxInjector( EsoxModelSocketIF modelSlot, InjectAspectValue set, JComboBox<?> cb )
    {
    	EsoxInjector a = new EsoxInjector( modelSlot, set );        
        cb.addItemListener( ( ItemEvent ev ) -> { a.injectAspectValue( cb.getSelectedItem() ); } );
        return a;
    }
    
    public static EsoxInjector createSpinnerInjector( EsoxModelSocketIF modelSlot, InjectAspectValue set, JSpinner s )
    {
    	EsoxInjector a = new EsoxInjector( modelSlot, set );        
        s.addChangeListener( ( ChangeEvent ev ) -> { a.injectAspectValue( s.getValue() ); } );
        return a;
    }
    
    public static EsoxInjector createSliderInjector( EsoxModelSocketIF modelSlot, InjectAspectValue set, JSlider s )
    {
    	EsoxInjector a = new EsoxInjector( modelSlot, set );        
        s.addChangeListener( ( ChangeEvent ev ) -> { if ( ! s.getValueIsAdjusting() ) a.injectAspectValue( s.getValue() ); } );
        return a;
    }

    
    
    
    
    protected EsoxInjector( EsoxModelSocketIF modelSocket, InjectAspectValue set )
    {   
    	m_modelSocket = modelSocket;
        m_inject = set;
    }

    protected final void injectAspectValue( Object projection )
    {
        if ( m_modelSocket.getModel() == null ) return;

        m_inject.set( m_modelSocket.getModel(), deriveAspectValue( projection ) );
    }
 
    protected Object deriveAspectValue( Object projection )
    {
        return projection;
    }
    
    protected static class DefaultInjectAspectValue implements InjectAspectValue
    {
        private final Method m_setAspectMethod;

        
        public DefaultInjectAspectValue( Class<?> modelClass, String setAspectMethodName, Class<?> aspectClass )
        {
            try
            {
             	m_setAspectMethod = modelClass.getMethod( setAspectMethodName, aspectClass );
            }
            catch ( NoSuchMethodException ex )
            {
                throw new Error( "No such method: " + modelClass + "." + setAspectMethodName + "( " + aspectClass.getName() + ")" );
            }
        }
        
        public void set( Object model, Object aspectValue )
        {
            if ( m_setAspectMethod == null ) return;

            boolean tmp = m_setAspectMethod.canAccess( model );
            try
            {
                m_setAspectMethod.setAccessible( true );
                m_setAspectMethod.invoke( model, aspectValue );
            }
            catch ( IllegalAccessException ex )
            {
                throw new Error( "Method not accessible: " + m_setAspectMethod );
            }
            catch ( InvocationTargetException ex )
            {
                throw new Error( "Invocation failure: " + m_setAspectMethod + ", " + aspectValue, ex );
            }
            catch ( Throwable ex )
            {
                throw new Error( "Failure: " + m_setAspectMethod + ", " + aspectValue, ex );
            }
            finally
            {
                m_setAspectMethod.setAccessible( tmp );
            }
        }
    }

}
