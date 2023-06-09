package nu.esox.xml;


import java.io.*;
import java.text.*;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;


public class EsoxXmlReader extends DefaultHandler
{
	public interface Decoder
	{
		Object create( Object superModel, String tag, Attributes attrs ) throws Exception;
		void add( Object superModel, String tag, Object submodel ) throws Exception;
		void done( Object model ) throws Exception;
		
		static Decoder EMPTY =
				new Decoder()
				{
					public Object create( Object superModel, String tag, Attributes attrs ) { return null; }
					public void add( Object superModel, String tag, Object submodel ) {}
					public void done( Object model ) {}
				};
	}

    
    public static boolean TRACE = !true;
    public static boolean TRACE_2 = !true;
    public static boolean TRACE_3 = !true; // text
    private String m_traceIndent = "";

    private final Decoder m_decoder;
    private final Map<String,Object> m_ids = new HashMap<String,Object>();
    private final List<Object> m_modelStack = new ArrayList<Object>();
    private Object m_root = null;
    private Object m_superModel = null;
    
    
  

    public EsoxXmlReader( InputStream s, Object superModel, Decoder f ) throws SAXException, ParserConfigurationException, IOException
    {
    	m_decoder = ( f == null ) ? Decoder.EMPTY : f;   	
        m_superModel = superModel;
        SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
        sp.parse( s, this );
    }


    public Object getRoot()
    {
        return m_root;
    }


      // DocumentHandler

    public void startDocument() throws SAXException
    {
        if ( TRACE ) System.err.println( "start" );
    }

    public void endDocument() throws SAXException
    {
        if ( TRACE ) System.err.println( "end" );
    }
    
    public void startElement( String namespaceURI, String localName, String qualifiedName, Attributes attrs ) throws SAXException
    {
        if
            ( TRACE )
        {
            System.err.print( m_traceIndent + "<" + qualifiedName );
            m_traceIndent += "  ";
            int I = attrs.getLength();
            for ( int i = 0; i <I; i++ )
            {
                System.err.print( " " + attrs.getQName( i ) + "=" + attrs.getValue( i ) );
            }
            System.err.println( ">" );
        }

        {  // is it a ref ?
            String idref = attrs.getValue( "idref" );
            if
                ( idref != null )
            {
                Object model = m_ids.get( idref );

                if
                    ( model != null )
                {
            		addSubmodel( qualifiedName, model );
                    m_modelStack.add( model );
                    m_superModel = model;
                    return;
                } else {
                    throw new SAXException( "Can't resolve reference: " + idref );
                }
            }
        }

        try
        {
        	Object model = m_decoder.create( m_superModel, qualifiedName, attrs );
	        if
	            ( model == null )
	        {
	              // exception ?
	            System.err.println( "Can't create model" );
	            System.err.println( "     tag: " + qualifiedName );
	            System.err.println( "   super: " + m_superModel + " " + m_superModel.getClass() );
	            m_modelStack.add( null );
	            throw new SAXException( "Can't create model for " + qualifiedName );
	        } 
	        
	        if ( m_root == null ) m_root = model;
	            
	        addSubmodel( qualifiedName, model );
	        m_modelStack.add( model );
	        m_superModel = model;
	
	        String id = attrs.getValue( "id" );
	        if ( id != null ) m_ids.put( id, model );
		}
        catch ( Exception ex )
        {
            throw new SAXException( "Can't create model for " + qualifiedName );
        }

    }

    public void endElement( String namespaceURI, String simpleName, String qualifiedName ) throws SAXException
    {
        if
            ( TRACE )
        {
            m_traceIndent = m_traceIndent.substring( 2 );
            System.err.println( m_traceIndent + "</" + qualifiedName + ">" );
        }

        try
        {
        	m_decoder.done( m_superModel );
		}
        catch ( Exception ex )
        {
            throw new SAXException( ex );
        }
        
        if
            ( ! m_modelStack.isEmpty() )
        {
            m_modelStack.remove( m_modelStack.size() - 1 );
        }

        if
            ( m_modelStack.isEmpty() )
        {
            m_superModel = null;
        } else {
            if
                ( m_modelStack.get( m_modelStack.size() - 1 ) != null )
            {
                m_superModel = m_modelStack.get( m_modelStack.size() - 1 );
            }
        }
    }
    
