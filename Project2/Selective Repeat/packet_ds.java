
public class packet_ds {
	public Packet p;
	public boolean ack_or_rec=false;//rec does not use
	public boolean re=false;
	public double time;
	//public boolean first_ack=false;
	packet_ds(Packet p1,double t){
		p=new Packet(p1);
		time=t;
	}
}
