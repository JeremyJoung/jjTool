package jjTool;
import java.sql.*;
import java.util.*;
import java.net.*;
import java.io.*;


public class DataTool
{
/** open/close log */
public static boolean doLog=false;
/** Execute log */
public static StringBuffer excLog=new StringBuffer();
public static int sqlType=0; //1:MS-SQL, 2:MySQL, 3:Oracle
public static String sqlTag[][]={ {"", ""}, {"[", "]"}, {"`","`"}, {"\"", "\""} };

// ------------------------------------- Arrays

	static public String[] arrSql(String[] data, String[] cols)
	{	return arraySql( data, cols, null); }
	static public String[] arraySql(String[] data, String[] cols)
	{	return arraySql( data, cols, null); }
	static public String[] arrSql(String[] data, String[] cols, Map<String, String> map)
	{	return arraySql( data, cols, map); }
/** 
Convert array date to SQL command.

@param data	(String[]) Origin data array.
@param cols	(String[]) SQL columns array.
@param map	(Map&lt;String, String&gt;) Map for value transform.
@return		(String[]) 4 part of SQL commend [INSERT column(), INSERT VALUES(), UPDATE SET value, UPDATE WHERE rule value]
*/
	static public String[] arraySql(String[] data, String[] cols, Map<String, String> map)
	{
		int i=0;
		String cStr="", vStr="", upStr="", wStr="";
		try
		{
			for(; i<data.length && i<cols.length; i++)
			{
				if(cols[i].length()==0) continue; //data[i].length()==0||

				String tmp[]=cols[i].split(":")
				, val=data[i].trim().replace("'", "''")
				, col=sqlTag[sqlType][0]+tmp[0]+sqlTag[sqlType][1];
				if(tmp[0].length()==0) continue;
				boolean k=false;

					if(tmp.length==1 || tmp[1].equals("S")) // String
					{}
					else if(tmp.length>2 && tmp[1].equals("ST")) // static String
					{	val=tmp[2];	}
					else if(tmp[1].equals("KI")) // int key
					{
						val=Parser.toInt(data[i], tmp.length>2?Parser.toInt(tmp[2]):0)+"";
						k=true;
					}
					else if(tmp[1].equals("K")) // String key
					{	k=true;	}
					else if(tmp[1].equals("I")) // int
					{	val=Parser.toInt(data[i], tmp.length>2?Parser.toInt(tmp[2]):0)+"";	}
					else if(tmp[1].equals("MAP") && map!=null) // map
					{
						val=map.get(data[i]);
						if(val==null)	val="";
					}
					else if(tmp[1].equals("D")) // Date
					{
						if(val.equals("")) val="null";
						else val="DATE('"+val+"')";
						
						cStr+=", "+col;
						vStr+=", "+val;
						upStr+=", "+col+"="+val;
						continue;
					}
					else if(tmp[1].equals("MyD")) //MySQL
					{
						if(val.equals("")) val="null";
						else val="DATE('"+val+"')";
						
						cStr+=", "+col;
						vStr+=", "+val;
						upStr+=", "+col+"="+val;
						continue;
					}
					else if(tmp.length>2 && tmp[1].equals("MSD")) //MS-SQL
					{
						if(val.equals("")) val="null";
						else val="STR_TO_DATE('"+val+"', '"+tmp[2]+"')";
						
						cStr+=", "+col;
						vStr+=", "+val;
						upStr+=", "+col+"="+val;
						continue;
					}
					else if(tmp.length>2 && tmp[1].equals("MUL")) // multi col
					{
						for(int j=2; j<tmp.length; j++)
						{
							String col2=sqlTag[sqlType][0]+tmp[j]+sqlTag[sqlType][1];
							cStr+=", "+col2;
							vStr+=", '"+val+"'";
							upStr+=", "+col2+"='"+val+"'";
						}
					}
					else if(tmp[1].equals("B")) // boolean
					{
						String v=val.toUpperCase(), falseStr="|N|NO|X|F|FALSE||";
						if(falseStr.indexOf(v)!=-1) val="0";
						else val="1";
					}
					else if(tmp[1].equals("KV")) // key,value: two data.
					{
						String v[]=data[i].split(",");
						val=Parser.toInt(v[0])+"";
						if(v.length>1 && tmp.length>2)
						{
							cStr+=", "+sqlTag[sqlType][0]+tmp[2]+sqlTag[sqlType][1];
							vStr+=", '"+v[1].replace("'", "''")+"'";
							upStr+=", "+sqlTag[sqlType][0]+tmp[2]+sqlTag[sqlType][1]+"='"+v[1].replace("'", "''")+"'";
						}
					}
					else if(tmp[1].equals("CMD")) // SQL free command.
					{
						cStr+=", "+col;
						vStr+=", "+val;
						upStr+=", "+col+"="+val;
						continue;
					}
				cStr+=", "+col;
				vStr+=", '"+val+"'";
				if(k)
					wStr+=" AND "+col+"='"+val+"'";
				else
					upStr+=", "+col+"='"+val+"'";

			}
		}
		catch(Exception e)   
		{	return new String[]{i+": "+data+" <font color=red>¸ê®Æ²§±`</font><br>"};	}
		
		if(!cStr.equals("")) cStr=cStr.substring(2);
		if(!vStr.equals("")) vStr=vStr.substring(2);
		if(!upStr.equals("")) upStr=upStr.substring(2);
		if(!wStr.equals("")) wStr=wStr.substring(4);

		String tmp[]={cStr, vStr, upStr, wStr};
		return tmp;
	}
	
	
//arrSql2
	public static String[] sqlMaker(String[][] inp)
	{	return arrSql2(inp); }
	public static String[] arrSql2(String[][] inp) //[][col, value, type]
	{
		StringBuffer cStr=new StringBuffer(), vStr=new StringBuffer(), uStr=new StringBuffer();
		for(int i=0; i<inp.length; i++)
		{
			if(inp[i].length<2) continue;
			else if(inp[i].length==2 || inp[i][2].equals("S"))
			{
				cStr.append(", "+sqlTag[sqlType][0]+inp[i][0]+sqlTag[sqlType][1]);
				vStr.append(", '"+inp[i][1]+"'");
				uStr.append(", "+sqlTag[sqlType][0]+inp[i][0]+sqlTag[sqlType][1]+"='"+inp[i][1]+"'");
			}
			else if(inp[i][2].equals("D"))
			{
				cStr.append(", "+sqlTag[sqlType][0]+inp[i][0]+sqlTag[sqlType][1]);
				vStr.append(", "+inp[i][1]);
				uStr.append(", "+sqlTag[sqlType][0]+inp[i][0]+sqlTag[sqlType][1]+"="+inp[i][1]);
			}

		}
		String tmp[]={cStr.substring(2), vStr.substring(2), uStr.substring(2)};
		return tmp;
	}

	
//arrString
	public static String defSplitTag=",\t";
	
