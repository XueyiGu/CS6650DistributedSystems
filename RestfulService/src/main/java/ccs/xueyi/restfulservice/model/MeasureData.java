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
public class MeasureData {
    private int responseTime;
    private int queryTime;
    private int error;

    public MeasureData(int responseTime, int queryTime, int error) {
        this.responseTime = responseTime;
        this.queryTime = queryTime;
        this.error = error;
    }

    public MeasureData(int responseTime, int error) {
        this.responseTime = responseTime;
        this.error = error;
    }

    
    public int getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }

    public int getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(int queryTime) {
        this.queryTime = queryTime;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }
    
}
