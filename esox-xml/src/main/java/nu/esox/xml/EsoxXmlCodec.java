package nu.esox.xml;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;


public class EsoxXmlCodec implements EsoxXmlReader.Decoder, EsoxXmlWriter.Encoder
{	
	public static abstract class Codec
	{
		public abstract String getTag();
		public boolean writeAttributes( Object model, EsoxXmlWriter w ) { return true; }
		public boolean writeSubmodels( Object model, EsoxXmlWriter w )  { return true; }
		
		public abstract Object create( Object superModel, Attributes as ) throws ParseException, IOException;
		public void done( Object model ) {}		
	}

		
	private Map<String,Codec> m_codecsByTag = new HashMap<String,Codec>();
	private Map<Class<?>,Codec> m_codecsByClass = new HashMap<Class<?>,Codec>();
	
	
	public void add( Codec d, Class<?> ... cs )
	{
		m_codecsByTag.put( d.getTag(),  d );
		for ( Class<?> c : cs ) m_codecsByClass.put( c,  d );
	}

	
	@Override
	public String getTag( Object model ) 
	{
		Codec d = m_codecsByClass.get( model.getClass() );
		if ( d != null ) return d.getTag();
		return null; // fixit: throw exception
	}
    
	@Override
	public boolean hasId( Object model ) 
	{
		return false;
	}	
	
	@Override
	public void writeAttributes( Object model, EsoxXmlWriter w ) 
	{	
		Codec d = m_codecsByClass.get( model.getClass() );
		if ( d != null ) d.writeAttributes( model, w );
	}

	@Override
	public void writeSubmodels( Object model, EsoxXmlWriter w ) 
	{
		Codec d = m_codecsByClass.get( model.getClass() );
		if ( d != null ) d.writeSubmodels( model, w );
	}
	
	
	@Override
	public Object create( Object superModel, String tag, Attributes as )  throws ParseException, IOException
	{
		Codec d = m_codecsByTag.get( tag );
		if ( d != null ) return d.create( superModel, as );
		return null;
	}

	@Override
	public void add( Object superModel, String tag, Object submodel )
	{
	}
	
	@Override
	public void done( Object model )
	{
		Codec d = m_codecsByClass.get( model.getClass() );
		if ( d != null ) d.done( model );
	}

}