	public static String arrString(String[] dat)
	{	return arrString(dat, defSplitTag); }
	public static String arrString(String[] dat, String tag)
	{
		if(dat==null) return "arrString.Err: data null!!";
		if(dat.length==0) return "";
		StringBuffer str=new StringBuffer(dat[0]);
		for(int i=1; i<dat.length; i++)
			str.append(tag).append(dat[i]);
		return str.toString();
	}
	
// -------------------------------------------------- map

// rsMapI(ResultSet RS, String id, String name)
	public static String colSplit=" - ";
	public static TreeMap<Integer, String> rsMapI(ResultSet RS, String id, String name) throws Exception
	{
		int i=1;
		try
		{
			if(RS==null) return null;
			TreeMap<Integer, String> map=new TreeMap<Integer, String>();
			
			String nameList[]=name.split("\\|"), err="";
			for(; RS.next(); i++)
			{
				StringBuffer str=new StringBuffer();
				for(int j=0; j<nameList.length; j++)
					str.append(colSplit+RS.getString(nameList[j]));
				map.put(RS.getInt(id), str.substring(3));
			}
			return map;
		}
		catch(Exception e)
		{	throw new Exception("rsMapI.err: ["+id+", "+name+"]"+i+": ["+e.toString()+"]", e);	}

	}

	public static <T extends Map<Integer, String>> T rsMapI(ResultSet RS, String id, String name, T tp) throws Exception
	{
		int i=1;
		try
		{
			if(RS==null) return null;
			T map=(T)tp.getClass().newInstance();
			
			String nameList[]=name.split("\\|"), err="";
			for(; RS.next(); i++)
			{
				StringBuffer str=new StringBuffer();
				for(int j=0; j<nameList.length; j++)
					str.append(colSplit+RS.getString(nameList[j]));
				map.put(RS.getInt(id), str.substring(3));
			}
			return map;
		}
		catch(Exception e)
		{	throw new Exception("rsMapI.err: ["+id+", "+name+"]"+i+": ["+e.toString()+"]", e);	}

	}

// rsMapS(ResultSet RS, String id, String name)
	public static TreeMap<String, String> rsMapS(ResultSet RS, String id, String name) throws Exception
	{
		int i=1;
		try
		{
			if(RS==null) return null;
			TreeMap<String, String> map=new TreeMap<String, String>();
			
			String nameList[]=name.split("\\|");
			for(; RS.next(); i++)
			{
				StringBuffer str=new StringBuffer();
				for(int j=0; j<nameList.length; j++)
					str.append(colSplit+RS.getString(nameList[j]));
				map.put(RS.getString(id), str.substring(3));
			}
			return map;
		}
		catch(Exception e)
		{	throw new Exception("rsMapS.err: ["+id+", "+name+"]"+i+": ["+e.toString()+"]", e);	}
	}

	public static <T extends Map<String, String>> T rsMapS(ResultSet RS, String id, String name, T tp) throws Exception
	{
		int i=1;
		try
		{
			if(RS==null) return null;
			T map=(T)tp.getClass().newInstance();
			
			String nameList[]=name.split("\\|");
			for(; RS.next(); i++)
			{
				StringBuffer str=new StringBuffer();
				for(int j=0; j<nameList.length; j++)
					str.append(colSplit+RS.getString(nameList[j]));
				map.put(RS.getString(id), str.substring(3));
			}
			return map;
		}
		catch(Exception e)
		{	throw new Exception("rsMapS.err: ["+id+", "+name+"]"+i+": ["+e.toString()+"]", e);	}
	}

