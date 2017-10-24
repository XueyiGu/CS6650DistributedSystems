/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.restfulservice.cache;

import ccs.xueyi.restfulservice.model.SkierMetric;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Singleton;

/**
 *
 * @author ceres
 */
@Singleton
public class SkierMetricCache {
    private static SkierMetricCache instance = null;
    private List<SkierMetric> cache = null;
    
    public SkierMetricCache(){
        cache = new ArrayList<>();
    }
    
    public SkierMetricCache getInstance(){
        if(instance == null){
            instance = new SkierMetricCache();
        }
        return instance;
    }
    
    public synchronized void addToCache(SkierMetric data){
        cache.add(data);
    }
    
    public synchronized List<SkierMetric> getCache(){
        List<SkierMetric> data = new ArrayList<>(cache);
        return data;
    }
    
    public int getCacheSize(){
        return cache.size();
    }
}
