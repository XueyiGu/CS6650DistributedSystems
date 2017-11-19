/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.sqs;

import ccs.xueyi.restfulservice.DAO.MeasureDAO;
import ccs.xueyi.restfulservice.SimpleQueueService;
import ccs.xueyi.restfulservice.model.MeasureData;
import com.amazonaws.services.sqs.model.Message;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ceres
 */
public class Subscriber{
    private static final int SYNC_UP_SCHEDULE = 5 * 1000;
    private static final int SYNC_UP_DB_SCHEDULE = 10 * 1000;
    private static Map<String, Message> receivedMap;
    private static String queueUrl;
    private static long start = System.currentTimeMillis();
    private static SimpleQueueService sqs;
    private static SubscriberCache cache;
    private static MeasureDAO measureDAO;
//    private static final ScheduledExecutorService subscriber =
//            Executors.newSingleThreadScheduledExecutor();
    
    public static void main(String[] args) {
        cache = SubscriberCache.getInstance();
        measureDAO = MeasureDAO.getInstance();
        sqs = SimpleQueueService.getInstance();
        queueUrl = sqs.getQueueUrl();
        receivedMap = new HashMap<>();
        
        try {
            //create table first
            measureDAO.createTable();
        } catch (SQLException ex) {
            Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ScheduledExecutorService pullExecutor =
            Executors.newSingleThreadScheduledExecutor();
        pullExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run(){
                retrievalMessage();
            }
        }, 0, SYNC_UP_SCHEDULE, TimeUnit.MILLISECONDS);
        
        ScheduledExecutorService dbExecutor =
            Executors.newSingleThreadScheduledExecutor();
        dbExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run(){
                moveDataToDB();
            }
        }, 0, SYNC_UP_DB_SCHEDULE, TimeUnit.MILLISECONDS);
    }
    
    /**
     * retrieval messages from SQS
     */
    private static void retrievalMessage() {
        try {
            List<Message> receiveMsgs = sqs.receiveMessages();
            System.out.println("****************Display messages****************");
            sqs.displayMessage(receiveMsgs);
            
            List<Message> deleteMsgs = new ArrayList<>();
            String messageId;
            if (receiveMsgs.isEmpty()) {
                return;
            }
            for (Message message : receiveMsgs) {
                messageId = message.getMessageId();
                // Get new message
                if (!receivedMap.keySet().contains(messageId)) {
                    receivedMap.put(messageId, message);
                    deleteMsgs.add(message);
                    cache.addToCache(message.getBody());
                    System.out.println("received: " + receivedMap.values().size());
                }
            }
            
            SimpleQueueService.deleteSqsMessage(queueUrl, deleteMsgs);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * 
     */
    private static void moveDataToDB(){
        List<MeasureData> dataList = messageToData(cache.getCache());
        try {
            measureDAO.bathInsertData(dataList);
        } catch (SQLException ex) {
            Logger.getLogger(Subscriber.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static List<MeasureData> messageToData(List<String> msgs){
        List<MeasureData> dataList = new ArrayList<>();
        for(String s : msgs){
            String[] items = s.trim().split(" ");
            for(String item : items){
                String[] its = item.split(",");
                MeasureData data = new MeasureData(Integer.parseInt(its[0]), 
                        Integer.parseInt(its[1]));
                dataList.add(data);
            }
        }
        return dataList;
    }
}
