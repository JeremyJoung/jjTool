package jjTool;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.ServletContext;
import java.util.*;
import java.text.*;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class JspIO
{
	public HttpServletRequest req	=null;
	public HttpServletResponse resp	=null;
	public JspWriter out	= null;
	public HttpSession ses	= null;
	public javax.servlet.ServletContext app=null;
	public javax.servlet.jsp.PageContext page	=null;
	

	public JspIO(HttpServletRequest req0, HttpServletResponse resp0, javax.servlet.jsp.PageContext jsp0)
	{
		req=req0;
		resp=resp0;
		out=jsp0.getOut();
		ses=jsp0.getSession();
		app=jsp0.getServletContext();
		page=jsp0;
	}
	
	public JspIO(HttpServletRequest req0, HttpServletResponse resp0, JspWriter out0, HttpSession ses0, ServletContext app0)
	{
		req=req0;
		resp=resp0;
		out=out0;
		ses=ses0;
		app=app0;
	}
	
	
	class POST
	{
		
	}
	
	
/** Abbreviation of the [ ServletRequest.getParameter(String) ] method. 
@param name	(String) The same as request.getParameter(columnName).
@return	(String) Parameter String value.
*/
	public String req(String name)
	{	return req.getParameter(name); }
	
	
// ----------------------------------------------------------- number
	public int defIntVal=0;
	public int reqInt(String name)
	{	return reqInt(name, defIntVal, null, null); }
	public int reqInt(String name, int ifNull)
	{	return reqInt(name, ifNull, null, null); }
/**
Get int from request with default value.<p>

Get int from request. <br>
When parameter is null will give a customize default value or auto 0.<br>
And it can limit the min and max value by optionally.<br>
The method will <b>never throw any Exception</b>. All possible Exception will become default value.

@param	name		(String) Column name of the request parameter.
@param	ifNull	(String) Default value when the parameter is null or unparseable. [optionally]
@param	min	(Integer) Min value of the parameter value [optionally].
@param	max	(Integer) Max value of the parameter value [optionally].
@return	(int) Parameter value or default.
*/
	public int reqInt(String name, int ifNull, Integer min, Integer max)
	{	return Parser.toInt(req.getParameter(name), ifNull, min, max);	}

	public int[] reqInts(String name)
	{	return reqInts(name, defIntVal); }
	public int[] reqInts(String name, int ifNull)
	{
		String vals[] = reqStrings(name);
		int[] col2=new int[vals.length];
		for(int i=0; i<vals.length; i++)
			col2[i]=Parser.toInt(vals[i], ifNull);
		return col2;
	}


	public long defLongVal=0;
	public long reqLong(String name)
	{	return reqLong(name, defLongVal, null, null); }
	public long reqLong(String name, long ifNull)
	{	return reqLong(name, ifNull, null, null); }

/**
Get long from request with default value.<p>

Get long from request. <br>
When parameter is null will give a customize default value or auto 0.<br>
And it can limit the min and max value by optionally.<br>
The method will <b>never throw any Exception</b>. All possible Exception will become default value.

@param	name		(String) Column name of the request parameter.
@param	ifNull	(String) Default value when the parameter is null or unparseable. [optionally]
@param	min	(Long) Min value of the parameter value [optionally].
@param	max	(Long) Max value of the parameter value [optionally].
@return	(long) Parameter value or default.
*/

	public long reqLong(String name, long ifNull, Long min, Long max)
	{	return Parser.toLong(req.getParameter(name), ifNull, min, max);	}

	public long[] reqLongs(String name)
	{	return reqLongs(name, defIntVal); }
	public long[] reqLongs(String name, long ifNull)
	{
		String vals[] = reqStrings(name);
		long[] col2=new long[vals.length];
		for(int i=0; i<vals.length; i++)
			col2[i]=Parser.toLong(vals[i], ifNull);
		return col2;
	}


// float
	public float reqFloat(String name)
	{	return reqFloat(name, defIntVal, null, null); }
	public float reqFloat(String name, double ifNull)
	{	return reqFloat(name, ifNull, null, null); }
	public float reqFloat(String name, double ifNull, Double min, Double max)
	{	return (float)reqDouble(name, ifNull, min, max); }
	
	public double reqDouble(String name)
	{	return reqDouble(name, defIntVal, null, null); }
	public double reqDouble(String name, double ifNull)
	{	return reqDouble(name, ifNull, null, null); }
	public double reqDouble(String name, double ifNull, Double min, Double max)
	{	return Parser.toDouble(req.getParameter(name), ifNull, min, max);	}


	public int reqFlag(String name)
	{	return reqBits(name, 0); }
	public int reqFlag(String name, int def)
	{	return reqBits(name, def); }

	public int reqBits(String name)
	{	return reqBits(name, 0); }
	public int reqBits(String name, int def)
	{
		int[] cols=reqInts(name, 0);
		int r=cols.length==0?def:0;
		for(int i=0; i<cols.length; i++)
			r|=cols[i];
		return r;
	}

// ------------------------------------------------------- String
	public int defStringLen=0;

	public String reqString(String name)
	{	return reqString(name, "", 0); }
	public String reqString(String name, String ifNull)
	{	return reqString(name, ifNull, 0);	}

/**
Get String from request with default value.<p>

Get String from request. <br>
When parameter is null will give a customize default value or auto empty string "".<br>
And it can limit max length of the String by optionally.<br>
The method will <b>never throw any Exception</b>. All possible Exception will become default String.

@param	name		(String) Column name of the request parameter.
@param	ifNull	(String) Default value when the parameter is null [optionally].
@param	mLength	(int) Max String length of the parameter value [optionally].
@return	(String) Parameter value or default.
*/
	public String reqString(String name, String ifNull, Integer mLength)
	{
		String i=ifNull, s=req.getParameter(name);
		if(mLength==null) mLength=0;
		if(s!=null)
			i=mLength>0 && s.length()>mLength ? s.substring(0, mLength) : s;
		return i;
	}


	public String[] reqs(String name)
	{	return reqStrings(name); }
	public String[] reqStrings(String name)
	{
		String val[] = req.getParameterValues(name);
		return val!=null?val:new String[0];
	}

	public String[] reqSqlStrings(String name)
	{
		String val[]=reqStrings(name);
		for(int i=0; i<val.length; i++)
			val[i]=StrTool.sqlVal(val[i]);
		return val; 
	}


	public String reqSqlString(String name)
	{	return reqSqlString(name, "", 0); }

	public String reqSqlString(String name, String ifNull)
	{	return reqSqlString(name, ifNull, 0);	}

/** Get SQL format String from request. 
@param	name		(String) Column name of the request parameter.
@param	ifNull	(String) Default value when the parameter is null or unparseable. [optionally]
@param	mLength	(Integer) Max length limit of returned data. Defaulted unlimit. [optionally]
@return	(String) SQL format srting.
*/
	public String reqSqlString(String name, String ifNull, Integer mLength)
	{	String s=reqString(name, ifNull, mLength);
		return s==null?null:s.replace("\\", "\\\\").replace("'", "''"); 
	}

//Date
	public String defDateVal=null;

	public Date reqDate(String name)
	{	return reqDate(name, null); } 
	public Date reqDate(String name, Date ifNull)
	{	return reqDate(name, ifNull, 0); } 
/** @version 2017/01/13
@param	name	(String) Column name of the request parameter.
@param	ifNull	(Date) Default value when the parameter is null or unparseable. [optionally]
@param	mode	(int) 0:auto, 1:AC year, 2:TW year. Default:0
@return	(Date) Java Date object or null.
 */
	public Date reqDate(String name, Date ifNull, int mode)
	{
		String j=reqString(name, null);
		return Parser.toDate(j, ifNull, mode);
	}

	
//SQL cmd
	public int sqlType=2; //1:MS-SQL, 2:MySQL, 3:Oracle
	public String sqlTag[][]={ {"", ""}, {"[", "]"}, {"`","`"}, {"\"", "\""} };
	
	public String[] reqSqlCmd(String[] colList)  //[sqlCol:type[S,K,I,ST]:def:reqName]
	{	return reqSqlCmd( colList, 0); }
	public String[] reqSqlCmd(String[] colList, int txtLV)  //[col:type[S,K,I,ST]:def:req]
	{
		String cStr="", vStr="", upStr="", wStr="";
		for(int j=0; j<colList.length; j++)
		{
			String tmp[]=colList[j].split(":"), col=sqlTag[sqlType][0]+tmp[0]+sqlTag[sqlType][1]
			, val=reqSqlString(tmp.length>3?tmp[3]:tmp[0], tmp.length>2?tmp[2]:null)
			, md=tmp.length==1?"S":tmp[1];
			
			if(!md.equals("B") && !md.equals("C") && val==null) continue;					//null
			else if(md.equals("S"))
			{
				val=val.trim();
				vStr+=", '"+val+"'";
				upStr+=", "+col+"='"+val+"'";
			}
			else if(md.equals("PS"))					//pure text [keep:' ']
			{
				vStr+=", '"+val+"'";
				upStr+=", "+col+"='"+val+"'";
				
			}
			else if(tmp.length>2 && md.equals("ST")) //ÀRºA
			{
				val=StrTool.sqlVal(tmp[2]);
				vStr+=", '"+val+"'";
				upStr+=", "+col+"='"+val+"'";
			}
			else if(md.equals("KI")) 							//INT KEY
			{
				int val1=Parser.toInt(val, tmp.length>2&&tmp[2].length()>0?Parser.toInt(tmp[2]):0);
				vStr+=", "+val1;
				wStr+=" AND "+col+"="+val1;
			}
			else if(md.indexOf("K")==0)								//SQL KEY
			{
				if(val==null) continue;
				if(md.indexOf("U")>0) val=val.toUpperCase();
				vStr+=", '"+val+"'";
				wStr+=" AND "+col+"='"+val+"'";
				
			}
			else if(md.equals("I")||md.equals("B")||md.equals("C"))				//int:[col, I, def]
			{
				int val1=Parser.toInt(val, tmp.length>2&&tmp[2].length()>0?Parser.toInt(tmp[2]):0);
				vStr+=", "+val1;
				upStr+=", "+col+"="+val1;
			}
			else if(md.equals("F"))
			{
				double val1=Parser.toDouble(val, tmp.length>2&&tmp[2].length()>0?Parser.toDouble(tmp[2]):0.0);
				vStr+=", "+val1;
				upStr+=", "+col+"="+val1;
			}
			else if(md.equals("D"))									//Date
			{
				String val1="null";
				if(val.equals(""))
				{
					val1="null";
				}
				else
				{
					Date dat=null;
					
					try
					{ dat=Parser.smartDate(val); }
					catch(Exception ex){}
					
					switch(sqlType)
					{
						case 0:
							val1="'"+val+"'";
							break;
						case 1:
							break;
						case 2:
							val1="DATE('"+val+"')";
							break;
						case 3:
							break;
					}
				}
				vStr+=", "+val1;
				upStr+=", "+col+"="+val1;
			}
			else
			{
				if(val!=null)
				{
					val=val!=null ? val.trim().replace("'", "''"): "";
					vStr+=", '"+(val)+"'";
					upStr+=", "+col+"='"+val+"'";
				}
			}
			cStr+=", "+col;
		}
		if(!cStr.equals("")) cStr=cStr.substring(2);
		if(!vStr.equals("")) vStr=vStr.substring(2);
		if(!upStr.equals("")) upStr=upStr.substring(2);
		if(!wStr.equals("")) wStr=wStr.substring(4);

		String tmp[]={cStr, vStr, upStr, wStr};
		return tmp;
	}




//------------------------------------------------------------application

/** @version 2017/03/06 */
	public static String appPoolName="appPool";
	public <T> T appObj(String name,T tp)
	{
		try
		{
			if(name==null) return null;
			T ob=(T)app.getAttribute(name);
			if(ob==null)
			{	app.setAttribute(name, ob=((T)tp.getClass().newInstance()));	}
			return (T)ob;
		}
		catch(Exception e) {	return null; }
	}

/** Alias for application.setAttribute() 
@param name	(String) Application attribute name.
@param obj	(Object) Put in object.
@return	(JspIO) JspIO object self.
*/
	public JspIO setApp(String name, Object obj)
	{	app.setAttribute(name, obj);
		return this;
	}
	
/** Alias for application.getAttribute() 
@param name	(String) Application attribute name.
@return	(Object) Application returned object.
*/
	public Object getApp(String name)
	{	return app.getAttribute(name);	}

	public int appInt(String name)
	{	return appInt(name, 0); }

/**
Get int from Application with default value.<p>

Get int from Application. <br>
When attribute is null or unparseable will give a customize default value or initialize 0.<br>
The method will <b>never throw any Exception</b>. All possible Exception will replace to default value.

@param	name		(String) Column name of the Application attribute.
@param	ifNull	(int) Default value when the Application attribute is null or unparseable. Defaulted 0. [optionally]
@return	(int) int data return from Application.
*/
	public int appInt(String name, int ifNull)
	{
		int i=ifNull;
		if(app.getAttribute(name)!=null)
		{
			String c=app.getAttribute(name).getClass().getName();
			
			try
			{
				if(c.equals("java.lang.Integer"))
					i=(Integer)app.getAttribute(name);
				else
					i=Parser.toInt(app.getAttribute(name).toString(), ifNull);
			} 
			catch(Exception e) { }
		}
		return i;
	}

	public long appLong(String name)
	{	return appLong(name, 0l); }
/**
Get long from Application with default value.<p>

Get long from Application. <br>
When attribute is null or unparseable will give a customize default value or initialize 0.<br>
The method will <b>never throw any Exception</b>. All possible Exception will replace to default value.

@param	name		(String) Column name of the Application attribute.
@param	ifNull	(long) Default value when the Application attribute is null or unparseable. Defaulted 0. [optionally]
@return	(long) long data return from Application.
*/
	public long appLong(String name, long ifNull)
	{
		long i=ifNull;
		if(app.getAttribute(name)!=null)
		{
			String c=app.getAttribute(name).getClass().getName();
			
			try
			{
				if(c.equals("java.lang.Long"))
					i=(Long)app.getAttribute(name);
				else
					i=Long.parseLong((String)app.getAttribute(name));
			} 
			catch(Exception e) { }
		}
		return i;
	}

	public String appString(String name)
	{	return appString(name, ""); }
/**
Get String from Application with default value.<p>

Get String from Application. <br>
When attribute is null or unparseable will give a customize default value or initialize "".<br>
The method will <b>never throw any Exception</b>. All possible Exception will replace to default value.

@param	name		(String) Column name of the Application attribute.
@param	ifNull	(String) Default value when the Application attribute is null or unparseable. Default "". [optionally]
@return	(String) Data return from Application.
*/
	public String appString(String name, String ifNull)
	{
		String i=ifNull;
		if(app.getAttribute(name)!=null)
		{
			try
			{	i=app.getAttribute(name).toString(); } 
			catch(Exception e) { }
		}
		return i;
	}

//------------------------------------------------------------------------------app Object
	
TreeMap<Integer, String> appMapI(String name, String sql, String col, String val, Connection Cont0) throws Exception
{ return appMapI(name, sql, col, val, Cont0, false); }
TreeMap<Integer, String> appMapI(String name, String sql, String col, String val, Connection Cont0, boolean force) throws Exception
{
	TreeMap<Integer, String> r=null;
	try
	{	r=(TreeMap<Integer, String>)app.getAttribute(name);	}
	catch(Exception e) {}
	if(r==null || force || reqInt("FORCE")==1)
	{
		Statement stmt=Cont0.createStatement();
		r=DataTool.rsMapI(stmt.executeQuery(sql), col, val);
		stmt.close();
		app.setAttribute(name, r);
	}
	return r;
}

/*
TreeMap<Integer, Integer> appMapII(String name, String sql, String col, String val) throws Exception
{ return appMapI(name, sql, col, val, false); }
TreeMap<Integer, Integer> appMapII(String name, String sql, String col, String val, boolean force) throws Exception
{
	TreeMap<Integer, Integer> r=null;
	try
	{	r=(TreeMap<Integer, Integer>)app.getAttribute(name);	}
	catch(Exception e) {}
	if(r==null || force || reqInt("FORCE")==1)
	{
		Statement stmt=Cont0.createStatement();
		r=DataTool.rsMapII(stmt.executeQuery(sql), col, val);
		stmt.close();
		app.setAttribute(name, r);
	}
	return r;
}*/

TreeMap<String, String> appMapS(String name, String sql, String col, String val, Connection Cont0) throws Exception
{	return appMapS(name, sql, col, val, Cont0, false); }
TreeMap<String, String> appMapS(String name, String sql, String col, String val, Connection Cont0, boolean force) throws Exception
{
	TreeMap<String, String> r=null;
	try { r=(TreeMap<String, String>)app.getAttribute(name);	}
	catch(Exception e) {}
	if(r==null || force || reqInt("FORCE")==1)
	{
		Statement stmt=Cont0.createStatement();
		r=DataTool.rsMapS(stmt.executeQuery(sql), col, val);
		stmt.close();
		app.setAttribute(name, r);
	}
	return r;
}

String[][] appArray2(String name, String sql, String[] val, Connection Cont0) throws Exception
{	return appArray2(name, sql, val, Cont0, false); }
String[][] appArray2(String name, String sql, String[] val, Connection Cont0, boolean force) throws Exception
{
	String[][] r=null;
	try
	{	r=(String[][])app.getAttribute(name);	}
	catch(Exception e) {}
	if(r==null || force || reqInt("FORCE")==1)
	{
		Statement stmt=Cont0.createStatement();
		r=DataTool.rsArray2(stmt.executeQuery(sql), val);
		stmt.close();
		app.setAttribute(name, r);
	}
		return r;
	
}


/*
TreeMap<Integer, Integer> appMapII(String name, String sql, String col, String val, Connection Cont0) throws Exception
{ return appMapI(name, sql, col, val, Cont0, false); }
TreeMap<Integer, Integer> appMapII(String name, String sql, String col, String val, Connection Cont0, boolean force) throws Exception
{
	TreeMap<Integer, Integer> r=null;
	try
	{	r=(TreeMap<Integer, Integer>)app.getAttribute(name);	}
	catch(Exception e) {}
	if(r==null || force || reqInt("FORCE")==1)
	{
		Statement stmt=Cont0.createStatement();
		r=DataTool.rsMapII(stmt.executeQuery(sql), col, val);
		stmt.close();
		app.setAttribute(name, r);
	}
	return r;
}*/


	
	
//------------------------------------------------------------------------------session
/** @version 2016/02/19 */
	static String sesPoolName="sesPool";
	public <T> T sesObj(String col,T obj)
	{
		try
		{
			if(col==null) return null;
			T ob=(T)ses.getAttribute(col);
			if(ob==null)
			{	ses.setAttribute(col, ob=((T)obj.getClass().newInstance()));	}
			return (T)ob;
		}
		catch(Exception e) {	return null; }
	}

/** Alias for session.setAttribute() 
@param col	(String) Session attribute name.
@param obj	(Object) Put in object.
@return	(JspIO) JspIO object self.
*/
	public JspIO setSes(String col, Object obj)
	{	ses.setAttribute(col, obj);
		return this;
	}

/** Alias for session.getAttribute() 
@param col	(String) Session attribute name.
@return	(Object) Session returned object.
*/
	public Object getSes(String col)
	{	return ses.getAttribute(col);	}

	public int sesInt(String col)
	{	return sesInt(col, 0); }
/**
Get int from Session with default value.<p>

Get int from Session. <br>
When attribute is null or unparseable will give a customize default value or initialize 0.<br>
The method will <b>never throw any Exception</b>. All possible Exception will replace to default value.

@param	name		(String) Column name of the Session attribute.
@param	ifNull	(int) Default value when the Session attribute is null or unparseable. Defaulted 0. [optionally]
@return	(int) int data return from Session.
*/
	public int sesInt(String name, int ifNull)
	{
		int i=ifNull;
		if(ses.getAttribute(name)!=null)
		{
			String c=ses.getAttribute(name).getClass().getName();
			
			try
			{
				if(c.equals("java.lang.Integer"))
					i=(Integer)ses.getAttribute(name);
				else
					i=Parser.toInt(ses.getAttribute(name).toString(), ifNull);
			} 
			catch(Exception e) { }
		}
		return i;
	}

	public long sesLong(String name)
	{	return sesLong(name, 0); }
/**
Get long from Session with default value.<p>

Get long from Session. <br>
When attribute is null or unparseable will give a customize default value or initialize 0.<br>
The method will <b>never throw any Exception</b>. All possible Exception will replace to default value.

@param	name		(String) Column name of the Session attribute.
@param	ifNull	(long) Default value when the Session attribute is null or unparseable. Defaulted 0. [optionally]
@return	(long) long data return from Session.
*/
	public long sesLong(String name, long ifNull)
	{
		long i=ifNull;
		if(ses.getAttribute(name)!=null)
		{
			String c=ses.getAttribute(name).getClass().getName();
			
			try
			{
				if(c.equals("java.lang.Long"))
					i=(Long)ses.getAttribute(name);
				else
					i=Long.parseLong((String)ses.getAttribute(name));
			} 
			catch(Exception e) { }
		}
		return i;
	}

	public String sesString(String name)
	{	return sesString(name, ""); }
/**
Get String from Session with default value.<p>

Get String from Session. <br>
When attribute is null or unparseable will give a customize default value or initialize 0.<br>
The method will <b>never throw any Exception</b>. All possible Exception will replace to default value.

@param	name		(String) Column name of the Session attribute.
@param	ifNull	(String) Default value when the Session attribute is null or unparseable. Defaulted 0. [optionally]
@return	(String) String data return from Session.
*/
	public String sesString(String name, String ifNull)
	{
		String i=ifNull;
		if(ses.getAttribute(name)!=null)
		{
			try
			{	i=ses.getAttribute(name).toString(); } 
			catch(Exception e) { }
		}
		return i;
	}


//------------------------------------------------------------------------------req+Ses

/** int cache pool name for Request parameter */
	public String sesIntPool="sesIntPool";
	public int reqSesInt(String name)
	{	return reqSesInt(name, 0); }
/**
Get int from Request or session cache with default value.<p>

Get int from Request parameter and save a session cache. <br>
When parameter is null or unparseable will give a customize default value or initialize 0.<br>
The method will <b>never throw any Exception</b>. All possible Exception will replace to default value.

@param	name	(String) Column name of the Request parameter.
@param	ifNull	(int) Default value when both the Request parameter and session cache are null or unparseable. Defaulted 0. [optionally]
@return	(int) Data return from Request/cache.
*/
	public int reqSesInt(String name, int ifNull)
	{
		int v=ifNull;
		try
		{
			String v0=reqString(name, null);
			TreeMap<String, Integer> p=sesObj(sesIntPool, new TreeMap<String, Integer>());
			if(v0!=null)
				p.put(name, v=reqInt(name));
			else if(p.get(name)!=null)
				v=p.get(name).intValue();
		}
		catch(Exception e) {}
		return v;	
		
	}

/** Save a int Request parameter cache into session.
@param	name	(String) Column name of the Request parameter.
@param val	(int) Inputed cache value.
@return	(JspIO) JspIO object self.
*/
	public JspIO setReqSesInt(String name, int val)
	{
		try
		{
			TreeMap<String, Integer> p=sesObj(sesIntPool, new TreeMap<String, Integer>());
			p.put(name, val);
		}
		catch(Exception e) {}
		return this;
	}

	public String sesLongPool="sesLongPool";
	public long reqSesLong(String name)
	{	return reqSesLong(name, 0); }
	public long reqSesLong(String name, long ifNull)
	{
		long v=ifNull;
		try
		{
			String v0=reqString(name, null);
			TreeMap<String, Long> p=sesObj(sesLongPool, new TreeMap<String, Long>());
			if(v0!=null)
				p.put(name, v=reqLong(name));
			else if(p.get(name)!=null)
				v=p.get(name).longValue();
		}
		catch(Exception e) {}
		return v;
	}

	public JspIO setReqSesLong(String name, long val)
	{
		try
		{
			TreeMap<String, Long> p=sesObj(sesLongPool, new TreeMap<String, Long>());
			p.put(name, val);
		}
		catch(Exception e) {}
		return this;
	}


	public String sesStringPool="sesStringPool";
	public String reqSesString(String name)
	{	return reqSesString(name, ""); }
	public String reqSesString(String name, String ifNull)
	{
		String v=reqString(name, null);
		TreeMap<String, String> p=sesObj(sesStringPool, new TreeMap<String, String>());
		if(v!=null) p.put(name, v);
		else if((v=(String)p.get(name))==null)
			p.put(name, v=ifNull);
		
		return v;
	}

	public JspIO setReqSesString(String name, String val)
	{
		try
		{
			TreeMap<String, String> p=sesObj(sesStringPool, new TreeMap<String, String>());
			p.put(name, val);
		}
		catch(Exception e) {}
		return this;
	}

	public String reqSesSqlString(String name)
	{	return reqSesSqlString(name, ""); }
	public String reqSesSqlString(String name, String ifNull)
	{	return StrTool.sqlVal(reqSesString(name, "")); }



//------------------------------------------------------------------------------attachment

/** Set a Response to attachment Excel (.csv) file with Big5 charset, and give attachment file name.
@param fileName	(String) csv file name.
@version 2017/06/04 
@throws Exception	Any exception.
 */
	public void setCsv(String fileName) throws Exception
	{
		out.clear();
		resp.setContentType("application/vnd.ms-excel; charset=BIG5;");
		resp.setHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode(fileName, "utf8").replace("+", " ")+".csv");

	}

