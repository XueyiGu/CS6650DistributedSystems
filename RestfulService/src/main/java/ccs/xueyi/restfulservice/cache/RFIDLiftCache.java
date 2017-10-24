/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.restfulservice.cache;

import ccs.xueyi.restfulservice.model.RFIDLiftData;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Singleton;

/**
 *
 * @author ceres
 */
@Singleton
public class RFIDLiftCache {
    private static RFIDLiftCache instance = null;
    private List<RFIDLiftData> cache = null;
    
    public RFIDLiftCache(){
        cache = new ArrayList<>(); 
    }
    public static RFIDLiftCache getInstance(){
        if(instance == null){
            instance = new RFIDLiftCache();
        }
        return instance;
    }
    
    public synchronized void addToCache(RFIDLiftData data){
        cache.add(data);
    }
    
    public synchronized List<RFIDLiftData> getCache(){
        List<RFIDLiftData> data = new ArrayList<>(cache);
        cache = new ArrayList<>();
        return data;
    }
    
    public int getCacheSize(){
        return cache.size();
    }
}
