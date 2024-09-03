package at.srfg.graphium.gipimport.model.impl;

import at.srfg.graphium.gipimport.model.IGipLinkToWayNames;

import java.util.Objects;

public class GipLinkToWayNamesImpl implements IGipLinkToWayNames {
    private long linkShortId;
    private long wayShortId;
    private String linkObjectId;
    private String wayObjectId;

    @Override
    public long getLinkShortId() {
        return linkShortId;
    }

    @Override
    public void setLinkShortId(long linkShortId) {
        this.linkShortId = linkShortId;
    }

    @Override
    public long getWayShortId() {
        return wayShortId;
    }

    @Override
    public void setWayShortId(long wayShortId) {
        this.wayShortId = wayShortId;
    }

    @Override
    public String getLinkObjectId() {
        return linkObjectId;
    }

    @Override
    public void setLinkObjectId(String linkObjectId) {
        this.linkObjectId = linkObjectId;
    }

    @Override
    public String getWayObjectId() {
        return wayObjectId;
    }

    @Override
    public void setWayObjectId(String wayObjectId) {
        this.wayObjectId = wayObjectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GipLinkToWayNamesImpl that = (GipLinkToWayNamesImpl) o;
        return linkShortId == that.linkShortId && wayShortId == that.wayShortId && Objects.equals(linkObjectId, that.linkObjectId) && Objects.equals(wayObjectId, that.wayObjectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(linkShortId, wayShortId, linkObjectId, wayObjectId);
    }
}
