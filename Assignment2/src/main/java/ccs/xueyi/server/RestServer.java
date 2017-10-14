package ccs.xueyi.server;

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
        return Response.status(200).entity("").build();
    }
    
     /**
     * Method to post data
     * @param resortID
     * @param dayNum
     * @param timestamp
     * @param skierID
     * @param liftOD
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
                @PathParam("liftID") String liftOD) {
        return Response.status(200).entity("").build(); 
    }   
}
