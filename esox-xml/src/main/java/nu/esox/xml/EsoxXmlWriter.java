package nu.esox.xml;


import java.io.*;
import java.util.*;


public class EsoxXmlWriter
{
	public interface Encoder
	{
		String getTag( Object model );
        boolean hasId( Object model );
        void writeAttributes( Object model, EsoxXmlWriter w );
        void writeSubmodels( Object model, EsoxXmlWriter w );
        
        static Encoder EMPTY =
        		new Encoder()
        		{
    				public String getTag( Object model ) { return null; }
    		        public boolean hasId( Object model ) { return false; }
    				public void writeAttributes( Object model, EsoxXmlWriter w ) {}
    				public void writeSubmodels( Object model, EsoxXmlWriter w ) {}   	
        		};
	}
	
    private final PrintStream m_stream;
    private final Encoder m_encoder;
    private Map<Object,Integer> m_ids = new IdentityHashMap<Object,Integer>();
    private String m_indent = "";
    private String m_pending = null;
    
    public EsoxXmlWriter( PrintStream s, String version, String encoding, boolean standAlone, Encoder e )
    {
        m_stream = s;
        m_encoder = ( e == null ) ? Encoder.EMPTY : e;

        if ( version == null ) version = "1.0";
        if ( encoding == null ) encoding = "ISO-8859-1";

        m_stream.print( "<?xml version=\"" );
        m_stream.print( version );
        m_stream.print( "\" encoding=\"" );
        m_stream.print( encoding );
        m_stream.print( "\"" );
        if ( standAlone ) m_stream.print( " standalone=\"yes\"" );
        m_stream.println( "?>" );
    }

    
    public PrintStream getPrintStream()
    {
        return m_stream;
    }
    
    public void write( Iterable<?> ws, int skipLines )
    {
        boolean first = true;
        
        for
            ( Object w : ws )
        {
            if ( ! first ) skip( skipLines );  
            write( w );
            first = false;         
            
        }
    }

    public void write( String str )
    {
        flush( true );
        m_stream.println( m_indent + str );
    }

    public void comment( String comment )
    {
        flush( true );
        m_stream.println( m_indent + "<!-- " + comment + " -->" );
    }

    public void skip( int lines )
    {
        flush( true );
        while ( lines-- > 0 ) m_stream.println();
    }

    private void writeString( String value )
    {
        flush( false );
        
        int I = value.length();

        for
            ( int i = 0; i < I; i++ )
        {
            char c = value.charAt( i );

            if      ( c == '"' )  m_stream.print( "&quot;" );
            else if ( c == '&' )  m_stream.print( "&amp;" );
            else if ( c == '<' )  m_stream.print( "&lt;" );
            else if ( c == '>' )  m_stream.print( "&gt;" );
            else if ( c == '\'' ) m_stream.print( "&apos;" );
            else                  m_stream.print( c );
        }
    }

    private void writeBytes( byte [] value )
    {
        flush( false );

        for
            ( int i = 0; i < value.length; i++ )
        {
            m_stream.print( String.format( "%02X", value[ i ] ) );
        }
    }

    public void write( String propertyName, String value )
    {
        if ( value == null ) return;
        m_stream.print( " " + propertyName + "=\"" );
        writeString( value );
        m_stream.print( "\"" );
    }

    public void write( String propertyName, byte [] value )
    {
        if ( value == null ) return;
        m_stream.print( " " + propertyName + "=\"" );
        writeBytes( value );
        m_stream.print( "\"" );
    }

    public void write( String propertyName, boolean value )
    {
        write( propertyName, Boolean.toString( value ) );
    }

    public void write( String propertyName, long value )
    {
        write( propertyName, Long.toString( value ) );
    }

    public void write( String propertyName, int value )
    {
        write( propertyName, Integer.toString( value ) );
    }

    public void write( String propertyName, float value )
    {
        write( propertyName, Float.toString( value ) );
    }

    public void write( String propertyName, double value )
    {
        write( propertyName, Double.toString( value ) );
    }






    public void write( Object w )
    {
        if ( w == null ) return;

        flush( true );
        
        String tag = m_encoder.getTag( w );

        Integer id = m_ids.get( w );

        if
            ( id != null )
        {
            m_stream.print( m_indent + "<" + tag );
            write( "idref", id.intValue() );
            m_stream.println( "/>" );
        } else {
            m_stream.print( m_indent + "<" + tag );

            if
                ( m_encoder.hasId( w ) )
            {
                id = m_ids.size();
                m_ids.put( w, id );
                write( "id", id.intValue() );
            }

            m_encoder.writeAttributes( w, this );
            m_pending = ">";

            String tmp = m_indent;
            m_indent += "  ";

            m_encoder.writeSubmodels( w, this );

            m_indent = tmp;

            if
                ( m_pending == null )
            {
                m_stream.println( m_indent + "</" + tag + ">" );
            } else {
                m_stream.println( "/>" );
                m_pending = null;
            }
        }
    }


    private void flush( boolean newline )
    {
        if
            ( m_pending != null )
        {
            m_stream.print( m_pending );
            if ( newline ) m_stream.println();
            m_pending = null;
        }
    }
}
