package at.srfg.graphium.gipimport.model.impl;

import at.srfg.graphium.gipimport.model.IGipLinkToReferenceObject;

import java.util.Objects;

public class GipLinkToReferenceObjectImpl implements IGipLinkToReferenceObject {
    private String referenceObjectId;
    private long referenceObjectShortId;
    private String linkId;
    private long linkShortId;

    @Override
    public String getReferenceObjectId() {
        return referenceObjectId;
    }

    @Override
    public void setReferenceObjectId(String referenceObjectId) {
        this.referenceObjectId = referenceObjectId;
    }

    @Override
    public long getReferenceObjectShortId() {
        return referenceObjectShortId;
    }

    @Override
    public void setReferenceObjectShortId(long referenceObjectShortId) {
        this.referenceObjectShortId = referenceObjectShortId;
    }

    @Override
    public String getLinkId() {
        return linkId;
    }

    @Override
    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    @Override
    public long getLinkShortId() {
        return linkShortId;
    }

    @Override
    public void setLinkShortId(long linkShortId) {
        this.linkShortId = linkShortId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GipLinkToReferenceObjectImpl that = (GipLinkToReferenceObjectImpl) o;
        return referenceObjectShortId == that.referenceObjectShortId && linkShortId == that.linkShortId && Objects.equals(referenceObjectId, that.referenceObjectId) && Objects.equals(linkId, that.linkId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenceObjectId, referenceObjectShortId, linkId, linkShortId);
    }
}
