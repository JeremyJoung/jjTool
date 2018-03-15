package jjTool;

import java.sql.*;
import java.util.*;



/**
 * @author      Jeremy Joung &lt; jeremy.joung@gmail.com &gt;
 承接ResultSet提供強化功能
 
 * @version     2018/01/31
 * @since       2016/12/31
 */
public class RsTool
{
/** Orininal ResultSet data object. */
	public ResultSet RS=null;
/** Throw Exception or ignore. (Defaulted: false) */
	public boolean throwsErr=false;
/** Error/Exception log between execute. */
	public String errMsg="";
//	public StringBuffer runLog=new StringBuffer();
//	public String colSplit=" - ";


	public RsTool(ResultSet rs1) throws Exception
	{
		if(rs1==null) // || rs1.isAfterLast()) 
		{	throw new Exception("RS is null!!"); }
		RS=rs1;
	}

// ResultSet basic

/** From ResultSet.
@return	(boolean) RS have next row.
@throws	SQLException	Orininal SQLException.
@see java.sql.ResultSet */
	public boolean next() throws SQLException
	{	return RS.next(); }

/** From ResultSet.
@param col	(String) RS column name.
@return	RS return value.
@throws	SQLException	Orininal SQLException.
@see java.sql.ResultSet */
	public String getString(String col) throws SQLException
	{	return RS.getString(col); }
	
/** @return String but never [null]
@param col	(String) RS column name.
@return	RS return value replace null as "".
@throws	SQLException	Orininal SQLException.
@see java.sql.ResultSet */
	public String getStringF(String col) throws SQLException
	{	String t;
		return (t=RS.getString(col))==null?"":t; }
	
/** From ResultSet.
@param col	(String) RS column name.
@return	RS return value.
@throws	SQLException	Orininal SQLException.
@see java.sql.ResultSet */
	public int getInt(String col) throws SQLException
	{	return RS.getInt(col); }
	
/** From ResultSet.
@param col	(String) RS column name.
@return	RS return value.
@throws	SQLException	Orininal SQLException.
@see java.sql.ResultSet */
	public java.sql.Date getDate(String col) throws SQLException
	{	return RS.getDate(col); }
	
/** From ResultSet.
@param col	(String) RS column name.
@return	RS return value.
@throws	SQLException	Orininal SQLException.
@see java.sql.ResultSet */
	public java.sql.Time getTime(String col) throws SQLException
	{	return RS.getTime(col); }
	
/** From ResultSet.
@param col	(String) RS column name.
@return	RS return value.
@throws	SQLException	Orininal SQLException.
@see java.sql.ResultSet */
	public java.sql.Timestamp getTimestamp(String col) throws SQLException
	{	return RS.getTimestamp(col); }
	
/** From ResultSet.
@param col	(String) RS column name.
@return	RS return value.
@throws	SQLException	Orininal SQLException.
@see java.sql.ResultSet */
	public float getFloat(String col) throws SQLException
	{	return RS.getFloat(col); }
	
/** From ResultSet.
@param col	(String) RS column name.
@return	RS return value.
@throws	SQLException	Orininal SQLException.
@see java.sql.ResultSet */
	public double getDouble(String col) throws SQLException
	{	return RS.getDouble(col); }
	
/** From ResultSet.
@param col	(String) RS column name.
@return	RS return value.
@throws	SQLException	Orininal SQLException.
@see java.sql.ResultSet */
	public long getLong(String col) throws SQLException
	{	return RS.getLong(col); }
	
//	public boolean () throws Exception
//	{	return RS.(); }
	

/** Return a SQL format String.
 * @param col	(String) RS column name.
 * @return		(String) SQL format string.
*/
	public String getSqlString(String col)
	{
		try
		{
			if(RS.isAfterLast()) return "sqlVal.err: RS after Last!";
			String s=RS.getString(col);
			if(s==null) s="";
			return s.replace("'", "''");
		}
		catch(Exception e)
		{	return "sqlVal.err: ["+col+"]: "+e.toString();	}
	}

/** Return a Javascript format String.
 * @param col	(String) SQL columns name.
 * @param canNull	(boolean) How will the sql.NULL value do. <br>
 * true: output JSON null. <br>
 * flase: output "".
 * @return	String with a Javascript format.
*/
	public String getJsString(String col, boolean canNull)
	{
		if(col==null || col.equals(""))  return "Col null!!";
		try
		{	return StrTool.jsVal(RS.getString(col), canNull);	}
		catch(Exception e)
		{	return e.toString(); }
	}

