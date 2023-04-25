package nu.esox.gui;

import java.awt.*;

import javax.swing.*;


@SuppressWarnings("serial")
public class EsoxProgressDialog extends JDialog
{
    private final JProgressBar m_progressBar = new JProgressBar();
    private final JLabel m_label = new JLabel( "" );
    
    
    public EsoxProgressDialog( Frame owner, String title, String longestText )
    {
        super( owner, title, true );
        
        JPanel p = new JPanel( new BorderLayout( 5, 5 ) );
        add( p );
        p.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        p.add( m_progressBar, BorderLayout.NORTH );
        p.add( m_label );
        
        m_label.setText( longestText );
        m_label.setPreferredSize( m_label.getPreferredSize() );
        m_label.setText( "" );
        pack();
   }

    public EsoxProgressDialog( Dialog owner, String title, String longestText )
    {
        super( owner, title, true );
        
        JPanel p = new JPanel( new BorderLayout( 5, 5 ) );
        add( p );
        p.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        p.add( m_progressBar, BorderLayout.NORTH );
        p.add( m_label );
        
        m_label.setText( longestText );
        m_label.setPreferredSize( m_label.getPreferredSize() );
        m_label.setText( "" );
        pack();
    }
    
 //   public JProgressBar getProgressBar() { return m_progressBar; }
    
    public void progress( int i )
    {
        m_progressBar.setValue( i );
    }
    
    public void progress( int i, int I )
    {
        m_progressBar.setMaximum( I );
        m_progressBar.setValue( i );
    }
    
    public void progress( int i, int I, String text )
    {
        progress( i, I );
        if ( text != null ) m_label.setText( text );
    }
}