    /*
    public void characters( char buf[], int offset, int len ) throws SAXException
    {
        addText( new String( buf, offset, len ) );
    }







    private Method getAddTextMethod( Class<?> c )
    {
        Method m = null;
        
        if
            ( m_addTextMethods.containsKey( c ) )
        {
            m = (Method) m_addTextMethods.get( c );
        } else {
            while
                ( m == null && c != null )
            {
                try
                {
                    m = c.getDeclaredMethod( "xmlAddText", String.class );
                }
                catch ( NoSuchMethodException ex )
                {
                }

                c = c.getSuperclass();
            }
            
            m_addTextMethods.put( m_superModel.getClass(), m );
        }

        return m;
    }
    
   


    private Map<Class<?>,Method> m_addTextMethods = new HashMap<Class<?>,Method>();

    
    private void addText( String text ) throws SAXException
    {
        if ( TRACE_3 ) System.err.println( m_traceIndent + "## addText " + text + " to " + m_superModel );
        if ( m_superModel == null ) return;
        
        Method m = getAddTextMethod( m_superModel.getClass() );
            
        if ( TRACE_3 ) System.err.println(  m_traceIndent + "## " + m );
        if
            ( m != null )
        {
            boolean tmp = false;
            try
            {
                Boolean accepted = null;

                tmp = m.canAccess( m_superModel );
                m.setAccessible( true );

                if
                    ( m.getReturnType().equals( boolean.class ) || m.getReturnType().equals( Boolean.class ) )
                {
                    accepted = (Boolean) m.invoke( m_superModel, text );
                } else {
                    m.invoke( m_superModel, text );
                }
                
                if
                    ( ( accepted != null ) && ! ( (Boolean) accepted ).booleanValue() )
                {
                    throw new SAXException( "Text [" + text + "] not accepted by [" + m_superModel + "]" );
                }
            }
            catch ( IllegalAccessException ex )
            {
                throw new SAXException( ex );
            }
            catch ( InvocationTargetException ex )
            {
                if ( TRACE_3 ) System.err.println(  m_traceIndent + "## InvocationTargetException" );
                if ( TRACE_3 ) System.err.println(  m_traceIndent + "## " + m_superModel.getClass() );
                if ( TRACE_3 ) System.err.println(  m_traceIndent + "## " + m_superModel );
                if ( TRACE_3 ) System.err.println(  m_traceIndent + "## " + m );
                if ( TRACE_3 ) System.err.println(  m_traceIndent + "## " + text );
                if ( TRACE_3 ) System.err.println(  m_traceIndent + "## " + ex.getTargetException() );
                throw new SAXException( m_superModel.getClass() + "." + m, ex );
            }
            finally
            {
                m.setAccessible( tmp );
            }
        }
    }
*/





 
    private void addSubmodel( String tag, Object model ) throws SAXException
    {
        if ( TRACE_2 ) System.err.println( m_traceIndent + "## addSubmodel " + model + " to " + m_superModel );
        if ( m_superModel == null ) return;
        
        try
        {
        	m_decoder.add( m_superModel, tag, model );
	    } 
		catch ( Exception ex ) 
		{
            throw new SAXException( ex );
		}
    }























    public static byte [] xml2Bytes( Attributes as, String tag )
    {
        return xml2Bytes( as, tag, null );
    }

    public static byte [] xml2Bytes( Attributes as, String tag, byte [] nullValue )
    {
        String tmp = as.getValue( tag );
        if ( tmp == null ) return nullValue;
        byte [] data = new byte [ tmp.length() / 2 ];
        for
            ( int i = 0; i < data.length; i++ )
        {
            data[ i ] = (byte) ( c2i( tmp.charAt( i * 2 ) ) * 16 + c2i( tmp.charAt( i * 2 + 1 ) ) );
        }
        return data;
    }

    private static byte c2i( char c )
    {
        if ( Character.isDigit( c ) ) return (byte) ( 0xFF & ( c - '0' ) );
        return (byte) ( 0xFF & ( 10 + ( c - 'A' ) ) );
    }





    public static String xml2String( Attributes as, String tag )
    {
        return xml2String( as, tag, null );
    }

    public static String xml2String( Attributes as, String tag, String nullValue )
    {
        String tmp = as.getValue( tag );
        return ( tmp == null ) ? nullValue : tmp;
    }

    public static Date xml2Date( Attributes as, String tag, Date nullValue, Date errorValue, DateFormat df )
    {
        String tmp = as.getValue( tag );
        if ( tmp == null ) return nullValue;
        try
        {
            return df.parse( tmp );
        }
        catch ( ParseException ex )
        {
            return errorValue;
        }

    }

    public static Date xml2Date( Attributes as, String tag, Date nullValue, DateFormat df ) throws ParseException
    {
        String tmp = as.getValue( tag );
        if ( tmp == null ) return nullValue;
        return df.parse( tmp );
    }


    public static short xml2Short( Attributes as, String tag, short nullValue, short errorValue )
    {
        String tmp = as.getValue( tag );
        if ( tmp == null ) return nullValue;
        try
        {
            return Short.parseShort( tmp );
        }
        catch ( NumberFormatException ex )
        {
            return errorValue;
        }

    }
    
    public static short xml2Short( Attributes as, String tag ) throws ParseException
    {
        String tmp = as.getValue( tag );
        if ( tmp == null ) throw new ParseException( tmp, 0 );
        try
        {
            return Short.parseShort( tmp );
        }
        catch ( NumberFormatException ex )
        {
            throw new ParseException( tmp, 0 );
        }
    }

