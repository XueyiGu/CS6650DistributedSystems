package ccs.xueyi.restfulservice;

import ccs.xueyi.restfulservice.DAO.RFIDLiftDAO;
import ccs.xueyi.restfulservice.DAO.SkierMetricDAO;
import ccs.xueyi.restfulservice.cache.MeasureCache;
import ccs.xueyi.restfulservice.cache.MeasureCacheExecutor;
import ccs.xueyi.restfulservice.cache.RDFIDCacheExecutor;
import ccs.xueyi.restfulservice.cache.RFIDLiftCache;
import ccs.xueyi.restfulservice.model.MeasureData;
import ccs.xueyi.restfulservice.model.RFIDLiftData;
import ccs.xueyi.restfulservice.model.SkierMetric;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Root resource (exposed at "rest" path)
 */
@Path("rest")
public class RestServer {
    
    private static final String SUCCESS_RESULT="<result>success</result>";
    private static final String FAILURE_RESULT="<result>failure</result>";
    private final RFIDLiftDAO rfidLifDAO = RFIDLiftDAO.getInstance();
    private final SkierMetricDAO sMetricDAO = SkierMetricDAO.getInstance();

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @param skierID
     * @param dayNum
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Path("/myvert/{skierID}&{dayNum}")
    @Produces(MediaType.APPLICATION_JSON)
    public SkierMetric getData(
                        @PathParam("skierID") String skierID,
                        @PathParam("dayNum") String dayNum) throws SQLException {
       // System.out.println("Did search for " + skierID + " and " + dayNum );
        SkierMetric data = sMetricDAO.findSkiMetricByFilter(skierID, dayNum);
        return data;
    }
  
    @POST
    @Path("/load")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postData(RFIDLiftData data) {
        long startTime = System.currentTimeMillis();
        //use int to indicate error or not
        int error = 0;
//        insertWithoutCache(data);
        try{
            insertWithCache(data);
        }catch(Exception e){
            error = 1;
        }
        long responseTime = System.currentTimeMillis() - startTime;
        String msg = dataToString(responseTime, error);
        saveMeasureData(msg, data);
        return Response.status(201).entity(data).build(); 
    } 
    
    /**
     * 
     * @param data 
     */
    private void insertWithCache(RFIDLiftData data){
        if(data.isLastone()){
            List<RFIDLiftData> dataList = new ArrayList<>(RFIDLiftCache.getInstance().getCache());
            RDFIDCacheExecutor executor = new RDFIDCacheExecutor(dataList);
            executor.start();
            RFIDLiftCache.getInstance().clearCache();
            return;
        }
        RFIDLiftCache.getInstance().addToCache(data);
    }
    
    /**
     * 
     * @param measure
     * @param data 
     */
    private void saveMeasureData(String msg, RFIDLiftData data){
        if(data.isLastone()){
            List<String> dataList = new ArrayList<>(MeasureCache.getInstance().getCache());
            MeasureCacheExecutor executor = new MeasureCacheExecutor(dataList);
            executor.start();
            MeasureCache.getInstance().clearCache();
            return;
        }
        MeasureCache.getInstance().addToCache(msg);
    }
    
    /**
     * 
     * @param responseTime
     * @param error
     * @return 
     */
    private String dataToString(long responseTime, int error){
        StringBuilder sb = new StringBuilder();
        sb.append(responseTime);
        sb.append(",");
        sb.append(error);
        return sb.toString();
    }
    /**
     * 
     * @param data 
     */
    private void insertWithoutCache(RFIDLiftData data){
        long recordID = 0;
        long metricID = 0;
        if(data != null){
            try {
                recordID = rfidLifDAO.insertData(data);
                metricID = sMetricDAO.upsertMetrics(data);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
