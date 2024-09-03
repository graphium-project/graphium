package at.srfg.graphium.gipimport.model;

public interface IGipLinkToTruck {
    String getObjectId();

    void setObjectId(String objectId);

    long getShortId();

    void setShortId(long shortId);

    String getNodeFromId();

    void setNodeFromId(String nodeFromId);

    String getNodeToId();

    void setNodeToId(String nodeToId);

    long getNodeFromShortId();

    void setNodeFromShortId(long nodeFromShortId);

    long getNodeToShortId();

    void setNodeToShortId(long nodeToShortId);

    float getMaxWidth();

    void setMaxWidth(float maxWidth);
}
