/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.restfulservice.cache;

import ccs.xueyi.restfulservice.SimpleQueueService;
import java.util.List;

/**
 *
 * @author ceres
 */
public class MeasureCacheExecutor extends Thread{
    List<String> msgs;
    SimpleQueueService sqs = SimpleQueueService.getInstance();
    
    public MeasureCacheExecutor(List<String> msgs){
        this.msgs = msgs;
    }
    
    @Override
    public void run(){
        System.out.println("SQS cache size is " + msgs.size());
        sqs.sendBathRequest(msgs);
    }
}
