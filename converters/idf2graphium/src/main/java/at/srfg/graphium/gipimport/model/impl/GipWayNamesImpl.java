package at.srfg.graphium.gipimport.model.impl;

import at.srfg.graphium.gipimport.model.IGipWayNames;

import java.util.Objects;

public class GipWayNamesImpl implements IGipWayNames {
    private String objectId;
    private long shortId;
    private String nameText;
    private String nameCategoryLong;
    private String shortName;

    @Override
    public String getObjectId() {
        return objectId;
    }

    @Override
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    @Override
    public String getNameText() {
        return nameText;
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
    public void setNameText(String nameText) {
        this.nameText = nameText;
    }

    @Override
    public String getNameCategoryLong() {
        return nameCategoryLong;
    }

    @Override
    public void setNameCategoryLong(String nameCategoryLong) {
        this.nameCategoryLong = nameCategoryLong;
    }

    @Override
    public String getShortName() {
        return shortName;
    }

    @Override
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GipWayNamesImpl that = (GipWayNamesImpl) o;
        return shortId == that.shortId && Objects.equals(objectId, that.objectId) && Objects.equals(nameText, that.nameText) && Objects.equals(nameCategoryLong, that.nameCategoryLong) && Objects.equals(shortName, that.shortName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectId, shortId, nameText, nameCategoryLong, shortName);
    }
}
