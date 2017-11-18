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
    private List<MeasureData> cache = null;
    private int BATCH_SIZE = 1000;
    
    public MeasureCache(){
        cache = Collections.synchronizedList(new ArrayList<MeasureData>());
    }
    
    public static MeasureCache getInstance(){
        if(instance == null){
            instance = new MeasureCache();
        }
        return instance;
    }
    
    public synchronized void addToCache(MeasureData data){
        cache.add(data);
        if(cache.size() >= BATCH_SIZE){
            List<MeasureData> dataList = new ArrayList<>(cache);
            MeasureCacheExecutor executor = new MeasureCacheExecutor(dataList);
            executor.start();
            cache.clear();
        }
    }
    
    public synchronized List<MeasureData> getCache(){
        List<MeasureData> data = new ArrayList<>(cache);
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
