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
import javax.ws.rs.core.Form;
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
            client.target("http://" + url + "/").path("webapi/rest/load");
//        client.target("http://localhost:9090/").path("webapi/");
    }
  
    public String postData(Form form) throws
            ClientErrorException{
//         webTarget.request(MediaType.TEXT_PLAIN)
//                        .post(Entity.entity(requestEntity,
//                                javax.ws.rs.core.MediaType.TEXT_PLAIN),
//                                responseType);
        String result = webTarget.request(MediaType.APPLICATION_XML)
         .post(Entity.entity(form,
            MediaType.APPLICATION_FORM_URLENCODED_TYPE),
            String.class);
        return result;
    }
    
    public String getData(String skierID, String dayNum) throws ClientErrorException{
        RFIDLiftData data = webTarget.path("/{skierID}&{dayNum}")
                .resolveTemplate("skierID", skierID)
                .resolveTemplate("dayNum", dayNum)
                .request(MediaType.APPLICATION_XML)
                .get(RFIDLiftData.class);
        return data.getTimestamp();
    } 
    
    public static void main(String[] args){
        RestClient client = new RestClient("localhost:9090");
        Form form = new Form();
        form.param("resortID", "1");
        form.param("dayNum", "1");
        form.param("timestamp", "1");
        form.param("skierID", "001");
        form.param("liftID", "99");
        String result = client.postData(form);
        System.out.println(result);      
    } 
}
