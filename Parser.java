package jjTool;
import java.text.*;
import java.util.*;

public class Parser
{
//int
	public static int toInt(String str)
	{	return toInt(str, 0, null, null); }
	public static int toInt(String str, int ifNull)
	{	return toInt(str, ifNull, null, null); }
	public static int toInt(String str, int ifNull, Integer min, Integer max)
	{
		int i=ifNull;
		if(str!=null)
		{	try
			{	i=Integer.parseInt(str.trim().replace(",", "").split("\\.")[0], 10);
				if(min!=null && min<=max && i<min) i=min;
				if(max!=null && max>=min && i>max) i=max;
			} 
			catch(NumberFormatException e)
			{	i=ifNull; }
		}
		return i;
	}
	
	public static int[] toInts(String[] col1)
	{	return toInts(col1, 0, null, null); }
	public static int[] toInts(String[] col1, int ifNull)
	{	return toInts(col1, 0, null, null); }
	public static int[] toInts(String[] col1, int ifNull, Integer min, Integer max)
	{
		if(col1==null) return new int[0];
		int[] col2=new int[col1.length];
		for(int i=0; i<col1.length; i++)
			col2[i]=toInt(col1[i], ifNull, min, max);
		return col2;
	}

	

//long
	public static long toLong(String str)
	{	return toLong(str, 0, null, null); }
	public static long toLong(String str, long ifNull)
	{	return toLong(str, ifNull, null, null); }
	public static long toLong(String str, long ifNull, Long min, Long max)
	{
		long i=ifNull;
		if(str!=null)
		{	try
			{	
			//	i=Long.parseLong(str.trim().split("\\.")[0], 10);
				i=Long.parseLong(str.trim());
				if(min!=null && min<=max && i<min) i=min;
				if(max!=null && max>=min && i>max) i=max;
			} 
			catch(NumberFormatException e)
			{	i=ifNull; }
		}
		return i;
	}

//float
	public static float toFloat(String str)
	{	return (float)toDouble(str, 0.0, null, null); }
	public static float toFloat(String str, Double ifNull)
	{	return (float)toDouble(str, ifNull, null, null); }
	public static float toFloat(String str, Double ifNull, Double min, Double max)
	{	return (float)toDouble(str, ifNull, min, max); }

	public static double toDouble(String str)
	{	return toFloat(str, 0.0, null, null); }
	public static double toDouble(String str, Double ifNull)
	{	return toFloat(str, ifNull, null, null); }
	public static double toDouble(String str, Double ifNull, Double min, Double max)
	{
		Double i=ifNull;
		if(str!=null)
		{	try
			{	i=Double.parseDouble(str.trim());
				if(min!=null && i<min) i=min;
				if(max!=null && i>max) i=max;
			} 
			catch(NumberFormatException e)
			{	i=ifNull; }
		}
		return i;
	}

//Date
	public static Date toDate(String str)
	{ return toDate(str, null, "yyyy-MM-dd", 0); }
	public static Date toDate(String str, Date ifNull)
	{ return toDate(str, ifNull, "yyyy-MM-dd", 0); }
	public static Date toDate(String str, Date ifNull, String format)
	{ return toDate(str, ifNull, format, 0); }
	public static Date toDate(String str, Date ifNull, int mode)
	{ return toDate(str, ifNull, "yyyy-MM-dd", mode); }
/**
@param str (String) Input date string.
@param ifNull (Date) Default date value when fall.
@param format (Strinf) Useless.....
@param mode (int) 0:auto, 1:AC year, 2:TW year.
@return (Date) Parsed Date value.
*/
	public static Date toDate(String str, Date ifNull, String format, int mode)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat(format);
		Date i=new Date();
		
		try
		{
			if(str==null) return ifNull;
			int d1[]=new int[6];

			String str2[]=str.trim().split("[\\D]+");
			if(str2[0].equals("")) return ifNull;
			for(int k=0; k<str2.length&&k<6; k++)	d1[k]=Integer.parseInt(str2[k]);
			
			if(d1[0]<0) {}
			else if(d1[0]<60 && mode!=2) d1[0]+=2000; // AC
			else if(d1[0]<500 && mode!=1) d1[0]+=1911; // TW

			//i = sdf.parse(d1[0]+"-"+d1[1]+"-"+d1[2]+" "+d1[3]+":"+d1[4]+":"+d1[5]);
			i = sdf2.parse(str);
//		i=sdf.format(date1); //Date>String
		}
		catch(Exception e)
		{	i = ifNull;	}
		return i;
	}


	public static Date smartDate(String val) throws Exception
	{
		String d[]=val.split("[\\D]+"), dStr="";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		if(d.length==1 && d[0].length()>=6)
		{
			switch(d[0].length())
			{
				case 14: // yyyyMMddHHmmss
				case 8: // yyyyMMdd
					break;
				case 11: // tw yyyMMddHHmmss
				case 7: // tw yyyMMdd
					break;
				case 12: // yyMMddHHmmss
				case 6: // yyMMdd
			}
			
			int y=toInt(d[0]);
			int dd=y%100;
			y/=100;
			int mm=y%100;
			y/=100;
			if(y<1000)	y+=1911;
			dStr=y+"-"+mm+"-"+dd+" 00:00:00";
		}
		else if(d.length==2 && d[0].length()>=6 && d[1].length()>=4)
		{
			int y=toInt(d[0]);
			int dd=y%100;
			y/=100;
			int MM=y%100;
			y/=100;
			if(y<1000)	y+=1911;
			
			int t=toInt(d[1]);
			int ss=t%100;
			t/=100;
			int mm=t%100;
			int hh=t/100;
			dStr=y+"-"+MM+"-"+dd+" "+hh+":"+mm+":"+ss;
			
		}
		else if(d.length>=3 && d.length<=6)
		{
			String a[]={"", "-", "-", " ", ":", ":"}, b[]={"1900", "01", "01", "00", "00", "00"};
			int l=0;
			if(val.length()>0)
			{
				for(; l<d.length; l++)
				{
					int c=toInt(d[l]);
					if(l==0 && c<300)	c+=1911;
					dStr+=a[l]+c;
				}
			}
			for(; l<6; l++)	dStr+=a[l]+b[l];
		}
		else
		{
			throw new Exception("smartDate Err: Out of format! ["+val+"]");
		}
		
		try
		{ return sdf.parse(dStr); }
		catch(Exception e)
		{	throw new Exception("smartDate.Err: ["+val+"] "+e);	}
	}

	public static long toLongIP(String inp)
	{
		if(inp==null) return -1;
		String ips[] = inp.split("\\.");
		if(ips.length!=4) return -2;
		long userIP0=0;
		for(int i=0; i<ips.length; i++)
		{
			int a=Parser.toInt(ips[i], -1);
			if(a==-1) return -3;
			userIP0 = (userIP0<<8) + a;
		}
		return userIP0;
	}














}

