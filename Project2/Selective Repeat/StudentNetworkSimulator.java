
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;





public class StudentNetworkSimulator extends NetworkSimulator{
	   /*
     * Predefined Constants (static member variables):
     *
     *   int MAXDATASIZE : the maximum size of the Message data and
     *                     Packet payload
     *
     *   int A           : a predefined integer that represents entity A
     *   int B           : a predefined integer that represents entity B 
     *
     * Predefined Member Methods:
     *
     *  void stopTimer(int entity): 
     *       Stops the timer running at "entity" [A or B]
     *  void startTimer(int entity, double increment): 
     *       Starts a timer running at "entity" [A or B], which will expire in
     *       "increment" time units, causing the interrupt handler to be
     *       called.  You should only call this with A.
     *  void toLayer3(int callingEntity, Packet p)
     *       Puts the packet "p" into the network from "callingEntity" [A or B]
     *  void toLayer5(String dataSent)
     *       Passes "dataSent" up to layer 5
     *  double getTime()
     *       Returns the current time in the simulator.  Might be useful for
     *       debugging.
     *  int getTraceLevel()
     *       Returns TraceLevel
     *  void printEventList()
     *       Prints the current event list to stdout.  Might be useful for
     *       debugging, but probably not.
     *
     *
     *  Predefined Classes:
     *
     *  Message: Used to encapsulate a message coming from layer 5
     *    Constructor:
     *      Message(String inputData): 
     *          creates a new Message containing "inputData"
     *    Methods:
     *      boolean setData(String inputData):
     *          sets an existing Message's data to "inputData"
     *          returns true on success, false otherwise
     *      String getData():
     *          returns the data contained in the message
     *  Packet: Used to encapsulate a packet
     *    Constructors:
     *      Packet (Packet p):
     *          creates a new Packet that is a copy of "p"
     *      Packet (int seq, int ack, int check, String newPayload)
     *          creates a new Packet with a sequence field of "seq", an
     *          ack field of "ack", a checksum field of "check", and a
     *          payload of "newPayload"
     *      Packet (int seq, int ack, int check)
     *          chreate a new Packet with a sequence field of "seq", an
     *          ack field of "ack", a checksum field of "check", and
     *          an empty payload
     *    Methods:
     *      boolean setSeqnum(int n)
     *          sets the Packet's sequence field to "n"
     *          returns true on success, false otherwise
     *      boolean setAcknum(int n)
     *          sets the Packet's ack field to "n"
     *          returns true on success, false otherwise
     *      boolean setChecksum(int n)
     *          sets the Packet's checksum to "n"
     *          returns true on success, false otherwise
     *      boolean setPayload(String newPayload)
     *          sets the Packet's payload to "newPayload"
     *          returns true on success, false otherwise
     *      int getSeqnum()
     *          returns the contents of the Packet's sequence field
     *      int getAcknum()
     *          returns the contents of the Packet's ack field
     *      int getChecksum()
     *          returns the checksum of the Packet
     *      int getPayload()
     *          returns the Packet's payload
     *
     */

    /*   Please use the following variables in your routines.
     *   int WindowSize  : the window size
     *   double RxmtInterval   : the retransmission timeout
     *   int LimitSeqNo  : when sequence number reaches this value, it wraps around
     */

    public static final int FirstSeqNo = 0;
    private int WindowSize;
    private double RxmtInterval;
    private int LimitSeqNo;
    
    private int a_available=1;
    private LinkedList<message_ds> a_buffer=new LinkedList<message_ds>();
    private LinkedList<packet_ds> a_pending_packet=new LinkedList<packet_ds>();
//    private int a_current_seqnum=0;
//    private int a_expect_acknum=0;
    private int b_expect_seqnum=0;
    //private int count=0;
    private int a_base=0;
    private int a_nextseq=0;
    private Packet b_sndpkt;
    private LinkedList<Packet> b_pending_packet=new LinkedList<Packet>();
    
    private LinkedList<relative_time_ds> time_relative=new LinkedList<relative_time_ds>();
    //private int total_time=0;
//    private double time1=0;
//    private double time2=0;
    private double time=0;
    private boolean dump=false;
//    private boolean flag_first=true;
//    private int packet_pointer=0;
    //private int b_expect_first=0;
    //private int b_expect_end=0;
    
