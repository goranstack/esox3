package nu.esox.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;


@SuppressWarnings("serial")
public class EsoxWizard extends JDialog
{
    public static abstract class Page extends JPanel
    {
        public Page() { super(); }
        public Page( LayoutManager lm ) { super( lm ); }
        
        public void fromPrevious( EsoxWizard w ) {}
        public void toNext( EsoxWizard w ) {}
        public void fromNext( EsoxWizard w ) {}
        public void toPrevious( EsoxWizard w ) {}
    }
    
    public interface Model
    {
        void start( EsoxWizard w );
        void moved( Page from, Page to );
        void cancelled( EsoxWizard w );
        void finish( EsoxWizard w );
    }
    
    
    private final JPanel m_workspace = new JPanel( new CardLayout() );
    private final EsoxButton m_back = new EsoxButton( "Back", null, new ActionListener() { public void actionPerformed( ActionEvent ev ) { back(); } } );
    private final EsoxButton m_next = new EsoxButton( "Next", null, new ActionListener() { public void actionPerformed( ActionEvent ev ) { next(); } } );
    private final EsoxButton m_cancel = new EsoxButton( "Cancel", null, new ActionListener() { public void actionPerformed( ActionEvent ev ) { cancel(); } } );
    private final EsoxButton m_finish = new EsoxButton( "Finish", null, new ActionListener() { public void actionPerformed( ActionEvent ev ) { finish(); } } );
 
    private Model m_model;
    private int m_current = 0;
    
    
    public EsoxWizard( Frame owner, String title, boolean modal )
    {
        super( owner, title, modal );
         init();
    }

    public EsoxWizard( Dialog owner, String title, boolean modal )
    {
        super( owner, title, modal );
         init();
    }
    
    
    public final Model getModel() { return m_model; }
    public final EsoxButton getBackButton() { return m_back; }
    public final EsoxButton getNextButton() { return m_next; }
    public final EsoxButton getCancelButton() { return m_cancel; }
    public final EsoxButton getFinishButton() { return m_finish; }

    public void add( Page ... pages )
    {
        for ( Page page : pages ) m_workspace.add( page );
    }
    
    public void start( JComponent owner, Model model )
    {
        m_model = model;
        m_model.start( this );

        m_current = 0;
        refresh();
        ( (CardLayout) m_workspace.getLayout() ).first( m_workspace );
        m_model.moved( null, getCurrent() );

        pack();
        setLocationRelativeTo( owner );
        setVisible( true );
    }
    
    
    
    
    
    private Page getCurrent()
    {
        return (Page) m_workspace.getComponent( m_current );
    } 
      
      
    private void refresh()
    {
        m_back.setEnabled( m_current > 0 );
        m_next.setEnabled( m_current < m_workspace.getComponentCount() - 1 );
        m_finish.setEnabled( m_current == m_workspace.getComponentCount() - 1 );
    }
    
    private void init()
    {
       JPanel buttons = new JPanel( new FlowLayout( FlowLayout.CENTER, 5, 5 ) );
       add( buttons, BorderLayout.SOUTH );
       buttons.add( m_back );
       buttons.add( m_cancel );
       buttons.add( m_finish );
       buttons.add( m_next );

       add( m_workspace, BorderLayout.CENTER );   
    }
    
    private void back()
    {       
        Page from = getCurrent();
        m_current--;
        Page to = getCurrent();
        
        refresh();
        from.toPrevious( this );

        ( (CardLayout) m_workspace.getLayout() ).previous( m_workspace );

        to.fromNext( this );
        m_model.moved( from, to );
    }
    
    private void next()
    {       
        Page from = getCurrent();
        m_current++;
        Page to = getCurrent();
        
        refresh();
        from.toNext( this );

        ( (CardLayout) m_workspace.getLayout() ).next( m_workspace );

        to.fromPrevious( this );
        m_model.moved( from, to );
    }
    
    private void cancel()
    {       
        m_model.cancelled( this );
        setVisible( false );
    }
    
    private void finish()
    {       
        m_model.finish( this );
        setVisible( false );
   }
}
