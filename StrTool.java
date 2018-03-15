package jjTool;
import java.util.*;

public class StrTool
{
/** Array struct text word break size. (Default 20) */
	public static int valCol=20;

	public static String cutStr(String inp, String prelude, String behind)
	{	return cutStr(inp, prelude, behind, 0);	}
/** Get substring by quote search key string.
@param inp	(String) Search original string data.
@param prelude	(String) Prelude search key.
@param behind	(String) Behind search key.
@param offset	(int) Start offset of search position. [option]
@return (String) Inner value between key. (Without key self).
*/
	public static String cutStr(String inp, String prelude, String behind, int offset)
	{
		if(inp==null || inp.equals("")) return "";
		if(prelude==null) prelude="";
		if(behind==null) behind="";
		
		if(offset<0) offset=0;
		int j=prelude.length()==0 ? 0
			: inp.indexOf(prelude, offset)+prelude.length()
		, k=behind.length()==0 ? -1
			: inp.indexOf(behind, j);
		if(j<prelude.length())	return "";
		else if(k<0)	return inp.substring(j);
		else	return inp.substring(j, k);
	}
	
	
//																				HTML
	
	public static String htmlTxt(String val)
	{	return htmlVal(val); }
/** return HTML value. 
@param val	(String) Original data string.
@return	(String) HTML format string.
*/
	public static String htmlVal(String val)
	{
		if(val==null) val="";
		String vals[][]=
		{ {"&", "&amp;"}
		, {"<", "&lt;"}
		, {">", "&gt;"}
		, {"\"", "&quot;"}
		, {"'", "&apos;"}
		, {"\n", "<br>\n"}
		};

		for(int i=0; i<vals.length; i++)
			val=val.replace(vals[i][0], vals[i][1]);
		return val;
	}

//																				javascript

//toJsVal
	public static String jsVal(String val)
	{	return jsVal(val, false); }
/** return JSON value without quote tag. 
@param val	(String || String[]) Original data string.
@param canNull	(boolean) If can be return a JSON null value. Default false. [option]
@return	(String || String[]) JSON format string without quote tag.
*/
	public static String jsVal(String val, boolean canNull)
	{
		if(val==null)	return canNull?"null":"";
		return val
		.replace("\\", "\\\\")	// 
		.replace("'", "\\'")		// '
		.replace("\"", "\\\"")			// "
		.replace("\t", "\\t")				// tab
		.replaceAll("(\\r?)\\n", "\\\\n")	// \n
		;
	}
	public static String[] jsVal(String[] val)
	{	return jsVal(val, false); }
	public static String[] jsVal(String[] val, boolean canNull)
	{
		if(val==null)	return new String[0];
		String r[]=new String[val.length];
		for(int i=0; i<val.length; i++)
			r[i]=jsVal(val[i], canNull);
		return r;
	}
	