    private statistic s=new statistic(1);
    private int s_index=0;
    private int numM=0;
    
    
    private double perf=0;
    private int fa=0;
    // Add any necessary class variables here.  Remember, you cannot use
    // these variables to send messages error free!  They can only hold
    // state information for A or B.
    // Also add any necessary methods (e.g. checksum of a String)

    // This is the constructor.  Don't touch!
    public StudentNetworkSimulator(int numMessages,
                                   double loss,
                                   double corrupt,
                                   double avgDelay,
                                   int trace,
                                   int seed,
                                   int winsize,
                                   double delay)
    {
        super(numMessages, loss, corrupt, avgDelay, trace, seed);
	WindowSize = winsize;
	LimitSeqNo = winsize+1;
	RxmtInterval = delay;
	numM=numMessages;
    }

    public static double Add(double _a,double _b){
		BigDecimal a=new BigDecimal(Double.toString(_a));
		BigDecimal b=new BigDecimal(Double.toString(_b));
		return a.add(b).doubleValue();
	}
    
    public static double Subtract(double _a,double _b){//use big decimal to assure the accurancy
		BigDecimal a=new BigDecimal(Double.toString(_a));
		BigDecimal b=new BigDecimal(Double.toString(_b));	
		return a.subtract(b).doubleValue();
	}
    // This routine will be called whenever the upper layer at the sender [A]
    // has a message to send.  It is the job of your protocol to insure that
    // the data in such a message is delivered in-order, and correctly, to
    // the receiving upper layer.
    protected int calculate_checksum(int seqnum,Message m,int ack){
    	char []temp=m.getData().toCharArray();
    	int sum=0;
    	sum+=seqnum;
    	sum+=ack;
    	for(int i=0;i<temp.length;i++){
    		sum=sum+(int)temp[i];
    	}
    	return sum;
    }
    
    protected Packet make_pkt(int seqnum,Message m,int ack){
    	int checksum=calculate_checksum(seqnum,m,ack);
    	Packet p=new Packet(seqnum,ack,checksum,m.getData());
    	return p;
    }
    
    protected void aOutput(Message message)
    {
    	Packet p;
    	packet_ds pd;
    	//if(a_nextseq<a_base+WindowSize){
    	if(judge_in(a_base,(a_base+WindowSize-1)%LimitSeqNo,a_nextseq)){
    	//if(a_nextseq<((a_base+WindowSize)%LimitSeqNo)){    		
    		p=make_pkt(a_nextseq,message,0);//make a package
    		pd=new packet_ds(p,getTime());
    		a_pending_packet.add(pd);
    		System.out.println("from a:"+p.toString()+" ,time:"+getTime());
//    		packet_pointer++;
    		toLayer3(A,p);
    		s.original[s_index]++;
    		s.all++;
    		s.all_s++;
    		//if(a_base==a_nextseq)
    		//	startTimer(A,RxmtInterval);
    		
    		time=getTime();
    		if(a_nextseq==a_base){
    			startTimer(A,RxmtInterval);//only here?no
    			if(!time_relative.isEmpty())
    				System.out.println("have element!******************************");    			
    		}
    		time_relative.add(new relative_time_ds(a_nextseq,Add(time,RxmtInterval)));
    		a_nextseq=(a_nextseq+1)%LimitSeqNo;//increment the sequence number
    	}
    	else{
    		a_buffer.add(new message_ds(message,getTime()));
    		System.out.println("wait for window to slide in a:"+message.getData()+"  time:"+getTime());
    	}
    }
    
    // This routine will be called whenever a packet sent from the B-side 
    // (i.e. as a result of a toLayer3() being done by a B-side procedure)
    // arrives at the A-side.  "packet" is the (possibly corrupted) packet
    // sent from the B-side.
    protected boolean no_corrupt(Packet p){
    	char []temp=p.getPayload().toCharArray();
    	int sum=0;
    	sum+=p.getAcknum();
    	sum+=p.getSeqnum();
    	for(int i=0;i<temp.length;i++){
    		sum=sum+(int)temp[i];
    	}
 //   	System.out.println("no_corrupt"+sum);
    	if(p.getChecksum()==sum)
    		return true;
    	else{
    		s.corrupt[s_index]++;
    		return false;
    	}
    }
    
