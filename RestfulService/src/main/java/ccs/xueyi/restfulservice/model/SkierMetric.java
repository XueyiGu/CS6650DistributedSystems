/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.restfulservice.model;

/**
 *
 * @author ceres
 */
public class SkierMetric {
    private String id;
    private String skiID;
    private String dayNum;
    private int totalVertical;
    private int liftNum;

    public SkierMetric(String id, String skiID, String dayNum, int totalVertical, int liftNum) {
        this.id = id;
        this.skiID = skiID;
        this.dayNum = dayNum;
        this.totalVertical = totalVertical;
        this.liftNum = liftNum;
    }

    public SkierMetric(String skiID, String dayNum, int totalVertical, int liftNum) {
        this.skiID = skiID;
        this.dayNum = dayNum;
        this.totalVertical = totalVertical;
        this.liftNum = liftNum;
    }

    public SkierMetric() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSkiID() {
        return skiID;
    }

    public void setSkiID(String skiID) {
        this.skiID = skiID;
    }

    public String getDayNum() {
        return dayNum;
    }

    public void setDayNum(String dayNum) {
        this.dayNum = dayNum;
    }

    public int getTotalVertical() {
        return totalVertical;
    }

    public void setTotalVertical(int totalVertical) {
        this.totalVertical = totalVertical;
    }

    public int getLiftNum() {
        return liftNum;
    }

    public void setLiftNum(int liftNum) {
        this.liftNum = liftNum;
    }

    @Override
    public String toString() {
        return "SkierMetric{" + "id=" + id + ", skiID=" + skiID + ", dayNum=" + 
                dayNum + ", totalVertical=" + totalVertical + 
                ", liftNum=" + liftNum + '}';
    }
    
    
}
