/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.restfulservice.client.read;


import ccs.xueyi.restfulservice.client.*;
import ccs.xueyi.restfulservice.model.RFIDLiftData;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ceres
 */
public class ReadTool {
    
    private String url;
    private int threadNum = 10;
    private int requestCount = 0;
    private int successCount = 0;
    private List<ReadDataThread> threads = new ArrayList<>();
    
    private long startTime = 0;
    private long finishTime = 0;
    
    private double meanLatency = 0;
    private double medianLatency = 0;
    private long percentile95th = 0;
    private long percentile99th = 0;
    private long[] latencyArray;
    
    static CyclicBarrier barrier; 
    private List<RFIDLiftData> dataList = new ArrayList<>();
    
    class BarrierRunnable implements Runnable{

        @Override
        public void run() {
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ReadTool.class.getName()).log(Level.SEVERE, null, ex);
            }
            //send the end signal
            RestClient myClient = new RestClient(url);
            myClient.getData("-1", "-1");
            
            getCounts();
            System.out.println("Totoal number of request send: " + requestCount);
            System.out.println("Total number of successfull responses: " + successCount);
            getMetrics();
            System.out.println("The mean latency is: " + meanLatency + "ms");
            System.out.println("The median latency is: " + medianLatency + "ms");
            System.out.println("The 95th perentile of latency is: " + percentile95th + "ms");
            System.out.println("The 99th perentile of latency is: " + percentile99th + "ms");
            
            finishTime = System.currentTimeMillis();
            System.out.println("All threads completed. Finish time: " + convertTime(finishTime));
            long wallTime = finishTime - startTime;
            System.out.println("Total wall time: " + wallTime + "ms");
            System.out.println("Throughput: " + (successCount / (wallTime / 1000)));
            
            ChartGenerator chartGenerator = new ChartGenerator();
            try {
                chartGenerator.getChart(latencyArray, "Throughput - Read", 
                        "Ranking", "Latancy");
            } catch (IOException ex) {
                Logger.getLogger(ReadTool.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }

    public ReadTool(String url, int threadNum) {
        this.url = url;
        this.threadNum = threadNum;
    }
    
    public void startThread() throws TimeoutException{
        barrier = new CyclicBarrier(threadNum, new BarrierRunnable());
        int partitionSize = dataList.size() / threadNum;
        startTime = System.currentTimeMillis();
        System.out.println("Client starting, Started time:" + convertTime(startTime));
        System.out.println("All threads running...");
        
        for(int i = 0; i < threadNum; i++){
            int start = partitionSize * i;
            int end = Math.min(partitionSize * (i + 1), dataList.size()) - 1;
            //System.out.println("Start is "+ start + " and end is " + end);
            ReadDataThread t = new ReadDataThread(url, barrier, dataList, start, end);
            threads.add(t);
            t.start();
        }
    }
    
    //load skier entry for get
    public void loadEntry(){
        for(int i = 1; i <= 4000; i++){
            RFIDLiftData data = new RFIDLiftData("5", i + "");
            dataList.add(data);
        }
    }
    
     public void calculateAndPrint(){
        getCounts();
        System.out.println("Totoal number of request send: " + requestCount);
        System.out.println("Total number of successfull responses: " + successCount);
        getMetrics();
        System.out.println("The mean latency is: " + meanLatency + "ms");
        System.out.println("The median latency is: " + medianLatency + "ms");
        System.out.println("The 95th perentile of latency is: " + percentile95th + "ms");
        System.out.println("The 99th perentile of latency is: " + percentile99th + "ms");
    }
    
    private void getCounts(){
        for(ReadDataThread t : threads){
            successCount += t.getSuccessCount();
            requestCount += t.getRequestCount();
        }
    }
    
    private void getMetrics(){
        
        long latencySum = 0;
        latencyArray = new long[requestCount];
        int count = 0;
        for(ReadDataThread t : threads){
            for(long l : t.getLatency()){
                latencyArray[count++] = l;
                latencySum += l;
            }
        }
        Arrays.sort(latencyArray);
        //mean
        meanLatency = latencySum / requestCount;
        //median
        if(successCount % 2 == 1){
            medianLatency = latencyArray[requestCount / 2];
        }else{
            medianLatency = (latencyArray[requestCount / 2] + latencyArray[requestCount / 2 - 1])/ 2 * 1.0;
        }
        //95th
        percentile95th = latencyArray[(int)(requestCount * 0.95 - 1)];
        //99th
        percentile99th = latencyArray[(int)(requestCount * 0.99 - 1)];
    }
    
    
    public static String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        return format.format(date);
    }
    
}
