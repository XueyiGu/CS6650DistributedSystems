/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.restfulservice.client;

import java.util.Scanner;

/**
 *
 * @author ceres
 */
public class ClientEntrance {
    public static void main(String[] args){
        int threadNum = 10;
        String ip = null;
        String port = null;
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please input the client parameters \n"
                    + "[thread_num] [iteration_num] [server_ip] [server_port] \n"
                    + "Parameters are separeted by space.");
        while (scanner.hasNext()) {
            String[] strs = scanner.nextLine().split("\\s");
            if (strs != null && strs.length == 3) {
                threadNum = Integer.parseInt(strs[0]);
                ip = strs[1];
                port = strs[2];
                break;
            }else{
                System.out.println("Please enter the parameters in correct format");
                return;
            }
        }
        String url = ip + ":" + port;
        ClientTools processor = new ClientTools(url, threadNum);
        
        
        
    }
    
}
