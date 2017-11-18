/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.restfulservice.cache;

import ccs.xueyi.restfulservice.DAO.RFIDLiftDAO;
import ccs.xueyi.restfulservice.DAO.SkierMetricDAO;
import ccs.xueyi.restfulservice.model.RFIDLiftData;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ceres
 */
public class RDFIDCacheExecutor extends Thread{
    private final RFIDLiftDAO rfidLifDAO = RFIDLiftDAO.getInstance();
    private final SkierMetricDAO sMetricDAO = SkierMetricDAO.getInstance();
    List<RFIDLiftData> dataList;
    
    public RDFIDCacheExecutor(List<RFIDLiftData> dataList){
        this.dataList = dataList;
    }
    @Override
    public void run(){
        try {
            System.out.println("cache size is " + dataList.size());
            rfidLifDAO.bathInsertData(dataList);
            sMetricDAO.batchUpsertMetrics(dataList);
        } catch (SQLException ex) {
            Logger.getLogger(RDFIDCacheExecutor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