    protected boolean judge_in(int first,int last,int ack){
    	if(first<=last){//normal case,no wrap around
    		if(ack>=first&&ack<=last)
    			return true;
    		else
    			return false;
    	}
    	else{
    		if(ack>=first||ack<=last)
    			return true;
    		else
    			return false;
    	}
    }
    
    protected void aInput(Packet packet)//forget remove element from pending buffer
    {
    	Packet p;
    	//Packet p1;
    	Message m;//for message in buffer
    	message_ds md;
    	packet_ds pd;
    	relative_time_ds temp2;
    	double temp,temp1=0;
    	int one=0;
    	//a_base=(packet.getAcknum()+1)%LimitSeqNo;
    	
    	if(no_corrupt(packet)){
    		System.out.println("ack recieved by A, "+packet.toString()+" ,time:"+getTime());
    		if(a_pending_packet.isEmpty())//bug!!!!!!!!!!!!!!
    			return ;
    		if(judge_in(a_pending_packet.getFirst().p.getSeqnum(),a_pending_packet.getLast().p.getSeqnum(),packet.getAcknum())){
    			ListIterator<packet_ds> it=a_pending_packet.listIterator();
    			ListIterator<relative_time_ds> it2=time_relative.listIterator();    			
    			while(it.hasNext()){//find the packet in the pending window and set it acked and calculate performance
    				pd=(packet_ds)it.next();    				    				
    				if(pd.p.getSeqnum()==packet.getAcknum()){    					
    					if(!pd.re){///for rtt
    						s.rtt[s_index]+=getTime()-pd.time;
    						s.nore++;//impossible to recount
    					}
    					if(!pd.ack_or_rec){
    						perf+=getTime()-pd.time;
    						fa++;
    						s.performance[s_index]=s.performance[s_index]+getTime()-pd.time;
    						s.firstacked++;//for performance
    					}
    					pd.ack_or_rec=true;
    					it.set(pd);//alreafy acked
    					break;
    				}
    			}    			
    			while(it2.hasNext()){//remove the timer
    				one++;//first acked
    				temp2=(relative_time_ds)it2.next();
    				temp=temp2.seqnum;    				
    				if(temp==packet.getAcknum()){    					
    					it2.remove();//remove the clock associated with the packet
    					break;    					
    				}
    			}
    			
    			if(one==1){
    				stopTimer(A);
    				if(!time_relative.isEmpty())
    					startTimer(A,Subtract(time_relative.getFirst().interval,getTime()));    			
    			}    		
    			if(a_pending_packet.getFirst().p.getSeqnum()==packet.getAcknum()){//the window can move forward    			
    				do{
        				pd=a_pending_packet.remove();
        				a_base=(a_base+1)%LimitSeqNo;
        				if(a_pending_packet.isEmpty())
        					break;
        				else
        					pd=a_pending_packet.getFirst();
        			}while(pd.ack_or_rec==true);
    			}
    			else //window can not move
    				;    			
    		}
    		else//ack not within the window
    			;    		
    		while(judge_in(a_base,(a_base+WindowSize-1)%LimitSeqNo,a_nextseq)&&!a_buffer.isEmpty()){
    			md=a_buffer.remove();
    			m=md.m;
    			s.delay[s_index]+=getTime()-md.time;
    			p=make_pkt(a_nextseq,m,0);//make a package
        		pd=new packet_ds(p,getTime());
        		a_pending_packet.add(pd);
        		System.out.println("from a:"+p.toString()+" ,time:"+getTime());//        		
        		toLayer3(A,p);
        		s.original[s_index]++;
        		s.all_s++;
        		s.all++;        		
        		time=getTime();        		
        		if(a_nextseq==a_base){
        			startTimer(A,RxmtInterval);//only here?no
        			if(!time_relative.isEmpty())
        				System.out.println("have element!******************************");    			
        		}
        		time_relative.add(new relative_time_ds(a_nextseq,Add(time,RxmtInterval)));        		
        		a_nextseq=(a_nextseq+1)%LimitSeqNo;
    		}
    	}
    	else//corrupt ack from B
    		System.out.println("corrupt acked recieved by A, "+packet.toString()+" ,time:"+getTime());//do nothing
    	//return ;
    	return ;
    }
    
    // This routine will be called when A's timer expires (thus generating a 
    // timer interrupt). You'll probably want to use this routine to control 
    // the retransmission of packets. See startTimer() and stopTimer(), above,
    // for how the timer is started and stopped.
    
