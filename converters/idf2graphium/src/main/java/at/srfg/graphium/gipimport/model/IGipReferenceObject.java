package at.srfg.graphium.gipimport.model;

public interface IGipReferenceObject {
    String getObjectId();

    void setObjectId(String objectId);

    long getShortId();

    void setShortId(long shortId);

    int getReferenceType();

    void setReferenceType(int referenceType);
}
