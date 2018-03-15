package jjTool;
import java.sql.*;
import java.util.*;

class RsTab
{
	boolean init=false;
	public ResultSet _RS;
	public ResultSetMetaData rsmd;
	
	public int[][] colMetaA;
	public HashMap<String, int[]> colMetaM;
	
	public RsTab(ResultSet RS1) throws Exception
	{
		if(RS1==null)
			throw new Exception("RS is null!!");
		else if(RS1.isClosed())
			throw new Exception("RS closed!!");
		else
		{
			_RS=RS1;
			rsmd = _RS.getMetaData();
			colMetaM=new HashMap<String, int[]>();
			
			int n=rsmd.getColumnCount();
			colMetaA=new int[n+1][];
		
			for(int i=1; i<=n; i++) //¼g¤Jmatadata ®æ¦¡
				colMetaM.put(rsmd.getColumnName(i),
				 (colMetaA[i]=new int[]{i, rsmd.getColumnType(i), rsmd.getColumnDisplaySize(i)}));
				
//rsmd.getColumnTypeName(i)
			init=true;
		}
	}
	
	public String isActive()
	{
		try
		{
			if(_RS==null)
				return "[RS null!!]";
			else if(_RS.isClosed())
				return "[RS closed!]";
			else if(_RS.isAfterLast())
				return "[RS EOF!]";
			else return null;
			
		}
		catch(Exception e)
		{	return "isActive.Err: "+e.toString();	}
	}
	
	public String toJSON(String col)
	{	
		int[] t=colMetaM.get(col);//[n, type, size]
		if(t==null) return "column miss!["+col+"] ";
		return this.toJSON(col, _RS); 
	}
	
	
	public String toJSON(String col, ResultSet RS)
	{
		try
		{
			ResultSetMetaData rsmd0=RS.getMetaData();
			int n=0, N=rsmd0.getColumnCount();
			for(; n<N; n++)
				if(rsmd0.getColumnName(n).equals(col)) break;
			if(n==N) return "column miss!["+col+"] ";
			String r="";
			if(RS.getString(col)==null)	return "\"\"";
			switch(rsmd0.getColumnType(n))
			{
				//int
				case java.sql.Types.INTEGER:
				case java.sql.Types.BIGINT:
				case java.sql.Types.TINYINT:
					r=RS.getLong(col)+"";
					break;
				case java.sql.Types.DECIMAL: 
					r=RS.getString(col)+"";
					break;
				//double
				case java.sql.Types.FLOAT:
				case java.sql.Types.DOUBLE:
					r=RS.getDouble(col)+"";
					break;
				//date
				case java.sql.Types.TIME:
				case java.sql.Types.DATE:
				case java.sql.Types.TIMESTAMP:
					//r="new Date(\""+RS.getTimestamp(col)+"\")";
					r="\""+RS.getTimestamp(col)+"\"";
					break;
				//bool
				case java.sql.Types.BOOLEAN:
					r=RS.getBoolean(col)+"";
					break;
				//string
				case java.sql.Types.VARCHAR:
				case java.sql.Types.LONGVARCHAR:
				case java.sql.Types.LONGNVARCHAR:
				case java.sql.Types.NVARCHAR:
				case java.sql.Types.CHAR:
				default:
					r="\""+RS.getString(col).replace("'", "\\'").replace("\"", "\\\"").replace("\n", "\\n")+"\"";
					break;
				
//				case java.sql.Types.:
			}
			
			return r;
//			return RS.getString(col);
			
		}
		catch(Exception e)
		{	return "toJSON.Err: "+e.toString();	}
	}
	
	public String toJSONArr(String[] cols)
	{
		if(cols==null || cols.length==0) return "[]";
		StringBuilder r=new StringBuilder();
		for(int i=0; i<cols.length; i++)
			r.append(", "+toJSON(cols[i]));
		
		return "["+r.substring(2)+"]";
	}
	public String toJSONObj(String[] cols)
	{
		if(cols==null || cols.length==0) return "{}";
		StringBuilder r=new StringBuilder();
		for(int i=0; i<cols.length; i++)
			r.append(", '"+cols[i]+"':"+toJSON(cols[i]));
		
		return "{"+r.substring(2)+"}";
	}
	
	public String showMetaData() throws Exception
	{
		if(rsmd==null) return "[null]";
		int n=rsmd.getColumnCount();
		StringBuilder str=new StringBuilder();
		
		for(int i=1; i<=n; i++)
		{
			str.append(i+", "+rsmd.getColumnName(i)+": ["+rsmd.getColumnType(i)+"]"+rsmd.getColumnTypeName(i)+", "+rsmd.getColumnDisplaySize(i)+"\n");
		}
		
		return str.toString();
	}
	
	///
	public String toTd(String[][] cols)
	{	return toTd(cols, _RS);	}
	public String toTd(String[][] cols, ResultSet RS)
	{
		if(RS==null || cols==null)
			return "";
		StringBuilder str=new StringBuilder();
		
		try
		{
			for(int i=0; i<cols.length; i++)
			{
				String attb="", nm=cols[i][1]
				, v=RS.getString(cols[i][0])==null?"":RS.getString(cols[i][0]);
				
				if(cols[i].length>2)
				{
					String cmd=","+cols[i][2]+",";
					if(cmd.indexOf(",H,")!=-1 || cmd.indexOf(",HIDE,")!=-1) continue;
					if(cmd.indexOf(",A:L,")!=-1) attb+=" align='left'";
					else if(cmd.indexOf(",A:C,")!=-1) attb+=" align='center'";
					else if(cmd.indexOf(",A:R,")!=-1) attb+=" align='right'";
					
					int w=Parser.toInt(StrTool.cutStr(cmd, ",W:", ","), -1);
					if(w>=0) attb+=" width="+w;
					
					if((cmd.indexOf(",R,")!=-1 || cmd.indexOf(",HTML,")!=-1) && cols[i].length>3)
						v=cols[i][3].replace("$VAL$", v).replace("$NAME$", nm);
						
				}
				str.append("<td"+attb+">"+v+"</td>");
			}
		}
		catch(Exception e)
		{
			return "Err: "+e.toString();
		}
		
		return str.toString();
	}
	
	String getTh(String[][] cols)
	{
		if(cols==null)	return "";
		StringBuilder str=new StringBuilder();
		for(int i=0; i<cols.length; i++)
		{
			String cmd=cols[i].length>2?","+cols[i][2]+",":"";
	
			if(cols[i].length>2 && (cmd.indexOf(",HIDE,")!=-1 || cmd.indexOf(",H,")!=-1)) continue;
			String attb="";
			if(cols[i].length>2)
			{
				int w=Parser.toInt(StrTool.cutStr(cmd, ",W:", ","), -1);
				if(w>=0) attb+=" width="+w;
			}
			str.append("<th"+attb+">"+cols[i][1]+"</th>");	
		}
		return str.toString();
	}
}
