/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.client;

import ccs.xueyi.model.RFIDLiftData;
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
public class GetDataThread extends Thread{
    
    private int requestCount = 0;
    private int successCount = 0;
    private String url;
    private List<Long> latencies = new ArrayList<>();
    private CyclicBarrier barrier;
    private ConcurrentLinkedQueue<RFIDLiftData> queue;
    
    public GetDataThread(String url, CyclicBarrier barrier, ConcurrentLinkedQueue<RFIDLiftData> queue){
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
            doGet(myClient);
            long latency = System.currentTimeMillis() - startTime;
            latencies.add(latency);
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException ex) {
            Logger.getLogger(GetDataThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void doGet(RestClient client){
        String get = client.getData();
        requestCount++;
        
        if("Got it!".equals(get)){
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
