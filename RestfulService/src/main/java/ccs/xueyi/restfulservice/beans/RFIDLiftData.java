/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.restfulservice.beans;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ceres
 */
//@XmlRootElement(name = "rfid")
public class RFIDLiftData implements Serializable{
    private String id;
    private String resortID;
    private String dayNum;
    private String timestamp;
    private String skierID;
    private String liftID;

    public RFIDLiftData(String id, String resortID, String dayNum, String timestamp, String skierID, String liftID) {
        this.id = id;
        this.resortID = resortID;
        this.dayNum = dayNum;
        this.timestamp = timestamp;
        this.skierID = skierID;
        this.liftID = liftID;
    }

    
    public RFIDLiftData(String resortID, String dayNum, String timestamp, String skierID, String liftID) {
        this.resortID = resortID;
        this.dayNum = dayNum;
        this.timestamp = timestamp;
        this.skierID = skierID;
        this.liftID = liftID;
    }
    public RFIDLiftData(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    
    public String getResortID() {
        return resortID;
    }
    
   //@XmlElement
    public void setResortID(String resortID) {
        this.resortID = resortID;
    }

    public String getDayNum() {
        return dayNum;
    }

    //@XmlElement
    public void setDayNum(String dayNum) {
        this.dayNum = dayNum;
    }

    public String getTimestamp() {
        return timestamp;
    }

    //@XmlElement
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSkierID() {
        return skierID;
    }

    //@XmlElement
    public void setSkierID(String skierID) {
        this.skierID = skierID;
    }

    public String getLiftID() {
        return liftID;
    }

    //@XmlElement
    public void setLiftID(String liftID) {
        this.liftID = liftID;
    }
    
    
}
