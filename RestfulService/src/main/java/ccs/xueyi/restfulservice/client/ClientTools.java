/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.restfulservice.client;


import ccs.xueyi.restfulservice.beans.RFIDLiftData;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ceres
 */
public class ClientTools {
    private String FILE_NAME;
    
    private String url;
    private int threadNum = 10;
    private int iterationNum = 100;
    private int requestCount = 0;
    private int successCount = 0;
    private List<GetDataThread> threads = new ArrayList<>();
    
    private long startTime = 0;
    private long finishTime = 0;
    
    private double meanLatency = 0;
    private double medianLatency = 0;
    private long percentile95th = 0;
    private long percentile99th = 0;
    
    static CyclicBarrier barrier; 
    private ConcurrentLinkedQueue<RFIDLiftData> queue = new ConcurrentLinkedQueue<>();
    
    class BarrierRunnable implements Runnable{

        @Override
        public void run() {
            
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
            System.out.println("Total wall time: " + (finishTime - startTime) + "ms");
        }
        
    }

    public ClientTools(String url, int threadNum, int iterationNum) {
        this.url = url;
        this.threadNum = threadNum;
        this.iterationNum = iterationNum;
    }
    
    public void startThread() throws TimeoutException{
        barrier = new CyclicBarrier(threadNum, new BarrierRunnable());
        startTime = System.currentTimeMillis();
        System.out.println("Client starting, Started time:" + convertTime(startTime));
        System.out.println("All threads running...");
        
        for(int i = 0; i < threadNum; i++){
            GetDataThread t = new GetDataThread(url, barrier, queue);
            threads.add(t);
            t.start();
        }
    }
    public void fileReader(ConcurrentLinkedQueue queue) throws IOException{
        if(FILE_NAME == null){
            return;
        }
        try {
            BufferedReader bReader = new BufferedReader(new FileReader(FILE_NAME));
            String nextLine = bReader.readLine();
            while(nextLine != null){
                String[] items = nextLine.split(",");
                String resortID = items[0];
                String dayNum = items[1];
                String timestamp = items[2];
                String skierID = items[3];
                String liftID = items[4];
                RFIDLiftData data = new RFIDLiftData(resortID, dayNum, timestamp,
                                                    skierID, liftID);
                queue.offer(data);
            }
            bReader.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ClientTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public void setFILE_NAME(String FILE_NAME) {
        this.FILE_NAME = FILE_NAME;
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
        for(GetDataThread t : threads){
            successCount += t.getSuccessCount();
            requestCount += t.getRequestCount();
        }
    }
    
    private void getMetrics(){
        
        long latencySum = 0;
        long[] latencyArray = new long[requestCount];
        int count = 0;
        for(GetDataThread t : threads){
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
