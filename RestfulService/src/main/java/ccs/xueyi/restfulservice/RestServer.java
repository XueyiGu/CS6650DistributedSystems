package ccs.xueyi.restfulservice;

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
    
    private static Map<String, RFIDLiftData> map = new HashMap<>();
    private static final String SUCCESS_RESULT="<result>success</result>";
    private static final String FAILURE_RESULT="<result>failure</result>";

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }
    
    
    @POST
    @Path("/load")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String postData(
                @PathParam("resortID") String resortID,
                @PathParam("dayNum") String dayNum,
                @PathParam("timestamp") String timestamp,
                @PathParam("skierID") String skierID,
                @PathParam("liftID") String liftID) {
        RFIDLiftData data = new RFIDLiftData(resortID, dayNum, timestamp, skierID, liftID);
        if(!map.containsKey(skierID)){
            map.put(skierID, data);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(resortID);
        sb.append(dayNum);
        sb.append(timestamp);
        sb.append(skierID);
        sb.append(liftID);
        return SUCCESS_RESULT; 
    } 
}
