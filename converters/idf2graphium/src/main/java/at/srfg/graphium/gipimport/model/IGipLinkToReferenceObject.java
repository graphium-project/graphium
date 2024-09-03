package at.srfg.graphium.gipimport.model;

public interface IGipLinkToReferenceObject {
    String getReferenceObjectId();

    void setReferenceObjectId(String referenceObjectId);

    long getReferenceObjectShortId();

    void setReferenceObjectShortId(long referenceObjectShortId);

    String getLinkId();

    void setLinkId(String linkId);

    long getLinkShortId();

    void setLinkShortId(long linkShortId);
}
