/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.restfulservice;

import ccs.xueyi.restfulservice.model.MeasureData;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
    private static final String access_key_id = "AKIAIE4ZUNWJ2K464ONQ";
    private static final String secret_access_key = "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";
    
    public SimpleQueueService(){
        if(sqs == null){
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(
                access_key_id, secret_access_key);
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
     * 
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
            System.out.println("Caught an AmazonServiceException");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered " +
                    "a serious internal problem while trying to communicate with SQS, such as not " +
                    "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
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
    
    public void displayMessage(List<Message> messages) {
        System.out.println("num: " + messages.size());
        for (Message message : messages) {
            System.out.println("  Message");
            System.out.println("    MessageId:     " + message.getMessageId());
            System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
            System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
            System.out.println("    Body:          " + message.getBody());
            for (Map.Entry<String, String> entry : message.getAttributes().entrySet()) {
                System.out.println("  Attribute");
                System.out.println("    Name:  " + entry.getKey());
                System.out.println("    Value: " + entry.getValue());
            }
        }
        System.out.println();
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

}
