package ccs.xueyi.restfulservice;

import ccs.xueyi.restfulservice.DAO.RFIDLiftDAO;
import ccs.xueyi.restfulservice.beans.RFIDLiftData;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


/**
 * Root resource (exposed at "rest" path)
 */
@Path("rest")
public class RestServer {
    
    private static final String SUCCESS_RESULT="<result>success</result>";
    private static final String FAILURE_RESULT="<result>failure</result>";
    private final RFIDLiftDAO rfidLifDAO = RFIDLiftDAO.getInstance();
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
    public RFIDLiftData getData(
                        @PathParam("skierID") String skierID,
                        @PathParam("dayNum") String dayNum) {
        RFIDLiftData data = rfidLifDAO.findData(skierID, dayNum);
        return data;
    }
    
    
    @POST
    @Path("/load")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public String postData(RFIDLiftData data) {
        if(data != null){
            rfidLifDAO.insertData(data);
            return SUCCESS_RESULT;
        }
        return FAILURE_RESULT; 
    } 
}