	Integer insertedKey(Statement stmt) throws Exception
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


/**
Load one RS row into String[].
@param RS	(ResultSet) Origin data ResultSet.
@param cols	(String[]) Format for array output.
@return		(String[]) output SQL data.
*/
	
//static String[] rsArray(ResultSet RS, String[] cols)
	static public String[] rsArray(ResultSet RS, String[] cols)
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
		{
			String err[]={"toArray.err!! ["+cols[i]+"]: "+e.toString()};
			return err;
		}
	}

// rsArray2
	static public String[][] rsArray2(ResultSet RS, String[] cols)
	{
		String c="";
		int i=0;
		try
		{
if(doLog)
	excLog.append("rsArray2 start. size: "+cols.length+"\n");
			long t=System.currentTimeMillis();
			if(RS==null || RS.isAfterLast())
			{	return new String[][] {{"toArray2.err!! [rs null!!]"}};	}
			int L=cols.length;
			ArrayList<String[]> al=new ArrayList<String[]>();
			for(; RS.next(); i++)
			{
				String a[]=new String[L];
				for(int j=0; j<L; j++)
				{
					String ct[]=cols[j].split("\\|");
					a[j]=ct.length>1
						? arrString(rsArray(RS, ct), colSplit)
						: RS.getString(cols[j]);
				}
				al.add(a);
if(doLog)
	excLog.append(i+": ["+(System.currentTimeMillis()-t)+"]\n");
			}
if(doLog)
	excLog.append("End: "+al.size()+"\n\n");
			return al.toArray(new String[1][1]);
		}
		catch(Exception e)
		{
			String err[][]={{"toArray2.err!! ["+c+"]"+i+": ["+e.toString()+"]"}};
			return err;
		}
		
	}


//split2D
	public static String[][] split2D(String inp, String d1, String d2)
	{
		if(inp==null || d1==null || d2==null) return null;
		String t1[]=inp.split(d1), ans[][]=new String[t1.length][];
		for(int i=0; i<t1.length; i++)
			ans[i]=t1[i].split(d2);
		return ans;
	}
	



	public static String getHttp(String url)
	{	return getHttp(url, "utf-8"); }
/** getHttp. Get text data from HTTP GET method. 
@param url	(String) HTTP url.
@param charSet	(String) Request charSet.
@return	Response text.
*/
	public static String getHttp(String url, String charSet)
	{
		try
		{
			URL urlO = new URL(url);

			String inputLine;
			StringBuffer o = new StringBuffer();
			URLConnection uc = urlO.openConnection();
			InputStream is=uc.getInputStream();
	if(false)
	{
			byte bt[]=new byte[1048576];
			int len = is.read(bt);
			return new String(bt, charSet);
	}
	else
	{		
			BufferedReader in = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));
			DataInputStream inStream = new DataInputStream(uc.getInputStream());
			while ((inputLine = in.readLine()) != null) 
				o.append(inputLine+"\r\n");
			in.close();
			return new String(o.toString().getBytes("ISO-8859-1"), charSet);
	}
		} 
		catch (Exception e) 
		{ 
			return "getHttp.err: "+e.toString(); 
		}

	}


	public static String postHttp(String url)
	{	return postHttp(url, "", "utf-8"); }
	public static String postHttp(String url, String data)
	{	return postHttp(url, data, "utf-8"); }
/** postHttp. Get data from HTTP POST method. 
@param url	(String) HTTP url.
@param data	(String) HTTP POST data.
@param charSet	(String) HTTP POST data charset. [Option]
@return	(String) HTTP response data.
*/
	public static String postHttp(String url, String data, String charSet)
	{
		try
		{
			String inputLine;
			StringBuffer o = new StringBuffer();
			URL urlO = new URL(url);
//			URLConnection uc = urlO.openConnection();
//			InputStream is=uc.getInputStream();

			HttpURLConnection huc = (HttpURLConnection) urlO.openConnection();
			huc.setRequestMethod( "POST" );
			huc.setRequestProperty( "Content-Length", data.getBytes(charSet).length+"");
			huc.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
			huc.setRequestProperty( "charset", charSet);
			huc.setUseCaches(false);
			huc.setAllowUserInteraction(false);
			huc.setInstanceFollowRedirects( false );
			huc.setDoOutput( true );
			
			DataOutputStream wr = new DataOutputStream(huc.getOutputStream());
			wr.writeBytes(data);
			wr.flush();
			wr.close();
			
			int responseCode = huc.getResponseCode();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(huc.getInputStream(), "ISO-8859-1"));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null)
				sb.append(line+"\n");
			br.close();

			return new String(sb.toString().getBytes("ISO-8859-1"), charSet);
		

		}
		catch (Exception e) 
		{
			return "postHttp.err: "+e.toString(); 
		}
	}
}