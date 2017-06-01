import java.util.Iterator;
import java.util.LinkedList;



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
    private LinkedList<Message> a_buffer=new LinkedList<Message>();
    private LinkedList<Packet> a_pending_packet=new LinkedList<Packet>();
//    private int a_current_seqnum=0;
    private int a_expect_acknum=0;
    private int b_expect_seqnum=0;
    //private int count=0;
    private int a_base=0;
    private int a_nextseq=0;
    private Packet b_sndpkt;
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
    	//if(a_nextseq<a_base+WindowSize){
    	if(judge_in(a_base,(a_base+WindowSize-1)%LimitSeqNo,a_nextseq)){
    	//if(a_nextseq<((a_base+WindowSize)%LimitSeqNo)){
   //	if(a_available>0){
   // 		a_available--;
    		p=make_pkt(a_nextseq,message,0);//make a package
    		a_pending_packet.add(p);
    		System.out.println("from a:"+p.toString());
    		toLayer3(A,p);
    		if(a_base==a_nextseq)
    			startTimer(A,RxmtInterval);
    		a_nextseq=(a_nextseq+1)%LimitSeqNo;
    	}
    	else{
    		a_buffer.add(message);
    	}
/*    	if(a_available>0){
    		a_current_seqnum=(a_current_seqnum+1)%LimitSeqNo;//calculate current sequence number,the first packet reach the reciever has sequence number 0
    		a_expect_acknum=a_current_seqnum;
    		p=make_pkt(a_current_seqnum,message,0);//make a package
    		a_pending_packet.add(p);//pending package
    		System.out.println("from a:"+p.toString());
    		toLayer3(A,p);    		
    		startTimer(A,RxmtInterval);
    		a_available--;//decrease available window
    	}
    	else{
    		a_buffer.add(message);// not available,put into buffer
    	}*/
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
//    	System.out.println("no_corrupt"+sum);
    	if(p.getChecksum()==sum)
    		return true;
    	else
    		return false;
    }
    
/*    protected boolean is_fromb_Valid(Packet p){
    	if(p.getAcknum()==a_expect_acknum&&no_corrupt(p)){
    		return true;
    	}
    	else{
    	//	if(p.getAcknum()!=a_expect_acknum)
   // 			System.out.println("is_fromb_Valid_num");
    		//else
    //			System.out.println("is_fromb_Valid_no_corrupt");
    		return false;
    	}
    }*/
    
/*    protected boolean able_tosend(){
    	if(a_base+WindowSize<LimitSeqNo){
    		if(a_nextseq<a_base+WindowSize);
    	}
    	else{
    		
    	}
    }*/
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
    	Packet p1;
    	Message m;
    	if(no_corrupt(packet)){
    		if(judge_in(a_pending_packet.getFirst().getSeqnum(),a_pending_packet.getLast().getSeqnum(),packet.getAcknum())){
    			//Iterator it=a_pending_packet.iterator();
    			do{
    				//p1=(Packet)it.next();
    				p1=a_pending_packet.remove();
    		//		a_available++;
    			}while(p1.getSeqnum()!=packet.getAcknum());
    		}
    		else
    			;
/*    		
    		while(it.hasNext()){
    			
    			p1.
    		}
    		while(a_pending_packet.getFirst().getSeqnum()<a_base){//the statement!
    			a_pending_packet.removeFirst();    			
    		}*/
    		a_base=packet.getAcknum()+1;    		//!!!!!!!!!!!!!!!!!!!!!!!!!!!
    		
    		if(a_base==a_nextseq)
    			stopTimer(A);    			    		
    		else{
    			stopTimer(A);//stop first,then restart
    			startTimer(A,RxmtInterval);
    		}
    		//a_base+WindowSize is the first illegal index
    		while(judge_in(a_base,(a_base+WindowSize-1)%LimitSeqNo,a_nextseq)&&!a_buffer.isEmpty()){
    //		while((a_nextseq<((a_base+WindowSize)%LimitSeqNo))&&!a_buffer.isEmpty()){//send those messages that are stored in buffer
    //		while(a_available>0&&!a_buffer.isEmpty()){
    //			a_available--;
    			m=a_buffer.remove();
    			p=make_pkt(a_nextseq,m,0);//make a package
        		a_pending_packet.add(p);
        		System.out.println("from a:"+p.toString());
        		toLayer3(A,p);
        		if(a_base==a_nextseq)
        			startTimer(A,RxmtInterval);
        		a_nextseq=(a_nextseq+1)%LimitSeqNo;
    		}
    	}
    	else{
    		;//do nothing
    	}