    public static short xml2Short( Attributes as, String tag, short nullValue ) throws ParseException
    {
        String tmp = as.getValue( tag );
        if ( tmp == null ) return nullValue;
        try
        {
            return Short.parseShort( tmp );
        }
        catch ( NumberFormatException ex )
        {
            throw new ParseException( tmp, 0 );
        }
    }


    public static int xml2Integer( Attributes as, String tag, int nullValue, int errorValue )
    {
        String tmp = as.getValue( tag );
        if ( tmp == null ) return nullValue;
        try
        {
            return Integer.parseInt( tmp );
        }
        catch ( NumberFormatException ex )
        {
            return errorValue;
        }

    }
    
    public static int xml2Integer( Attributes as, String tag ) throws ParseException
    {
        String tmp = as.getValue( tag );
        if ( tmp == null ) throw new ParseException( tmp, 0 );
        try
        {
            return Integer.parseInt( tmp );
        }
        catch ( NumberFormatException ex )
        {
            throw new ParseException( tmp, 0 );
        }
    }

    public static int xml2Integer( Attributes as, String tag, int nullValue ) throws ParseException
    {
        String tmp = as.getValue( tag );
        if ( tmp == null ) return nullValue;
        try
        {
            return Integer.parseInt( tmp );
        }
        catch ( NumberFormatException ex )
        {
            throw new ParseException( tmp, 0 );
        }
    }

    
    public static byte xml2Byte( Attributes as, String tag ) throws ParseException
    {
        String tmp = as.getValue( tag );
        if ( tmp == null ) throw new ParseException( tmp, 0 );
        try
        {
            return Byte.parseByte( tmp );
        }
        catch ( NumberFormatException ex )
        {
            throw new ParseException( tmp, 0 );
        }
    }

    public static byte xml2Byte( Attributes as, String tag, byte nullValue ) throws ParseException
    {
        String tmp = as.getValue( tag );
        if ( tmp == null ) return nullValue;
        try
        {
            return Byte.parseByte( tmp );
        }
        catch ( NumberFormatException ex )
        {
            throw new ParseException( tmp, 0 );
        }
    }

    
    public static long xml2Long( Attributes as, String tag ) throws ParseException
    {
        String tmp = as.getValue( tag );
        if ( tmp == null ) throw new ParseException( tmp, 0 );
        try
        {
            return Long.parseLong( tmp );
        }
        catch ( NumberFormatException ex )
        {
            throw new ParseException( tmp, 0 );
        }
    }

    public static long xml2Long( Attributes as, String tag, long nullValue ) throws ParseException
    {
        String tmp = as.getValue( tag );
        if ( tmp == null ) return nullValue;
        try
        {
            return Long.parseLong( tmp );
        }
        catch ( NumberFormatException ex )
        {
            throw new ParseException( tmp, 0 );
        }
    }


    
    public static float xml2Float( Attributes as, String tag ) throws ParseException
    {
        String tmp = as.getValue( tag );
        if ( tmp == null ) throw new ParseException( tmp, 0 );
        try
        {
            return Float.parseFloat( tmp );
        }
        catch ( NumberFormatException ex )
        {
            throw new ParseException( tmp, 0 );
        }
    }

    public static float xml2Float( Attributes as, String tag, float nullValue ) throws ParseException
    {
        String tmp = as.getValue( tag );
        if ( tmp == null ) return nullValue;
        try
        {
            return Float.parseFloat( tmp );
        }
        catch ( NumberFormatException ex )
        {
            throw new ParseException( tmp, 0 );
        }
    }


    
    public static double xml2Double( Attributes as, String tag ) throws ParseException
    {
        String tmp = as.getValue( tag );
        if ( tmp == null ) throw new ParseException( tmp, 0 );
        try
        {
            return Double.parseDouble( tmp );
        }
        catch ( NumberFormatException ex )
        {
            throw new ParseException( tmp, 0 );
        }
    }

    public static double xml2Double( Attributes as, String tag, double nullValue ) throws ParseException
    {
        String tmp = as.getValue( tag );
        if ( tmp == null ) return nullValue;
        try
        {
            return Double.parseDouble( tmp );
        }
        catch ( NumberFormatException ex )
        {
            throw new ParseException( tmp, 0 );
        }
    }


    
    public static boolean xml2Boolean( Attributes as, String tag, boolean nullValue )
    {
        String tmp = as.getValue( tag );
        if ( tmp == null ) return nullValue;
        return Boolean.valueOf( tmp ).booleanValue();
    }

    public static boolean xml2Boolean( Attributes as, String tag ) throws ParseException
    {
        String tmp = as.getValue( tag );
        if ( tmp == null ) throw new ParseException( tmp, 0 );
        return Boolean.valueOf( tmp ).booleanValue();
    }
     
}
