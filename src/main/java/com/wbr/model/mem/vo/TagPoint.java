package com.wbr.model.mem.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 参数权重配置表
 * @author pxs
 */
@Data
public class TagPoint implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    private String equipmentNumber;

    private String equipmentCode;

    private String tagName;

    private String tagComment;

    private String orgTagName;

    private String tagUnit;

    private Double scaleFactor;

    private Double intercept;

    private Date createTime;

    private String creator;

    private Double maxValue;

    private Double minValue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getEquipmentNumber() {
        return equipmentNumber;
    }

    public void setEquipmentNumber(String equipmentNumber) {
        this.equipmentNumber = equipmentNumber == null ? null : equipmentNumber.trim();
    }

    public String getEquipmentCode() {
        return equipmentCode;
    }

    public void setEquipmentCode(String equipmentCode) {
        this.equipmentCode = equipmentCode == null ? null : equipmentCode.trim();
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName == null ? null : tagName.trim();
    }

    public String getTagComment() {
        return tagComment;
    }

    public void setTagComment(String tagComment) {
        this.tagComment = tagComment == null ? null : tagComment.trim();
    }

    public String getOrgTagName() {
        return orgTagName;
    }

    public void setOrgTagName(String orgTagName) {
        this.orgTagName = orgTagName == null ? null : orgTagName.trim();
    }

    public String getTagUnit() {
        return tagUnit;
    }

    public void setTagUnit(String tagUnit) {
        this.tagUnit = tagUnit == null ? null : tagUnit.trim();
    }

    public Double getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(Double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public Double getIntercept() {
        return intercept;
    }

    public void setIntercept(Double intercept) {
        this.intercept = intercept;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator == null ? null : creator.trim();
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }
}
