//U59925901 WANG CHENGCHEN Part 1-Client-Simple Echo Client
package part1;

/*import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;*/
import java.io.*;
import java.net.*;

public class Client {
	public static void main(String[] args) {
		String hostname="localhost";
		int portnumber =58669;
		//See if there are any arguments(such as 192.168.1.1 58669) to run with
		if (args.length>0)
		{
			hostname =args[0];
			portnumber =Integer.parseInt(args[1]);
		}

		try{
			//add hostname to IP address:
			InetAddress addr=InetAddress.getByName(hostname);
		
		/*Create Socket*/
			//appoint the IP address(default localhost) and port
			Socket socket=new Socket(addr,portnumber);
			//10s for overtime
			socket.setSoTimeout(10000);
			
		/*Send Information*/
			//get Outpustream from soket object, Create printwriter
			PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
			//ouput what we read from input stream
			BufferedReader readbuff =new BufferedReader(new InputStreamReader(System.in));
			printWriter.println(readbuff.readLine());
			//refres OutputStream
			printWriter.flush();
			
		/*Get Information*/	
			//get InputStream from socket, create correspond BufferedReader object
			BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//read input string
			String InfoClient =bufferedReader.readLine();
			System.out.println("Server:"+InfoClient);
		/*close the socket*/
			printWriter.close();
			bufferedReader.close();
			socket.close();
		}
		catch (Exception e){
			System.out.println("error:"+e);
		         }

	}

}
