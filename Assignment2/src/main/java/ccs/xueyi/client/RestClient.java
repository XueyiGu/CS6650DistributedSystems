/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.client;

import ccs.xueyi.model.RFIDLiftData;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
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
            client.target("http://" + url + "/MavenServer/").path("webapi/myresource");
    }
  
    public <T> T postData(Object requestEntity, Class<T> responseType) throws
            ClientErrorException{
        return webTarget.request(MediaType.TEXT_PLAIN)
                        .post(javax.ws.rs.client.Entity.entity(requestEntity,
                                javax.ws.rs.core.MediaType.TEXT_PLAIN),
                                responseType);
    }
    
    public String getData() throws ClientErrorException{
        String ret = "";
        try (Response response = webTarget.request(MediaType.TEXT_PLAIN).get()) {
            if(response.getStatus() != 200){
                ret = "error";
            }else{
                ret = response.readEntity(String.class);
            }
        }
        return ret;
    } 
}
