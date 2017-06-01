

//ack+all_s=all
//all-corrupt-no_corrupt=lost
public class statistic {
	public double original_a=0;
	public double original_ci=0;
	public double []original;//2
	public double retransmit_a=0;
	public double retransmit_ci=0;
	public double []retransmit;//1
	public double ack_a=0;
	public double ack_ci=0;
	public double []ack;//1
	public double corrupt_a=0;
	public double corrupt_ci=0;
	public double []corrupt;//1
	public double rtt_a=0;//?
	public double rtt_ci=0;
	public double []rtt;//1//indone
	public double performance_a=0;
	public double performance_ci=0;
	public double []performance;//indone
	public double delay_a=0;
	public double delay_ci=0;
	public double []delay;
	public double goodput_a=0;
	public double goodput_ci=0;
	public double []goodput;
	public double tput_a=0;
	public double tput_ci=0;
	public double []tput;
	//public int normal=0;
	public int all=0;//4
	public int all_s=0;//3
	//public int all_nor;//1
	public int firstacked=0;//1,already acked packets
	public int nore=0;
	public int n=0;
	public int t=0;
	public int g=0;
	statistic(int n){
		original=new double[n];//++
		retransmit=new double[n];//++
		ack=new double[n];//++
		corrupt=new double[n];//++
		rtt=new double[n];// /all_nore,change the apcket_ds to include re
		performance=new double[n];// /acked
		delay=new double[n];// /original
		goodput=new double[n];//original /time
		tput=new double[n];// all_s/time
		this.n=n;
	}
		
	public void psedoinit(){		
		all=0;
		all_s=0;
		firstacked=0;//1,already acked packets
		nore=0;
		t=0;
		g=0;
	}
	
	public void calculate_round(int i){
		original_a+=original[i];
		retransmit_a+=retransmit[i];
		ack_a+=ack[i];
		corrupt_a+=corrupt[i];
		rtt_a+=rtt[i];
		performance_a+=performance[i];
		delay_a+=delay[i];
		goodput_a+=goodput[i];
		tput_a+=tput[i];
	}
	
	public void calculate_final(){
		original_a=math_yt.Divide(original_a, n);
		retransmit_a=math_yt.Divide(retransmit_a, n);
		ack_a=math_yt.Divide(ack_a, n);
		corrupt_a=math_yt.Divide(corrupt_a, n);
		rtt_a=math_yt.Divide(rtt_a, n);
		performance_a=math_yt.Divide(performance_a, n);
		delay_a=math_yt.Divide(delay_a, n);
		goodput_a=math_yt.Divide(goodput_a, n);
		tput_a=math_yt.Divide(tput_a, n);
		
		original_ci=math_yt.Calculate_ci(math_yt.Calculate_S(n,original_a,original),n);
		retransmit_ci=math_yt.Calculate_ci(math_yt.Calculate_S(n,retransmit_a,retransmit),n);
		ack_ci=math_yt.Calculate_ci(math_yt.Calculate_S(n,ack_a,ack),n);
		corrupt_ci=math_yt.Calculate_ci(math_yt.Calculate_S(n,corrupt_a,corrupt),n);
		
		if(rtt_a==Double.POSITIVE_INFINITY)
			rtt_ci=Double.NaN;
		else
			rtt_ci=math_yt.Calculate_ci(math_yt.Calculate_S(n,rtt_a,rtt),n);
		
		if(performance_a==Double.POSITIVE_INFINITY)
			performance_ci=Double.NaN;
		else
			performance_ci=math_yt.Calculate_ci(math_yt.Calculate_S(n,performance_a,performance),n);
		
		delay_ci=math_yt.Calculate_ci(math_yt.Calculate_S(n,delay_a,delay),n);
		
		if(goodput_a==Double.POSITIVE_INFINITY)
			goodput_ci=Double.NaN;
		else
			goodput_ci=math_yt.Calculate_ci(math_yt.Calculate_S(n,goodput_a,goodput),n);
		
		tput_ci=math_yt.Calculate_ci(math_yt.Calculate_S(n,tput_a,tput),n);
	}
	
	public void print_r(int i){
		System.out.println("number of orginal packets transmitted:"+original[i]);
		System.out.println("number of retransmitted packets:"+retransmit[i]);
		System.out.println("number of ack packets:"+ack[i]);
		System.out.println("number of corrupted packets:"+corrupt[i]);
//		System.out.println("average rtt:"+rtt[i]);
		System.out.println("performance time:"+performance[i]);
/*		System.out.println("delay :"+delay[i]);
		System.out.println("goodput:"+goodput[i]);
		System.out.println("throughput:"+tput[i]);
		System.out.println("all:"+all);
		System.out.println("first_ack:"+firstacked);*/
		System.out.println("*****************************");
	}
	
	public void print_s(){
		System.out.println("number of orginal packets transmitted:"+original_a);
		System.out.println("number of retransmitted packets:"+retransmit_a);
		System.out.println("number of ack packets:"+ack_a);
//		System.out.println("number of corrupted packets:"+corrupt_a);
//		System.out.println("average rtt:"+rtt_a);
		System.out.println("performance time:"+performance_a);
/*		System.out.println("delay :"+delay_a);
		System.out.println("goodput:"+goodput_a);
		System.out.println("throughput:"+tput_a);
		System.out.println("all:"+all);*/
	}
}
