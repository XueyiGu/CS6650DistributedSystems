/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.restfulservice.client.write;

import ccs.xueyi.restfulservice.client.*;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ceres
 */
public class WriteClientEntrance {
    public static void main(String[] args){
        int threadNum = 10;
        String ip = null;
        String port = null;
        CyclicBarrier barrier = null;
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please input the client parameters \n"
                    + "[thread_num] [server_ip] [server_port] \n"
                    + "Parameters are separeted by space.");
        while (scanner.hasNext()) {
            String[] strs = scanner.nextLine().split("\\s");
            if (strs != null && strs.length == 3) {
                threadNum = Integer.parseInt(strs[0]);
                ip = strs[1];
                port = strs[2];
                barrier = new CyclicBarrier(threadNum);
                break;
            }else{
                System.out.println("Please enter the parameters in correct format");
                return;
            }
        }
        String url = ip + ":" + port;
        final ClientTools processor = new ClientTools(url, threadNum);
        
        Thread readFile = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    processor.fileReader();
                } catch (IOException ex) {
                    Logger.getLogger(WriteClientEntrance.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        readFile.start();
        try {
            readFile.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(WriteClientEntrance.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Finished reading file");
        
        try {
            processor.startThread();
        } catch (TimeoutException ex) {
            Logger.getLogger(WriteClientEntrance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
