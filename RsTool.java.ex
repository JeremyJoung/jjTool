package jjTool;

import java.sql.*;
import java.util.*;

public class RsTool2 extends ResultSet
{
//	public ResultSet RS=null;
	public boolean err=false;
	public String errMsg="";
	public StringBuffer runLog=new StringBuffer();
	public String colSplit=" - ";


	RsTool2(ResultSet rs1) throws Exception
	{
		if(rs1==null || rs1.isClosed() || rs1.isAfterLast()) 
		{
			throw new Exception("RS is null!!");
		}

//		this=rs1;
	}

	
//sqlVal
	public String sqlVal(String col)
	{
		try
		{
			if(isAfterLast()) return "sqlVal.err: RS after Last!";
			String s=getString(col);
			if(s==null) s="";
			return s.replace("'", "''");
		}
		catch(Exception e)
		{	return "sqlVal.err: ["+col+"]: "+e.toString();	}
	}

//jsVal

	public String jsVal(String col, boolean canNull)
	{
		if(col==null || col.equals(""))  return "Col null!!";
		try
		{
			if(getString(col)==null)
				return canNull?"null":"";
			else
				return StrTool.jsVal(super.getString(col));
		}
		catch(Exception e)
		{	return e.toString(); }
	}

//toArray
	public String[] toArray(String[] cols)
	{
		int i=0;
		try
		{
			if(parent.isAfterLast())
			{	return new String[]{"toArray.err!! [after last!!]"};	}
			if(cols==null)
			{	return new String[]{"toArray.err!! [col null!!]"};	}
			
			String str[]=new String[cols.length];
			
			for(; i<cols.length; i++)
				str[i]=parent.getString(cols[i]);
			return str;
		}
		catch(Exception e)
		{
			String err[]={"toArray.err!! ["+cols[i]+"]: "+e.toString()};
			return err;
		}
	}

/**
for MySQL only.
*/
	static public int insertID(Statement stmt) 
	{
		try
		{
			if(stmt==null || stmt.isClosed())	return -2;
			ResultSet RS = stmt.executeQuery("SELECT LAST_INSERT_ID()");
			if(RS.next())	return RS.getInt(1);
			else return -1;
		}
		catch(Exception ex)
		{
			return -3;
		}
	}
}