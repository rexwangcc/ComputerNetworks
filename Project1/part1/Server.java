//# pa1    U59925901 WANG CHENGCHEN Part 1-Server-Server Echo Client
package part1;
/*import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;*/
import java.io.*;
import java.net.*;

public class Server {
	public static void main(String[] args) {
		//add port number as a command line argument:
		int portnumber =58669;//default port number if user doesn't input any argument
		if (args.length>0)
		{
			portnumber =Integer.parseInt(args[0]);
		}
		
		try{
			ServerSocket serverSocket = new ServerSocket(portnumber);
			while (true){
				//monitor and accept this Socket, if there comes a request, create a Socket object and go on
				Socket socket = serverSocket.accept();
			/*Get information from client*/	
				//get InputStream from Socket and create correspond BufferedReader
				BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(socket.getInputStream()));
				//get String from client
				String InfoClient =bufferedReader.readLine();
				System.out.println("Client says:"+InfoClient);
			/*Prepare to print*/	
				//get OutputStream from Socket object
				PrintWriter printWriter =new PrintWriter(socket.getOutputStream());
				printWriter.println(InfoClient);
				printWriter.flush();
				
			/*close the socket*/
				printWriter.close();
				bufferedReader.close();
				socket.close();
			         }
		   }
		catch (Exception e){
			System.out.println("error:"+e);
		         }
		finally{
//			serverSocket.close()
		       }
		}

	}

