/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.restfulservice.client.read;


import ccs.xueyi.restfulservice.client.RestClient;
import ccs.xueyi.restfulservice.model.RFIDLiftData;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ceres
 */
public class ReadDataThread extends Thread{
    
    private int requestCount = 0;
    private int successCount = 0;
    private String url;
    private List<Long> latencies = new ArrayList<>();
    private CyclicBarrier barrier;
    private List<RFIDLiftData> dataList;
    private int start;
    private int end;
    
    public ReadDataThread(String url, CyclicBarrier barrier, 
            List<RFIDLiftData> dataList, int start, int end){
        this.url = url;
        this.barrier = barrier;
        this.dataList = dataList;
        this.start = start;
        this.end = end;
    }
    
    @Override
    public void run() {
        try {
            //To call the HTTP endpoints iterationNum times
            RestClient myClient = new RestClient(url);
            for(int i = start; i <= end; i++){
                long startTime = System.currentTimeMillis();
                doGet(myClient, dataList.get(i));
                long latency = System.currentTimeMillis() - startTime;
                latencies.add(latency);
            }
            
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException ex) {
            Logger.getLogger(ReadDataThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void doGet(RestClient client, RFIDLiftData data){
        int status = client.getData(data.getSkierID(), data.getDayNum());
        requestCount++;
        //System.out.println("get status is " + status);
        if(status == 200 || status == 204){
            successCount++;
        }
    }
    
    public int getSuccessCount(){
        return successCount;
    }
    
    public int getRequestCount(){
        return requestCount;
    }
    
    public List<Long> getLatency(){
        return latencies;
    }
}