    protected void Timeout_retransmit(){
    	relative_time_ds temp1=time_relative.remove();
    	packet_ds d;
    	Packet p;
    	if(!time_relative.isEmpty()){
    		double test=Subtract(time_relative.getFirst().interval,temp1.interval);
    		if(test<0)
    			System.out.println("error test<0*********************************");
    		startTimer(A,test);
    		temp1.interval+=RxmtInterval;
    		time_relative.add(temp1);
    	}
    	else{
    		startTimer(A,RxmtInterval);
    		temp1.interval+=RxmtInterval;
    		time_relative.add(temp1);
    	}
    	ListIterator<packet_ds> it1=a_pending_packet.listIterator();
    	while(it1.hasNext()){
    		d=(packet_ds)it1.next();//cast error
    		p=d.p;
    		if(p.getSeqnum()==temp1.seqnum){//find the packet and retransmit
    			d.re=true;
    			it1.set(d);
    			System.out.println("from a(re):"+p.toString()+" ,time:"+getTime());
    			toLayer3(A,p);    			
    			s.retransmit[s_index]++;
    			s.all_s++;
    			s.all++;
    		}
    	}
    }
    
    protected void aTimerInterrupt()
    {
    	//startTimer(A,RxmtInterval);
    	Timeout_retransmit();
    	//System.out.println("from a:"+a_pending_packet.get(0).toString());    	    	    
    }
    
    // This routine will be called once, before any of your other A-side 
    // routines are called. It can be used to do any required
    // initialization (e.g. of member variables you add to control the state
    // of entity A).
    protected void aInit()
    {
    	a_available=WindowSize;
    	LimitSeqNo=WindowSize*2;
    	a_base=1;
    	a_nextseq=1;
    }
    
    // This routine will be called whenever a packet sent from the B-side 
    // (i.e. as a result of a toLayer3() being done by an A-side procedure)
    // arrives at the B-side.  "packet" is the (possibly corrupted) packet
    // sent from the A-side.
    
    public void order_insert(Packet p){//well_tested
    	ListIterator<Packet> it=b_pending_packet.listIterator();
		Packet temp = null;
		boolean end_flag=false;
		boolean begin_flag=true;
		boolean middle_flag=false;
    	if(b_expect_seqnum<=(b_expect_seqnum+WindowSize-1)%LimitSeqNo){//normal case    		
    		while(it.hasNext()){
    			begin_flag=false;
    			end_flag=true;
    			temp=(Packet) it.next();
    			if(temp.getSeqnum()==p.getSeqnum())//already received
    				return;
    			else{
    				if(temp.getSeqnum()>p.getSeqnum()){//logic
    					middle_flag=true;
    					end_flag=false;
    					break;
    				}
    				else
    					continue;
    			}    				
    		}
    		if(begin_flag==true)
    			b_pending_packet.addFirst(p);
    		else{
    			if(end_flag==true)
    				b_pending_packet.addLast(p);
    			else{
    				if(!it.hasPrevious())
    					b_pending_packet.addFirst(p);
    				else{
    					it.previous();
    					it.add(p);
    				}
    			}
    		}
    	}
    	else{//wrap around
    		if(p.getSeqnum()>=b_expect_seqnum){//insert before the LimitSeqNo
    			while(it.hasNext()){
        			begin_flag=false;
        			end_flag=true;
        			temp=(Packet) it.next();
        			if(temp.getSeqnum()<b_expect_seqnum){//prevent wrap around
        				//it.previous();
        				middle_flag=true;
    					end_flag=false;
        				break;
        			}
        			if(temp.getSeqnum()==p.getSeqnum())//already received
        				return;
        			else{
        				if(temp.getSeqnum()>p.getSeqnum()){//logic
        					middle_flag=true;
        					end_flag=false;
        					break;
        				}
        				else
        					continue;
        			}    				
        		}
        		if(begin_flag==true)
        			b_pending_packet.addFirst(p);
        		else{
        			if(end_flag==true)
        				b_pending_packet.addLast(p);
        			else{
        				if(!it.hasPrevious())
        					b_pending_packet.addFirst(p);
        				else{
        					it.previous();
        					it.add(p);
        				}
        			}
        		}
    		}
    		else{//insert between zero and end
    			while(it.hasNext()){
        			begin_flag=false;
        			end_flag=true;
        			temp=(Packet) it.next();
        			if(temp.getSeqnum()>=b_expect_seqnum)
        				continue;        		
        			if(temp.getSeqnum()==p.getSeqnum())//already received
        				return;
        			else{
        				if(temp.getSeqnum()>p.getSeqnum()){//logic
        					middle_flag=true;
        					end_flag=false;
        					break;
        				}
        				else
        					continue;
        			}    				
        		}
        		if(begin_flag==true)
        			b_pending_packet.addFirst(p);
        		else{
        			if(end_flag==true)
        				b_pending_packet.addLast(p);
        			else{
        				if(!it.hasPrevious())
        					b_pending_packet.addFirst(p);
        				else{
        					it.previous();
        					it.add(p);
        				}
        			}
        		}    			
    		}//else ,insert between zero and end
    	}//else (has warp around)		
	}

