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
    private long responseTime;
    private long queryTime;
    private boolean error;

    public MeasureData(long responseTime, long queryTime, boolean error) {
        this.responseTime = responseTime;
        this.queryTime = queryTime;
        this.error = error;
    }

    public MeasureData(long responseTime, boolean error) {
        this.responseTime = responseTime;
        this.error = error;
    }

    
    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public long getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(long queryTime) {
        this.queryTime = queryTime;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
    
}
