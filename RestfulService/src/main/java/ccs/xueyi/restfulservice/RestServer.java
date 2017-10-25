package ccs.xueyi.restfulservice;

import ccs.xueyi.restfulservice.DAO.RFIDLiftDAO;
import ccs.xueyi.restfulservice.DAO.SkierMetricDAO;
import ccs.xueyi.restfulservice.cache.CacheExecutor;
import ccs.xueyi.restfulservice.cache.DataCache;
import ccs.xueyi.restfulservice.model.RFIDLiftData;
import ccs.xueyi.restfulservice.model.SkierMetric;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
//        insertWithoutCache(data);
        insertWithCache(data);
        return Response.status(201).entity(data).build(); 
    } 
    
    private void insertWithCache(RFIDLiftData data){
        if(data.isLastone()){
            List<RFIDLiftData> dataList = new ArrayList<>(DataCache.getInstance().getCache());
            CacheExecutor executor = new CacheExecutor(dataList);
            executor.start();
            DataCache.getInstance().clearCache();
            return;
        }
        DataCache.getInstance().addToCache(data);
    }
    
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
