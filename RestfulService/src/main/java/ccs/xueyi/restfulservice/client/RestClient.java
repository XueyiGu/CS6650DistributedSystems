/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.restfulservice.client;


import ccs.xueyi.restfulservice.beans.RFIDLiftData;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author ceres
 */
public class RestClient {
    public String url;
    private final Client client;
    private final WebTarget webTarget;
    
    public RestClient(String url){
        this.url = url;
        client = ClientBuilder.newClient();
        webTarget = 
//            client.target("http://34.214.49.130:8080/MavenServer/").path("webapi/myresource");
            client.target("http://" + url + "/RestfulService/").path("webapi/rest");
//        client.target("http://localhost:9090/").path("webapi/");
    }
  
    public int postData(RFIDLiftData form) throws ClientErrorException{
        Response response = webTarget.path("/load").request()
                .post(Entity.json(form));
        return response.getStatus();
    }
    
    public RFIDLiftData getData(String skierID, String dayNum) throws ClientErrorException{
        RFIDLiftData data = webTarget.path("/myvert/{skierID}&{dayNum}")
                .resolveTemplate("skierID", skierID)
                .resolveTemplate("dayNum", dayNum)
                .request(MediaType.APPLICATION_JSON)
                .get(RFIDLiftData.class);
        return data;
    } 
}
