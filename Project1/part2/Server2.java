//# pa1    U59925901 WANG CHENGCHEN Part2 Server:
//Modified and separated from original part1's Server, added a system.in to let users determine which port to run on.
//Added a new function and separated new classes to meet the problem's requires
//Besides echoing service, it can also accomplish RTT / Throughput measurement following:
//CSP(Connection Setup)-MA(Measurement)-CTP(Connection Termination) phases method:
package part2;
//import require packages:
import java.io.*;
import java.net.*;
import java.util.*;

public class Server2 {
	public static void main(String[] args) throws Exception {
		
		/*==========================================*/
		/*Initialization arguments*/
		int portnumber=58669;
		ServerSocket serversocket=null;
		Socket socket =null;
		Scanner readin =new Scanner(System.in);//get user's inputs
		String hostname=null;
		String []analyzemessage;
		String measurementtype=null;
		int numofprobes;
		int messagesize;
		int delay;
		int examindex=0;
		int probesquencenum=0;
		String InfoClient;
		/*==========================================*/
		
		/*Interactive input process:determine the port number to run on*/
				System.out.println("Appoint a Portnumber:");
				portnumber=Integer.parseInt(readin.next());

		/*ServerSocket Start*/
			ServerSocket serverSocket = new ServerSocket(portnumber);
		/*Server Start Continuously*/
			while (true){
				socket = serverSocket.accept();//monitor and accept this Socket, if there comes a request, create a Socket object and go on
				BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(socket.getInputStream()));//get InputStream from Socket and create correspond BufferedReader
				PrintWriter printWriter =new PrintWriter(socket.getOutputStream());
				/*Get information from client*/	
		        InfoClient =bufferedReader.readLine();//get String from client
				analyzemessage=InfoClient.split(" ");
				/*CSP---Connection Setup Phase*/
				measurementtype=analyzemessage[1];
				numofprobes=Integer.parseInt(analyzemessage[2]);
				messagesize=Integer.parseInt(analyzemessage[3]);
				delay=Integer.parseInt(analyzemessage[4]);
				
	/*			if(!analyzemessage[0].equals("s")&&!analyzemessage[0].equals("m")&&!analyzemessage[0].equals("t")){
					printWriter.println("404 ERROR: Invalid Connection Setup Message");
					socket.close();
				}
	*/			
				if(analyzemessage[0].equals("s")){
					if(!measurementtype.equals("rtt")&&!measurementtype.equals("tput")){
						printWriter.println("404 ERROR: Invalid Connection Setup Message");
						printWriter.flush();
						socket.close();
						continue;
					}
					if(numofprobes<10){
						printWriter.println("404 ERROR: Invalid Connection Setup Message");
						printWriter.flush();
						printWriter.flush();
						socket.close();
						continue;
				    }
	/*				if((measurementtype.equals("rtt")&&messagesize!=1&&messagesize!=100&&messagesize!=200&&messagesize!=400&&messagesize!=800)||(measurementtype.equals("tput")&&messagesize!=1*1024&&messagesize!=2*1024&&messagesize!=4*1024&&messagesize!=8*1024&&messagesize!=16*1024&&messagesize!=32*1024)){
						printWriter.println("404 ERROR: Invalid Connection Setup Message");
						printWriter.flush();
						socket.close();
						continue;
				    }
	*/		    
					if(delay<0){
						printWriter.println("404 ERROR: Invalid Connection Setup Message");
						printWriter.flush();
						socket.close();
						continue;
				}
				else{
					printWriter.println("200 OK: Ready");
					printWriter.flush();
				    }
				}
				/*MP---Measurement Phase*/
				for(int i=1;i<=numofprobes;i++){
					examindex=i;
					InfoClient =bufferedReader.readLine();//get String from client
					analyzemessage=InfoClient.split(" ");
					probesquencenum=Integer.parseInt(analyzemessage[1]);
					if (!analyzemessage[0].equals("m")){
						printWriter.println("404 ERROR: Invalid Measurement Message");
						printWriter.flush();
						socket.close();
					}
					if (examindex!=probesquencenum){
						printWriter.println("404 ERROR: Invalid Measurement Message");
						printWriter.flush();
						socket.close();
					}
					if(delay!=0){
						Thread.currentThread().sleep(delay);
					}
					printWriter.println(InfoClient);
					printWriter.flush();			
		         }				
				/*CTP---Connection Termination Phase*/
				InfoClient =bufferedReader.readLine();//get String from client
				if(InfoClient.equals("t")){
					printWriter.println("200 OK: Closing Connection");
					printWriter.flush();
					socket.close();
	//				serversocket.close();
					continue;	
				}
		//		serversocket.close();
				socket.close();
			}


			

		
		 }	


	

}