	public static String jsValQ(String val)
	{	return jsValQ(val, false); }
/** Return JSON value with quote tag. 
@param val	(String || String[]) Original data string.
@param canNull	(boolean) If can be return a JSON null value. Default false. [option]
@return	(String || String[]) JSON format string with quote tag.
*/
	public static String jsValQ(String val, boolean canNull)
	{
		String t=jsVal(val, canNull);
		return (t==null||t.equals("null")?"null":"\""+t+"\"");
	}
	public static String[] jsValQ(String[] val)
	{	return jsValQ(val, false); }
	public static String[] jsValQ(String[] val, boolean canNull)
	{
		if(val==null)	return new String[0];
		String r[]=new String[val.length];
		for(int i=0; i<val.length; i++)
			r[i]=jsValQ(val[i], canNull);
		return r;
	}
	
/** Return JSON format Array struct text. (Without bracket[] tag)
@param val	(String[]) Original data string array.
@return	(String) JSON Array format string.
*/
	public static String jsArray(String[] val)
	{
		int i=0;
		try
		{
			if(val==null) return "";
			StringBuffer r=new StringBuffer();
			for(; i<val.length; i++)
				r.append((valCol>0 && i%valCol==0?"\n":"")+", "+jsValQ(val[i]));
			return r.substring(2);
			
		}
		catch(Exception e)
		{
			return "\"jsArray.Err["+i+": "+val[i]+"]: "+e.toString()+"\"";
		}
	}
	public static String jsArrayQ(String[] val)
	{	return "["+jsArray(val)+"]"; }

/** Return JSON format 2D Array struct text. (With bracket[] tag)
@param val	(String[][]) Original data string array.
@return	(String) JSON Array format string.
*/
	public static String jsArray2(String[][] val)
	{
		int i=0, j=0;
		try
		{
			if(val==null || val.length==0) return "[]";
			StringBuffer r=new StringBuffer();

			for(; i<val.length; i++)
			{
				for(; j<val[i].length; j++)
					r.append((j==0?", [ ":(i%valCol==0?"\n, ":", "))+jsValQ(val[i]));
				if(val[i].length>0)	r.append(" ]\n");
			}
			return "["+(r.length()>0?r.substring(2):"")+"]";
		}
		catch(Exception e)
		{
			return "[[\"jsArray2.Err["+i+", "+j+": "+val[i][j]+"]: "+e.toString()+"\"]]";
		}
	}
	
//	public static <T extends Map<<extends Object, String>> String jsObj(Map obj)
//	{
		
		
//	}
	public static <K, T extends Map<K, String>> String jsObj(T obj)
	{
		K k=null;
		String v="";
		try
		{
			StringBuffer str=new StringBuffer();
			
			Iterator<K> keyL=obj.keySet().iterator();
			for(int i=0; keyL.hasNext(); i++)
				if((v=obj.get(k=keyL.next()))!=null) // && v.length()>0)
					str.append((i%valCol==0?"\n":"")+", \""+k+"\":"+jsValQ(v));
			
			return str.length()>0?str.substring(2):"";
		}
		catch(Exception e)
		{	return "\"jsObj.Err\":"+jsValQ("["+k+"] "+e.toString());	}
	}
	
/*	public static String jsObj(Map<String, String> obj)
	{
		String k;
		String v="";
		try
		{
			StringBuffer str=new StringBuffer();
			
			Iterator<String> keyL=obj.keySet().iterator();
			for(int i=0; keyL.hasNext(); i++)
				if((v=obj.get(k=keyL.next()))!=null) // && v.length()>0)
					str.append((i%valCol==0?"\n":"")+", \""+k+"\":"+jsValQ(v));
			
			return str.length()>0?str.substring(2):"";
		}
		catch(Exception e)
		{	return "\"jsObj.Err\":"+jsValQ("["+k+"] "+e.toString());	}
	}
*/

//																				sql
	
/** Return SQL format value. 
@param val	(String || String[]) Original data string.
@return	(String || String[]) SQL format data string. (Without sql quote)
*/
	public static String sqlVal(String val)
	{
		if(val==null)	return "";
		return val.replace("'", "''")
		.replace("\\", "\\\\")
		.replace("\t", "\\t")
		.replaceAll("(\\r?)\\n", "\\\\n");
	}

	public static String[] sqlVal(String[] val)
	{
		if(val==null)	return new String[0];
		String r[]=new String[val.length];
		for(int i=0; i<val.length; i++)
			r[i]=sqlVal(val[i]);
		return r;
	}



// encoder
	public static String base64(String str)
	{	return base64(str.getBytes());	}
	public static String base64(byte[] bts)
	{	return javax.xml.bind.DatatypeConverter.printBase64Binary(bts);	}

	public static byte[] deBase64(String b64)
	{	return javax.xml.bind.DatatypeConverter.parseBase64Binary(b64);	}
	
	public static String hex(String str)
	{ return hex(str.getBytes());	}
	public static String hex(byte[] bts)
	{	return javax.xml.bind.DatatypeConverter.printHexBinary(bts);	}

	public static byte[] deHex(String hex)
	{	return javax.xml.bind.DatatypeConverter.parseHexBinary(hex);	}
	public static byte[] unHex(String hex)
	{	return javax.xml.bind.DatatypeConverter.parseHexBinary(hex);	}


}