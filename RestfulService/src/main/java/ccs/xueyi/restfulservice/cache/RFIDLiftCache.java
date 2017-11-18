/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.restfulservice.cache;

import ccs.xueyi.restfulservice.DAO.RFIDLiftDAO;
import ccs.xueyi.restfulservice.DAO.SkierMetricDAO;
import ccs.xueyi.restfulservice.RestServer;
import ccs.xueyi.restfulservice.model.RFIDLiftData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import static java.util.Collections.list;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Singleton;

/**
 *
 * @author ceres
 */
@Singleton
public class RFIDLiftCache {
    private static RFIDLiftCache instance = null;
    private List<RFIDLiftData> cache = null;
    private int BATCH_SIZE = 1000;
    private final RFIDLiftDAO rfidLifDAO = RFIDLiftDAO.getInstance();
    private final SkierMetricDAO sMetricDAO = SkierMetricDAO.getInstance();
    
    public RFIDLiftCache(){
        cache = Collections.synchronizedList(new ArrayList<RFIDLiftData>());
    }
    public static RFIDLiftCache getInstance(){
        if(instance == null){
            instance = new RFIDLiftCache();
        }
        return instance;
    }
    
    public synchronized void addToCache(RFIDLiftData data){
        cache.add(data);
        if(cache.size() >= BATCH_SIZE){
            List<RFIDLiftData> dataList = new ArrayList<>(cache);
            RDFIDCacheExecutor executor = new RDFIDCacheExecutor(dataList);
            executor.start();
            cache.clear();
        }
    }
    
    public void moveDataToDB(){
        try {
                System.out.println("cache size is " + cache.size());
                rfidLifDAO.bathInsertData(cache);
                sMetricDAO.batchUpsertMetrics(cache);
                cache.clear();
            } catch (SQLException ex) {
                Logger.getLogger(RestServer.class.getName()).log(Level.SEVERE, null, ex);
            } 
    }
    
    public synchronized List<RFIDLiftData> getCache(){
        List<RFIDLiftData> data = new ArrayList<>(cache);
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
