package PA2;

import java.math.BigDecimal;
import java.util.Random;

public class math_yt {
	public static double Subtract(double _a,double _b){//use big decimal to assure the accurancy
		if(_a==Double.POSITIVE_INFINITY||_b==Double.POSITIVE_INFINITY)
			return Double.POSITIVE_INFINITY;
		else if(_b==Double.NaN||_a==Double.NaN)
			return Double.NaN;
		BigDecimal a=new BigDecimal(Double.toString(_a));
		BigDecimal b=new BigDecimal(Double.toString(_b));	
		return a.subtract(b).doubleValue();
	}
	
	public static double Add(double _a,double _b){
		if(_a==Double.POSITIVE_INFINITY||_b==Double.POSITIVE_INFINITY)
			return Double.POSITIVE_INFINITY;
		else if(_b==Double.NaN||_a==Double.NaN)
			return Double.NaN;
		BigDecimal a=new BigDecimal(Double.toString(_a));
		BigDecimal b=new BigDecimal(Double.toString(_b));
		return a.add(b).doubleValue();
	}
	
	public static double Multiply(double _a,double _b){
		if(_a==Double.POSITIVE_INFINITY||_b==Double.POSITIVE_INFINITY)
			return Double.POSITIVE_INFINITY;
		else if(_b==Double.NaN||_a==Double.NaN)
			return Double.NaN;
		BigDecimal a=new BigDecimal(Double.toString(_a));
		BigDecimal b=new BigDecimal(Double.toString(_b));
		return a.multiply(b).doubleValue();
	}
	
	public static double Divide(double _a,double _b){
		if(_a==0||_b==0)return 0;
		if(_a==Double.POSITIVE_INFINITY||_b==Double.POSITIVE_INFINITY)
			return Double.POSITIVE_INFINITY;
		else if(_b==Double.NaN||_a==Double.NaN)
			return Double.NaN;
		BigDecimal a=new BigDecimal(Double.toString(_a));
		BigDecimal b=new BigDecimal(Double.toString(_b));
		return a.divide(b,10,BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	public static double Calculate_S(int n,double aver,double[]record){// calculate standard variance
		double total=0;
		double s=0;
		for(int i=0;i<n;i++){
			total=total+Math.pow((aver-record[i]),2);
		}
		return Math.sqrt(Divide(total,n-1));
	}
	
	public static double Calculate_ci(double s,int n){//calculate the confidence interval, use z distribution
		double z_a_fenzhi_2=1.645;
		return Multiply(z_a_fenzhi_2,Divide(s,Math.sqrt(n)));
	}
	
	public static double Generate_expnum(Random r,double lamda){//generate numbers that satisfy exponential distribution
		return Multiply(Multiply(Divide(1,lamda),-1),Math.log(r.nextDouble()));
		
	}
	
	public static double Setscale(double _a){//round up the number to specific accurancy
		BigDecimal a=new BigDecimal(_a);
		return a.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}

