package at.srfg.graphium.gipimport.model.impl;

import at.srfg.graphium.gipimport.model.IGipLinkToTruck;

import java.util.Objects;

public class GipLinkToTruckImpl implements IGipLinkToTruck {
    private String objectId;
    private long shortId;
    private String nodeFromId;
    private String nodeToId;
    private long nodeFromShortId;
    private long nodeToShortId;
    private float maxWidth;

    @Override
    public String getObjectId() {
        return objectId;
    }

    @Override
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    @Override
    public long getShortId() {
        return shortId;
    }

    @Override
    public void setShortId(long shortId) {
        this.shortId = shortId;
    }

    @Override
    public String getNodeFromId() {
        return nodeFromId;
    }

    @Override
    public void setNodeFromId(String nodeFromId) {
        this.nodeFromId = nodeFromId;
    }

    @Override
    public String getNodeToId() {
        return nodeToId;
    }

    @Override
    public void setNodeToId(String nodeToId) {
        this.nodeToId = nodeToId;
    }

    @Override
    public long getNodeFromShortId() {
        return nodeFromShortId;
    }

    @Override
    public void setNodeFromShortId(long nodeFromShortId) {
        this.nodeFromShortId = nodeFromShortId;
    }

    @Override
    public long getNodeToShortId() {
        return nodeToShortId;
    }

    @Override
    public void setNodeToShortId(long nodeToShortId) {
        this.nodeToShortId = nodeToShortId;
    }

    @Override
    public float getMaxWidth() {
        return maxWidth;
    }

    @Override
    public void setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GipLinkToTruckImpl that = (GipLinkToTruckImpl) o;
        return shortId == that.shortId && nodeFromShortId == that.nodeFromShortId && nodeToShortId == that.nodeToShortId && Float.compare(maxWidth, that.maxWidth) == 0 && Objects.equals(objectId, that.objectId) && Objects.equals(nodeFromId, that.nodeFromId) && Objects.equals(nodeToId, that.nodeToId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectId, shortId, nodeFromId, nodeToId, nodeFromShortId, nodeToShortId, maxWidth);
    }
}
