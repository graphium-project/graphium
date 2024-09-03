package at.srfg.graphium.gipimport.model;

public interface IGipLinkToWayNames {
    long getLinkShortId();

    void setLinkShortId(long linkShortId);

    /*
    long getWayShortId();

    void setWayShortId(long wayShortId);
    */

    long getWayShortId();

    void setWayShortId(long wayShortId);

    String getLinkObjectId();

    void setLinkObjectId(String linkObjectId);

    String getWayObjectId();

    void setWayObjectId(String wayObjectId);
}