/** Set a Response to attachment Excel(.csv) file with utf8 charset, and give attachment file name.
@param fileName	(String) csv file name.
@version 2017/06/23 
@throws Exception	Any exception.
 */
	public void setCsvU(String fileName) throws Exception
	{
		out.clear();
		resp.setContentType("application/vnd.ms-excel; charset=utf8;");
		resp.setHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode(fileName, "utf8").replace("+", " ")+".csv");
		out.write('\ufeff');

	}

	public void setExcel(String fileName) throws Exception
	{	setExcel(fileName, fileName, 0); }
	public void setExcel(String fileName, String sheetName) throws Exception
	{	setExcel(fileName, sheetName, 0); }
/** Set a Response to attachment HTML Excel(.xls) file with utf8 charset, and give attachment file name.
@param fileName	(String) csv file name.
@param sheetName	(String) Name of first sheet.
@param splitY	(Integer) If more than 1 split sheet row with customize value.
@version 2017/07/14 
@throws Exception	Any exception.
 */
	public void setExcel(String fileName, String sheetName, Integer splitY) throws Exception
	{
		out.clear();
		out.print('\ufeff');//BOM

		resp.setContentType("application/vnd.ms-excel; charset=utf8;");
		resp.setHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode(fileName, "UTF-8").replace("+", " ")+".xls");
		
		out.println(
	"<html xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:x='urn:schemas-microsoft-com:office:excel' xmlns='http://www.w3.org/TR/REC-html40'>\n"+
"<head>\n"+
"<meta name=ProgId content=Excel.Sheet/>\n"+
"<!--[if gte mso 9]><xml>\n"+
" <x:ExcelWorkbook>\n"+
"  <x:ExcelWorksheets>\n"+
"   <x:ExcelWorksheet>\n"+
"    <x:Name>"+sheetName+"</x:Name>\n"+
"    <x:WorksheetOptions>\n"+
"       <x:Print>\n"+
"        <x:ValidPrinterInfo />\n"+
"       </x:Print>\n"+
"     <x:Selected/>\n"+
(splitY!=null&&splitY>0?
	"     <x:FreezePanes/>\n"+
	"     <x:FrozenNoSplit/>\n"+
	"     <x:SplitHorizontal>"+splitY+"</x:SplitHorizontal>\n"+
	"     <x:TopRowBottomPane>"+splitY+"</x:TopRowBottomPane>\n"+
	"     <x:ActivePane>2</x:ActivePane>\n"+
	"     <x:Panes>\n"+
	"      <x:Pane>\n"+
	"       <x:Number>3</x:Number>\n"+
	"      </x:Pane>\n"+
	"      <x:Pane>\n"+
	"       <x:Number>2</x:Number>\n"+
	"      </x:Pane>\n"+
	"     </x:Panes>\n":"")+
"     <x:ProtectContents>False</x:ProtectContents>\n"+
"     <x:ProtectObjects>False</x:ProtectObjects>\n"+
"     <x:ProtectScenarios>False</x:ProtectScenarios>\n"+
"    </x:WorksheetOptions>\n"+
"   </x:ExcelWorksheet>\n"+
"  </x:ExcelWorksheets>\n"+
"  <x:ProtectStructure>False</x:ProtectStructure>\n"+
"  <x:ProtectWindows>False</x:ProtectWindows>\n"+
" </x:ExcelWorkbook>\n"+
"</xml><![endif]-->\n"+
"</head>");
	}


}