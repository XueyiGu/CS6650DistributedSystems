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
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ceres
 */
public class ScheduledCacheExecuter {
    private static final int FIXED_RATE = 5000;
    private static final int BATCH_COUNT = 100;
    private static final RFIDLiftDAO rfidLifDAO = RFIDLiftDAO.getInstance();
    private static final SkierMetricDAO sMetricDAO = SkierMetricDAO.getInstance();
    private static long start = System.currentTimeMillis();
    private static final ScheduledExecutorService scheduledExecutorService =
                            Executors.newScheduledThreadPool(1);
    
    public static void init(){
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                insertCacheData();
            }},
            0, FIXED_RATE, TimeUnit.MILLISECONDS);
    }
    
    private static void insertCacheData(){
        long curr = System.currentTimeMillis();
        DataCache cache = DataCache.getInstance();
        if(cache.getCacheSize() > BATCH_COUNT || (curr - start > FIXED_RATE 
                && cache.getCacheSize() > 0)){
            try {
                List<RFIDLiftData> dataList = DataCache.getInstance().getCache();
                rfidLifDAO.bathInsertData(dataList);
                sMetricDAO.batchUpsertMetrics(dataList);
                start = curr;
            } catch (SQLException ex) {
                Logger.getLogger(ScheduledCacheExecuter.class.getName()).
                        log(Level.SEVERE, null, ex);
            }   
        }
    }
}