    protected void bInput(Packet packet)
    {
    	Packet p;
    	s.t++;
    	if(no_corrupt(packet)){
    		System.out.println("packet recieved by B, "+packet.toString()+" ,time:"+getTime());
    		if(judge_in(b_expect_seqnum,(b_expect_seqnum+WindowSize-1)%LimitSeqNo,packet.getSeqnum())){
    			Message m=new Message(String.valueOf(new char[20]));
        		b_sndpkt=make_pkt(0,m,packet.getSeqnum());
        		System.out.println("from b:"+b_sndpkt.toString()+" ,time:"+getTime());
        		toLayer3(B,b_sndpkt);
        		s.all++;
        		s.ack[s_index]++;
    			order_insert(packet);
    			ListIterator<Packet> it=b_pending_packet.listIterator();
    			while(it.hasNext()){
    				p=(Packet)it.next();
    				if(p.getSeqnum()==b_expect_seqnum){//forget to dequeue!!!!!!!
    					toLayer5(p.getPayload());//deliever the layer 5
    					it.remove();
			    		b_expect_seqnum=(b_expect_seqnum+1)%LimitSeqNo;			    		
    				}
    				else//window stop to move
    					break;
    			}    			    			
    		}
    		else{
    			Message m=new Message(String.valueOf(new char[20]));
        		b_sndpkt=make_pkt(0,m,packet.getSeqnum());
        		System.out.println("from b(unexpected seqnum):"+b_sndpkt.toString()+" ,time:"+getTime());
        		toLayer3(B,b_sndpkt);
        		s.all++;
        		s.ack[s_index]++;
    		}
    			
    	}
    	else{
    		System.out.println("corrupt packet recieved by B, "+packet.toString()+" ,time:"+getTime());
    	}
    }
    
    // This routine will be called once, before any of your other B-side 
    // routines are called. It can be used to do any required
    // initialization (e.g. of member variables you add to control the state
    // of entity B).
    protected void bInit()
    {
    	b_expect_seqnum=1;
    //	Message m=new Message(String.valueOf(new char[20]));
    //	b_sndpkt=make_pkt(0,m,0);//make a package
    }

    // Use to print final statistics
    protected void Simulation_done()
    {
    	//System.out.println("total lost:");
    	double time=getTime();
    	while(!a_buffer.isEmpty()){
    		s.delay[s_index]+=time-a_buffer.remove().time;
    	}
    	s.delay[s_index]=math_yt.Divide(s.delay[s_index],numM);
    	if(s.performance[s_index]==0)
    		s.performance[s_index]=Double.POSITIVE_INFINITY;
    	else
    		s.performance[s_index]=math_yt.Divide(s.performance[s_index],s.firstacked);
    	if(s.rtt[s_index]==0)
    		s.rtt[s_index]=Double.POSITIVE_INFINITY;
    	else
    		s.rtt[s_index]=math_yt.Divide(s.rtt[s_index],s.nore);
    	s.tput[s_index]=math_yt.Divide(math_yt.Multiply(s.t,256),time);
    	/*if(s.firstacked==0)
    		s.goodput[s_index]=Double.POSITIVE_INFINITY;
    	else
    		s.goodput[s_index]=math_yt.Divide(math_yt.Multiply(s.firstacked,160),time);*/
    	s.goodput[s_index]=math_yt.Divide(math_yt.Multiply(s.firstacked,160),time);
    	System.out.println("****************************8");
    	System.out.println("time:"+getTime());
    	System.out.println("simulation_done:"+fa+", average performance:"+math_yt.Divide(perf, fa));
    }	


}
