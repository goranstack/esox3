package nu.esox.gui.bind;

import nu.esox.obs.*;


public interface EsoxModelSocketIF extends EsoxObservableModelIF
{
    String PROPERTY_MODEL = "PROPERTY_MODEL";
 /*   
    public interface ObserverIF
    {
        void modelChanged( EsoxObservableModelIF oldModel, EsoxObservableModelIF newModel );
    }
   */ 

    EsoxObservableModelIF getModel();
    void attach( EsoxObservableModelIF model );
    boolean isOccupied();
    EsoxPredicateIF getIsOccupied();
}
