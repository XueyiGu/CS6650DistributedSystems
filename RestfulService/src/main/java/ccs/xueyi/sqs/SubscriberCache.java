/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.sqs;

import ccs.xueyi.restfulservice.cache.*;
import ccs.xueyi.restfulservice.model.MeasureData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author ceres
 */
public class SubscriberCache {
    private static SubscriberCache instance = null;
    private List<String> cache = null;
    private int BATCH_SIZE = 1000;
    
    public SubscriberCache(){
        cache = Collections.synchronizedList(new ArrayList<String>());
    }
    
    public static SubscriberCache getInstance(){
        if(instance == null){
            instance = new SubscriberCache();
        }
        return instance;
    }
    
    public synchronized void addToCache(String msg){
        cache.add(msg);
        if(cache.size() >= BATCH_SIZE){
            List<String> dataList = new ArrayList<>(cache);
            MeasureCacheExecutor executor = new MeasureCacheExecutor(dataList);
            executor.start();
            cache.clear();
        }
    }
    
    public synchronized List<String> getCache(){
        List<String> data = new ArrayList<>(cache);
        cache = new ArrayList<>();
        return data;
    }
    
    public synchronized void clearCache(){
        cache.clear();
    }
    
    public int getCacheSize(){
        return cache.size();
    }
}
