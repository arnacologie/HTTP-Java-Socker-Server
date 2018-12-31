/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Arnacologie
 */
public class HTTPServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        
        Server s = new Server(80);
        s.run();
    }
    
}
