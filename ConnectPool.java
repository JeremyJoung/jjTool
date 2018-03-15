package jjTool;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

class ConnectPool
{
	static int appData[]=null;
	static Connection contPool[]=null;
	static Date contTimeL[]=null, contTimeL2[]=null;
	static public int contPoolSize=32, connTimeout=600000;
	
	String SQL_CMD=null, ac=null, ps=null;
	public JspIO jsp=null;
	
	public ConnectPool(String cmd0, JspIO jsp0, String ac0, String ps0) throws Exception
	{
		if(cmd0==null || cmd0.equals(""))
			throw new SQLException("ConnectPool.Exception: sql connection string null!!");
		if(jsp==null)
		{
			throw new Exception("ConnectPool.Exception: JspIO object null!!");
		}
		SQL_CMD=cmd0;
		ac=ac0;
		ps=ps0;
		jsp=jsp0;
	}
	
	public synchronized Connection getCont() throws Exception
	{
		Connection cont1=null;
		boolean f=jsp.reqInt("FORCE2")==1;
		appData=(int[])jsp.getApp("appData"); 
		int lim=4;
		Date d=new Date();
		if(appData==null || f)
		{
			jsp.setApp("appData", appData=new int[20]);
			jsp.setApp("contTime", new Date());
			appData[1]=2;
		}
		lim=Math.min(appData[1], contPoolSize)-1;
		
		contPool=(Connection[])jsp.getApp("contPool");
		contTimeL=(Date[])jsp.getApp("contTimeL");
		contTimeL2=(Date[])jsp.getApp("contTimeL2");
		if(contPool==null || contPool.length<contPoolSize || f)
		{
			jsp.setApp("contTimeL", contTimeL=new Date[contPoolSize]);
			jsp.setApp("contTimeL2", contTimeL2=new Date[contPoolSize]);
			jsp.setApp("contPool", contPool=new Connection[contPoolSize]);
		}

		int c=(appData[0]++)&lim;
		
		if(c==0 && contTimeL[1]!=null) //縮放
		{
			if(lim>1 && contTimeL2[1].getTime()+20000<d.getTime())
				appData[1]>>=1;
			else if(lim<contPoolSize-1 && contTimeL2[1].getTime()+1000>d.getTime())
				appData[1]<<=1;
		}
		
		cont1=contPool[c];
		if(cont1==null || cont1.isClosed() || (contTimeL2[1]!=null && contTimeL2[1].getTime()+connTimeout<d.getTime()) || f)
		{
			contTimeL[c]=d;
			try{ if(cont1!=null) cont1.close(); } catch(Exception e){}
			contPool[c]=cont1=DriverManager.getConnection(SQL_CMD);
		}
		Statement stmt0=null;
		try
		{
			stmt0=cont1.createStatement();
			ResultSet RS=stmt0.executeQuery("SHOW TABLES");
			stmt0.close();
		}
		catch(Exception ex)
		{
			cont1.close();
			contPool[c]=cont1=null;
			contTimeL[c]=d;
			contPool[c]=cont1=DriverManager.getConnection(SQL_CMD);
		}

		contTimeL2[c]=d;
		
		return cont1;
	}
}