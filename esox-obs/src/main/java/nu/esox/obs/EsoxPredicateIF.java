package nu.esox.obs;

public interface EsoxPredicateIF extends EsoxObservableModelIF
{
    String PROPERTY_IS_TRUE = "IS_TRUE";
    
    boolean isTrue();
}