	public String getJsObjs(String[] cols)
	{
		int i=0;
		try
		{
			if(RS.isAfterLast()) return "getJsObjs.err: RS after Last!";
			if(cols.length==0) return "";
			StringBuffer str=new StringBuffer();
			for(; i<cols.length; i++)
			{
				if(cols[i]==null || cols[i].equals(""))	continue;
				String t[]=cols[i].split(":");
				str.append((i%5==0?"\n":"")+", \""+t[0].replace("\"", "")+"\":");
				if(t.length==1)
					str.append(StrTool.jsValQ(RS.getString(t[0]), true));
				else if(t[1].equals("I"))
					str.append(RS.getInt(t[0]));
				else if(t[1].equals("F"))
					str.append(RS.getDouble(t[0]));
//				else if(t[1].equals("I"))
//					str.append();
				else 
					str.append(StrTool.jsValQ(RS.getString(t[0]), true));;
			}
			return str.length()>0?str.substring(3):"";
		}
		catch(Exception e)
		{	return "\"error\":"+StrTool.jsValQ("getJsObjs.Err["+i+": "+cols[i]+"]: "+e.toString());	}
	}

	public String getJsArray(String[] cols)
	{	return getJsArray(cols, false);	}
	public String getJsArray(String[] cols, boolean canNull)
	{
		int i=0;
		try
		{
			if(RS.isAfterLast()) return "getJsArray.err: RS after Last!";
			if(cols.length==0) return "[]";
			StringBuffer str=new StringBuffer();
			for(; i<cols.length; i++)
			{
				str.append((i%5==0?"\n":"")+", ");
				
				String t[]=cols[i].split(":");
				if(t.length==1)
					str.append(StrTool.jsValQ(RS.getString(t[0]), canNull));
				else if(t[1].equals("I"))
					str.append(RS.getInt(t[0]));
				else if(t[1].equals("F"))
					str.append(RS.getDouble(t[0]));
//				else if(t[1].equals("I"))
//					str.append();
				else 
					str.append(StrTool.jsValQ(RS.getString(t[0]), canNull));;
			}
			return "["+(str.length()>0?str.substring(2):"")+"]";
		}
		catch(Exception e)
		{	return "["+StrTool.jsValQ("getJsArray.Err["+i+": "+cols[i]+"]: "+e.toString())+"]";	}
	}

//toArray
	public String[] toArray(ResultSet RS, String[] cols)
	{
		int i=0;
		try
		{
			if(RS==null || RS.isAfterLast())
			{	return new String[]{"toArray.err!! [rs null!!]"};	}
			if(cols==null)
			{	return new String[]{"toArray.err!! [col null!!]"};	}
			
			String str[]=new String[cols.length];
			
			for(; i<cols.length; i++)
				str[i]=RS.getString(cols[i]);
			return str;
		}
		catch(Exception e)
		{	return new String[]{"toArray.err!! ["+i+": "+cols[i]+"]: "+e.toString()};	}
	}

/** Default attr for &lt;input&gt; tag. */
	public String inpDef="";

//rsInput
	public String getInput(String col)
	{	return getInput(col, inpDef);	}
	public String getInput(String col, String attr)
	{
		try
		{
			String t[]=col.split("&");
			if(RS.isClosed()) return "RS null!";
			return HtmlTool.Input.text((t.length==1?t[0]:t[1]), RS.getString(t[0]), attr);
		}
		catch(Exception e)
		{	return "getInput.err ["+col+"]: "+e.toString();	}
	}

	public String sqlCheckB(String col)
	{	return sqlCheckB(col, col, col); }
	public String sqlCheckB(String col, String txt)
	{	return sqlCheckB(col, col, txt); }
	public String sqlCheckB(String col, String name, String txt)
	{
		try
		{
			if(RS==null)	return "sqlCheckB.Err: RS null!";
			return HtmlTool.Checkbox.getCheck(name, "1", txt, (RS.getBoolean(col)?"1":""));
		}
		catch(Exception e)
		{	return "sqlCheckB.Err!!["+col+", "+e.toString()+"]";	}
	}

	
	
/**
Do "SELECT LAST_INSERT_ID()"<br>
For MySQL only.

@param stmt The origin [Statement] which want to get inserted key.
@return key with int type.
*/
	static public int getInsertID(Statement stmt) 
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
	
	static public Integer insertedId(Statement stmt) throws Exception
	{	return insertedKey(stmt); }
	static public Integer insertedKey(Statement stmt) throws Exception
	{
		try
		{
			ResultSet RS=stmt.getGeneratedKeys();
			if(RS.next())
				return RS.getInt(1);
			else return null;
			
		}
		catch(Exception e)
		{
			return null;
		}
	}

	
}