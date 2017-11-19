/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.restfulservice.cache;

import ccs.xueyi.restfulservice.model.MeasureData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author ceres
 */
public class MeasureCache {
    private static MeasureCache instance = null;
    private List<String> cache = null;
    private int BATCH_SIZE = 10 * 1000;
    private int MESSAGE_SIZE = 1000;
    
    public MeasureCache(){
        cache = Collections.synchronizedList(new ArrayList<String>());
    }
    
    public static MeasureCache getInstance(){
        if(instance == null){
            instance = new MeasureCache();
        }
        return instance;
    }
    
    public synchronized void addToCache(String msg){
        cache.add(msg);
        if(cache.size() >= BATCH_SIZE){
            List<String> dataList = new ArrayList<>(cache);
            List<String> msgs = dataToMessage(dataList);
            System.out.println("msfs size is " + msgs.size());
            MeasureCacheExecutor executor = new MeasureCacheExecutor(msgs);
            executor.start();
            cache.clear();
        }
    }
    
    public List<String> dataToMessage(List<String> dataList){
        List<String> msgs = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < dataList.size(); i++){
            sb.append(dataList.get(i));
            sb.append(" ");
            if(i > 0 && (i % MESSAGE_SIZE) == 0){
                msgs.add(sb.toString());
                sb = new StringBuilder();
            }
        }
        msgs.add(sb.toString());
        return msgs;
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
