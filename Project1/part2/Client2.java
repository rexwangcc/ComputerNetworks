//# pa1    U59925901 WANG CHENGCHEN Part2 Client:
//Modified from original part1's Client, instead of just running automatically, added interactive input interface for users to define: IP:Address, rtt/tput, number of probes, and delay.
//Measurement
//Using single thread method
package part2;
//import require packages:
import java.io.*;
import java.net.*;
import java.util.*;

public class Client2 {

	public static void main (String args[]) throws IOException{
		/*==========================================*/
		//Define variables:
		Socket socket =null;
		String measurementtype=null;
		int numofprobes=10;//default experiment times=10
		int messagesize;
		int []rttsize={1,100,200,400,800,1000};
		int []tputsize={1*1024,2*1024,4*1024,8*1024,16*1024,32*1024};
		int []temptuplesize={};
		int delay=0;
		int probesquencenum=0;
		long tOut = 0; // time of sending a probe
		long tIn = 0; //time of receiving probe echo
		long []rtt=null; //stores rtt value of each measurement
		long []tput=null; // stores throughput of each measurement
		byte []ccbyte;//define specific size of bytes------Payload
		/*==========================================*/
	
		/*initialization of arguments*/
		String hostname=null;
		int portnumber;

				//Following steps are dealing with strings that users input, modified from original my part1's getting args[0] and args[1] method
				//this ":"method is much easier to read than previous method
				System.out.println("Input IP address and its port number and experiment type as the following format:\"IPaddress:port number\"");
				Scanner readin=new Scanner(System.in);//get what users input from console
				String tempstr=readin.next().trim();//temp string for dealing with ":"
				hostname=tempstr.substring(0, tempstr.indexOf(":"));
				portnumber=Integer.parseInt(tempstr.substring(tempstr.indexOf(":")+1));	
				System.out.println("Input experiment type:(rtt/tput)");
				readin=new Scanner(System.in);
				//to see if type is rtt or throughput:
				String temptype=readin.next();
				if (temptype.equals("rtt")){
					temptuplesize=rttsize;
					measurementtype="rtt";
				}
				else{
					temptuplesize=tputsize;
					measurementtype="tput";
				}
				System.out.println("Input delay number:");
				readin=new Scanner(System.in);
				delay=Integer.parseInt(readin.next());
				
				System.out.println("Input Num of Probes:");
				readin=new Scanner(System.in);
				numofprobes=Integer.parseInt(readin.next());

		
		InetAddress addr =InetAddress.getByName(hostname);//add hostname to IP address:
		/*start this step of CSP-MP-CTP*/
		for(int j=0;j<=temptuplesize.length-1;j++){
			
			ccbyte=new byte[temptuplesize[j]];	
			messagesize=temptuplesize[j];
			/*Using previous inputs(hostname:portname) as arguments to start a new socket*/
			try{
				socket= new Socket(addr, portnumber);//appoint the IP address and port
				System.out.println("Client is connecting to Server and port:"+hostname+":"+portnumber);			
			} catch (Exception e){
				e.printStackTrace();//Prints this throwable and its backtrace to the print stream.
			}
			/*CSP---Connection Setup Phase*/
			
			String setup= "s"+" "+measurementtype+" "+numofprobes+" "+messagesize+" "+delay+"\n";//send CSP message
			/*Send Information*/
			//get Outpustream from soket object, Create printwriter
			PrintWriter printWriter=null;
			try {
				/*Send CSP message to Server*/
				printWriter = new PrintWriter(socket.getOutputStream(),true);
				printWriter.print(setup);
				printWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			/*Get Information*/
			//get InputStream from socket, create correspond BufferedReader object
			BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//read input string
			String EchoClient =bufferedReader.readLine();
			System.out.println(EchoClient);
			if (EchoClient.equals("404 ERROR: Invalid Connection Setup Message")){
				System.out.println("404 ERROR!");
				socket.close();
			}
			else{
				rtt=new long [numofprobes];
				/*CSP-End && MP-Start*/
				/*MP---Measurement Phase*/
					for (int i=1;i<=numofprobes;i++){
						probesquencenum=i;
						String measurement="m"+" "+probesquencenum+" "+ccbyte+"\n";//send mp message
						tOut = System.nanoTime(); // record the sending time
						/*Send Information*/
							/*Send CSP message to Server*/
							printWriter.print(measurement);
							printWriter.flush();							
//						System.out.println("packet " + probesquencenum +" in "+temptuplesize[j]+" size "+ " sent");
						/*Get Information*/
						//get InputStream from socket, create correspond BufferedReader object
						//read input string
						EchoClient =bufferedReader.readLine();
						tIn = System.nanoTime();  // record the echo time
						rtt[i-1]=tIn-tOut;
				    }
					/*MP-End && CTP-Start*/
					/*CTP---Connection Termination Phase*/
					
					String termination="t\n";
					/*Send CTP message to Server*/
					printWriter.print(termination);
					printWriter.flush(); 
					/*Get Information*/
					//get InputStream from socket, create correspond BufferedReader object
					//read input string
					EchoClient =bufferedReader.readLine();
					System.out.println(EchoClient);
					if (EchoClient.equals("404 ERROR: Invalid Connection Setup Message")){
						System.out.println("404 ERROR!");
						socket.close();
					}
					
					/*Calculate Mean Values: */
					if (temptype.equals("rtt")){
						double meanrtt=0;
						for (int k=0;k<=rtt.length-1;k++){
							System.out.println("rtt "+"= "+rtt[k]);
							meanrtt+=rtt[k];
						}
						meanrtt /= rttsize.length;
						meanrtt =meanrtt/1000000;
						System.out.println("mean of rtt is: "+meanrtt+"ms");
					}
					else{
						double tputvalue=0;
						for (int m=0;m<=rtt.length-1;m++){
							System.out.println("rtt "+"= "+rtt[m]);
							tputvalue+=(double)tputsize[j]*1000000*8/rtt[m];
						}
						tputvalue /=rttsize.length;
						System.out.println("mean of throughput is: "+tputvalue+"kbps");
					}
					//close socket
					socket.close();
			}
		}
	}
}
	
	

