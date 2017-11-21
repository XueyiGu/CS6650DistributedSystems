/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.restfulservice;

import ccs.xueyi.restfulservice.DAO.AwsDAO;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ceres
 */
public class SimpleQueueService {
    
    private static SimpleQueueService instance = null;
    private static final String QUEUE_NAME = "MeasureQueue";
    public static AmazonSQS sqs = null;
    private String queueUrl = null;
    public static AtomicInteger id = new AtomicInteger();
    private Map<String, String> tokens = new HashMap<>();
    public SimpleQueueService(){
        if(sqs == null){
            try {
                tokens = AwsDAO.getInstance().getAwsToken();
            } catch (SQLException ex) {
                Logger.getLogger(SimpleQueueService.class.getName()).log(Level.SEVERE, null, ex);
            }
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(
                tokens.get("access_key_id"), tokens.get("secret_access_key"));
            sqs = AmazonSQSClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                    .withRegion(Regions.US_WEST_2)
                    .build();
            try {
                CreateQueueRequest createRequest = new CreateQueueRequest(QUEUE_NAME);
                queueUrl = sqs.createQueue(createRequest).getQueueUrl();
            } catch (AmazonSQSException e) {
                if (!e.getErrorCode().equals("QueueAlreadyExists")) {
                    throw e;
                }
            }
        }
    }
    
    public static SimpleQueueService getInstance(){
        if(instance == null){
            instance = new SimpleQueueService();
        }
        return instance;
    }
    
    public String getQueueUrl(){
        return queueUrl;
    }
    /**
     * send multi requests 
     * max number is 10!!!
     * @param msgs 
     */
    public void sendBathRequest(List<String> msgs){
        try {
            List<SendMessageBatchRequestEntry> entries = new ArrayList<>();
            for (String msg : msgs) {
                SendMessageBatchRequestEntry entry = 
                        new SendMessageBatchRequestEntry(String.valueOf(id.get()), msg);
                entries.add(entry);
                id.getAndIncrement();
            }
            SendMessageBatchRequest smbr = new SendMessageBatchRequest(queueUrl, entries);
            sqs.sendMessageBatch(smbr);
        } catch (AmazonServiceException ase) {
            System.out.println(ase.getErrorMessage());
        } catch (AmazonClientException ace) {
            System.out.println(ace.getMessage());
        }
    }
    
    /**
     * 
     * @return 
     */
    public List<Message> receiveMessages() {
        System.out.println("Receiving messages from queue.");
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
        receiveMessageRequest.setMaxNumberOfMessages(10);
        receiveMessageRequest.setWaitTimeSeconds(20);
        return sqs.receiveMessage(receiveMessageRequest).getMessages();
    }
    
    
    /**
     * 
     * @param queueUrl
     * @param messages 
     */
    public static void deleteSqsMessage(String queueUrl, List<Message> messages) {
        System.out.println("Deleting batch message.\n");
        List<DeleteMessageBatchRequestEntry> msgs = new ArrayList<>();
        for (Message message : messages) {
            DeleteMessageBatchRequestEntry dmbre = 
                    new DeleteMessageBatchRequestEntry(
                        message.getMessageId(), 
                        message.getReceiptHandle());
            msgs.add(dmbre);
        }
        sqs.deleteMessageBatch(queueUrl, msgs);
    }

    //for test
    public static void main(String[] args){
        try {
            Map<String, String> map = AwsDAO.getInstance().getAwsToken();
            System.out.println(map.get("access_key_id"));
        } catch (SQLException ex) {
            Logger.getLogger(SimpleQueueService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
