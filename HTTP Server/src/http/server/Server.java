/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package http.server;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

/**
 *
 * @author Arnacologie
 */
public class Server {
    
        int port1 = 80;
        final ServerSocket server;
        private static final String resourcesFolder = "resources";
        
        public Server(int port) throws IOException{
            server = new ServerSocket(port1);
        }
        
        public void run() throws IOException{
            
            System.out.println("En attente de connection sur le port "+port1);

            while(true)
            {
                try (Socket client = server.accept())
                {
                    String httpResponse = "HTTP/1.1 200 OK\r\n\r\n"; 
                    
                    InputStream is = client.getInputStream();
                    BufferedReader brRequest = new BufferedReader(new InputStreamReader(is));
                    //Recupère la status_line ex : GET /index.html HTTP/1.1
                    String request = brRequest.readLine();
                    String[] requestParam = request.split(" ");
                    String methodRequest = requestParam[0];
                    String path = requestParam[1];
                    System.out.println(methodRequest);
                    Date today;
                    
                    switch(methodRequest){
                        case "GET":
                            File file = new File(resourcesFolder+"/index.html");
                            
                            //Sinon tenter de récupérer la ressource demandée
                            if(!path.equals("/")) file = new File(resourcesFolder+path);
                            
                            if (!file.exists()) {
                                String httpResponseError = "HTTP 404\r\n\r\n";
                                BufferedReader bfr404 = new BufferedReader(new FileReader(new File(resourcesFolder+"/not_found.html")));
                                String line404;
                                while ((line404 = bfr404.readLine()) != null) {
                                    httpResponseError += line404;
                                }
                                client.getOutputStream().write(httpResponseError.getBytes("UTF-8"));
                                //Fermeture des streams
                                bfr404.close();
                            }
                            String line;
                            //Chargement de la page demandée
                            //L'image ne s'affiche pas, j'ai tenté en vain
                            if(path.endsWith(".jpg") || path.endsWith(".jpeg")){
                                String image = "";
                                BufferedImage bI = null;
                                try{
                                    bI = ImageIO.read(file);
                                }catch(FileNotFoundException e){
                                    System.out.println(e.getMessage());
                                }
                                
                                today = new Date();
                                httpResponse = "HTTP/1.1 200 \r\n";
                                httpResponse+= "Date : "+today.toString()+"\r\n";
                                httpResponse+= "Server: Server_EG\r\n";
                                httpResponse+= "Content-Type: image/jpg\r\n";

                                httpResponse+= bI;
                                httpResponse+= "\r\n";
                                client.getOutputStream().write(httpResponse.getBytes("UTF-8"));
                                //Fermeture des streams
                                brRequest.close();
                            }
                            
                            else try(BufferedReader brFile = new BufferedReader(new FileReader(file))){
                                    today = new Date();
                                    httpResponse = "HTTP/1.1 200 OK\r\n";
                                    httpResponse+= "Date : "+today.toString()+"\r\n";
                                    httpResponse+= "Server: Server_EG\r\n";
                                    httpResponse+= "Content-Type: text/html\r\n";
                                    httpResponse+= "\n";
                                    while ((line = brFile.readLine()) != null) {
                                        httpResponse += line;
                                    }
                                    httpResponse+= "\r\n";
                                    //Renvoi de la page demandée à l'utilisateur
                                    client.getOutputStream().write(httpResponse.getBytes("UTF-8"));

                                    //Fermeture des streams
                                    brRequest.close();
                                    brFile.close();
                                }catch(FileNotFoundException e){
                                    System.out.println(e.getMessage());
                                }
                            break;
                        case "POST":
                            int postDataI = -1;
                            System.out.println("\nHTTP-HEADER:");
                            while ((line = brRequest.readLine()) != null && (line.length() != 0)) {
                                System.out.println("\t"+line);
                                //Je verifie si Content-Lent est present, si oui je recupere l'int qui le succede (nombre de bytes à recuperer)
                                if (line.indexOf("Content-Length:") > -1) {
                                    postDataI = new Integer(
                                            line.substring(
                                                    line.indexOf("Content-Length:") + 16,
                                                    line.length())).intValue();
                                }
                            }
                            String postData = "";
                            String POSTOutput =  "";
                            //Je lis le nombre de char recus avec un tableau de bytes récuperes precedemment 
                            if (postDataI > 0) {
                                char[] charArray = new char[postDataI];
                                brRequest.read(charArray, 0, postDataI);
                                postData = new String(charArray);
                                //Je format l'URL encoded query string recuperee
                                String[] POSTvalues = postData.split("&");
                                System.out.println();
                                for (String s : POSTvalues){
                                    System.out.println(s);
                                    POSTOutput+=s+"\n\n";
                                }
                            }
                            today = new Date();
                            //Je revoie au client les données rentrees (test-only)
                            httpResponse = "HTTP/1.1 200 \r\n";
                            httpResponse+= "Date : "+today.toString()+"\r\n";
                            httpResponse+= "Server: Server_EG\r\n";
                            httpResponse+= "Connection: close\r\n";
                            httpResponse+= "Content-Type: text/plain\r\n";
                            httpResponse+="\n";
                            httpResponse+= POSTOutput;
                            httpResponse+= "\r\n";
                            client.getOutputStream().write(httpResponse.getBytes("UTF-8"));
                            brRequest.close();
                            break;
                        default:
                            System.out.println("The method used isn't GET nor POST");
                            break;
                    }
                    
                    
                }
            }
        }
        
}