/*    	if(is_fromb_Valid(packet)){
    		a_pending_packet.remove();
    		System.out.println("stop timer!");
    		stopTimer(A);
    		a_available++;
    		if(!a_buffer.isEmpty()){//
    			a_current_seqnum=(a_current_seqnum+1)%LimitSeqNo;//calculate current sequence number
    			Message m=a_buffer.remove();
        		Packet p=make_pkt(a_current_seqnum,m,0);//make a package
        		a_pending_packet.add(p);//pending package
        		System.out.println("from a(b):"+p.toString());
        		toLayer3(A,p);        		
        		startTimer(A,RxmtInterval);
        		a_available--;//decrease available window
        		a_expect_acknum=a_current_seqnum;
    		}
    		else
    			;
    	}
    	else
    		;//
    		*/
    }
    
    // This routine will be called when A's timer expires (thus generating a 
    // timer interrupt). You'll probably want to use this routine to control 
    // the retransmission of packets. See startTimer() and stopTimer(), above,
    // for how the timer is started and stopped.
    
/*    protected void print_Timeout_retransmit(){
    	if(a_base<a_nextseq){
    		for(int i=a_base;i<a_nextseq;i++)
    			System.out.println("from a:"+a_pending_packet.remove().toString());
    	}
    	else{
    		for(int i=a_base;i<LimitSeqNo;i++)//number wrap around
    			System.out.println("from a:"+a_pending_packet.remove().toString());
    		for(int i=0;i<a_nextseq;i++)
    			System.out.println("from a:"+a_pending_packet.remove().toString());
    	}
    }*/
    
    protected void Timeout_retransmit(){
    	Packet p;
    	Iterator it=a_pending_packet.iterator();
    	int temp=0;
    	do{
    		p=(Packet)it.next();
			System.out.println("from a:"+p.toString());
			toLayer3(A,p);
			temp=(p.getSeqnum()+1)%LimitSeqNo;
    	}while(temp!=a_nextseq);
/*    	if(a_base<a_nextseq){
    		for(int i=a_base;i<a_nextseq;i++){
    			//p=a_pending_packet.remove();
    			p=(Packet)it.next();
    			System.out.println("from a:"+p.toString());
    			toLayer3(A,p);
    		}
    	}
    	else{
    		for(int i=a_base;i<LimitSeqNo;i++){//number wrap around
    			p=(Packet)it.next();
    			System.out.println("from a:"+p.toString());
    			toLayer3(A,p);
    		}
    		for(int i=0;i<a_nextseq;i++){
    			p=(Packet)it.next();
    			System.out.println("from a:"+p.toString());
    			toLayer3(A,p);
    		}
    	}*/
    }
    
    protected void aTimerInterrupt()
    {
    	startTimer(A,RxmtInterval);
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
    	LimitSeqNo=WindowSize+1;
    	a_base=1;
    	a_nextseq=1;
    }
    
    // This routine will be called whenever a packet sent from the B-side 
    // (i.e. as a result of a toLayer3() being done by an A-side procedure)
    // arrives at the B-side.  "packet" is the (possibly corrupted) packet
    // sent from the A-side.
    
    protected boolean is_froma_Valid(Packet p){//bug!!!
    	if(p.getSeqnum()==b_expect_seqnum&&no_corrupt(p)){
    		return true;
    	}
    	else{
    		if(p.getSeqnum()!=b_expect_seqnum){
    	//		System.out.println("is_froma_Valid_num");
    		}
    		else{
    		//	System.out.println("no_corrupt(p)");
    		}
    		return false;
    	}
    }
    
    protected void bInput(Packet packet)
    {
    	if(is_froma_Valid(packet)){
    		toLayer5(packet.getPayload());//deliever the layer 5
    //		System.out.println("bInput"+packet.getPayload());
    		Message m=new Message(String.valueOf(new char[20]));
    		b_sndpkt=make_pkt(0,m,b_expect_seqnum);
    		System.out.println("from b:"+b_sndpkt.toString());
    		toLayer3(B,b_sndpkt);    		
    		b_expect_seqnum=(b_expect_seqnum+1)%LimitSeqNo;
    	}
    	else{
/*    		Message m=new Message(String.valueOf(new char[20]));
    	//	System.out.println("bInput_error");
    		Packet p;
    		if(packet.getSeqnum()!=b_expect_seqnum)
    			p=make_pkt(0,m,packet.getSeqnum());
    		else
    			p=make_pkt(0,m,(packet.getSeqnum()+1)%LimitSeqNo);*/    		
    		System.out.println("from b:"+b_sndpkt.toString());
    		toLayer3(B,b_sndpkt);    		
    	}
    }
    
    // This routine will be called once, before any of your other B-side 
    // routines are called. It can be used to do any required
    // initialization (e.g. of member variables you add to control the state
    // of entity B).
    protected void bInit()
    {
    	b_expect_seqnum=1;
    	Message m=new Message(String.valueOf(new char[20]));
    	b_sndpkt=make_pkt(0,m,0);//make a package
    }

    // Use to print final statistics
    protected void Simulation_done()
    {
    	//System.out.println("total lost:");
    }	


}
