package at.srfg.graphium.gipimport.model.impl;

import at.srfg.graphium.gipimport.model.IGipReferenceObject;

import java.util.Objects;

public class GipReferenceObjectImpl implements IGipReferenceObject {
    private String objectId;
    private long shortId;
    private int referenceType; //bridges = 5002; tunnel = 5023

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
    public int getReferenceType() {
        return referenceType;
    }

    @Override
    public void setReferenceType(int referenceType) {
        this.referenceType = referenceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GipReferenceObjectImpl that = (GipReferenceObjectImpl) o;
        return shortId == that.shortId && referenceType == that.referenceType && Objects.equals(objectId, that.objectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectId, shortId, referenceType);
    }
}
