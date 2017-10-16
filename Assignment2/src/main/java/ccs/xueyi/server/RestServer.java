package ccs.xueyi.server;

import ccs.xueyi.model.RFIDLiftData;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class RestServer {

    private static Map<String, RFIDLiftData> map = new HashMap<>();
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
    @Produces(MediaType.TEXT_PLAIN)
    public Response getData(
            @PathParam("skierID") String skierID,
            @PathParam("dayNum") int dayNum
            ) {
        System.out.println("map size: " + map.size());
        String liftID = "0";
        if(map.containsKey(skierID)){
            liftID = map.get(skierID).getLiftID();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(skierID);
        sb.append(liftID);
        return Response.status(200).entity(sb.toString()).build();
    }
    
     /**
     * Method to post data
     * @param resortID
     * @param dayNum
     * @param timestamp
     * @param skierID
     * @param liftID
     * @return 
     */
    @POST
    @Path("/load/{resortID}&{dayNum}&{timestamp}&{skierID}&{liftID}")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response postData(
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
        return Response.status(200).entity(sb.toString()).build(); 
    }   
}
