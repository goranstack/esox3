package nu.esox.gui.bind;

import nu.esox.obs.EsoxObservableListIF;


public class EsoxTableModelProjector<T> extends EsoxProjector
{
    @SuppressWarnings("unchecked")
	public EsoxTableModelProjector( EsoxModelSocketIF modelSocket, ExtractAspectValue get, EsoxObservableListTableModel<T> tm )
    {
        super( modelSocket, get, null, ( Object value ) -> { tm.setData( (EsoxObservableListIF<T>) value ); }, null, null );
    }

}