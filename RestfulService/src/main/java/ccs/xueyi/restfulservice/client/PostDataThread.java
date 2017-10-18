/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.restfulservice.client;


import ccs.xueyi.restfulservice.beans.RFIDLiftData;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ceres
 */
public class PostDataThread extends Thread{
     
    private int requestCount = 0;
    private int successCount = 0;
    private String url;
    private List<Long> latencies = new ArrayList<>();
    private CyclicBarrier barrier;
    private ConcurrentLinkedQueue<RFIDLiftData> queue;
    
    public PostDataThread(String url, CyclicBarrier barrier, ConcurrentLinkedQueue<RFIDLiftData> queue){
        this.url = url;
        this.barrier = barrier;
        this.queue = queue;
    }
    
    @Override
    public void run() {
        try {
            //To call the HTTP endpoints iterationNum times
            RestClient myClient = new RestClient(url);
            long startTime = System.currentTimeMillis();
            doPost(myClient);
            long latency = System.currentTimeMillis() - startTime;
            latencies.add(latency);
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException ex) {
            Logger.getLogger(GetDataThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void doPost(RestClient client){
        int length = 11;
        
        requestCount++;
        
        if(length == 11){
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
