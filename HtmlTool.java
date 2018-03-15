package jjTool;

import java.sql.*;
import java.util.*;

public class HtmlTool
{
	public static String colSplit=" - ";

/** Generate HTML tag. */

//FormMaker 2013/7/31 ------------------------------------------------------
	public static String fromFrame[]={"<li>"," :<br>", "</li>"};
	public static String kfStr="<font color=red>*</font>";
	
	public static String getForm(String[][] inp)
	{	return getForm(inp, null); }
	public static String getForm(String[][] inp, JspIO jsp)
	{
		StringBuffer str=new StringBuffer();
		for(int i=0; i<inp.length; i++)
		{
			String attr="";
			if(inp[i].length>2)
			{
				if(inp[i][2].indexOf("P")!=-1)
					attr=" type=password";
			}
			str.append(fromFrame[0]+(inp[i].length>2&&inp[i][2].indexOf("*")==0?kfStr:"")+inp[i][0]+fromFrame[1]+
			Input.text(inp[i][1], (jsp!=null?jsp.reqString(inp[i][1], ""):""), attr)+
			(inp[i].length>3?inp[i][3]:"")+fromFrame[2]+"\n");
		}
		return str.toString();
	}
	
/** Method about make &lt;input&gt; tag. */
	public static class Input
	{
/** Default attribute in &lt;input&gt;. */
		public static String inpDefAttr="";
/** If auto select the input onFocus. */
		public static boolean autoSelect=true;
		
//dataInput
		public static String text(String name)
		{	return text(name, "", inpDefAttr);	}
		public static String text(String name, double value)
		{	return text(name, value+"", inpDefAttr);	}
		public static String text(String name, double value, String attr)
		{	return text(name, value+"", attr);	}
		public static String text(String name, long value)
		{	return text(name, value+"", inpDefAttr);	}
		public static String text(String name, long value, String attr)
		{	return text(name, value+"", attr);	}
		public static String text(String name, String value)
		{	return text(name, value, inpDefAttr);	}
		public static String text(String name, String value, String attr)
		{
			if(value==null) value="";
			return "<input name=\""+StrTool.htmlVal(name)+
				"\" value=\""+StrTool.htmlVal(value)+"\" "+attr+
				(autoSelect?" onFocus='this.select();'":"")+">";
		}

//num
		public static String num(String name)
		{	return num(name, 0, inpDefAttr);	}
		public static String num(String name, long value)
		{	return num(name, value+"", inpDefAttr);	}
		public static String num(String name, double value)
		{	return num(name, value+"", inpDefAttr);	}
		public static String num(String name, long value, String attr)
		{	return num(name, value+"", attr);	}
		public static String num(String name, double value, String attr)
		{	return num(name, value+"", attr);	}
		public static String num(String name, String value, String attr)
		{
			return "<input type=number name=\""+StrTool.htmlVal(name)+
				"\" value=\""+value+"\" "+attr+
				(autoSelect?" onFocus='this.select();'":"")+">"; 
		}


//hidden
		public static String hidden(String name, long value)
		{	return hidden(name, value+""); }
		public static String hidden(String name, double value)
		{	return hidden(name, value+""); }
		public static String hidden(String name, Object value)
		{	return hidden(name, value.toString()); }
		public static String hidden(String name, String value)
		{
			if(value==null) value="";
			return "<input type=hidden name=\""+StrTool.htmlVal(name)+"\" value=\""+StrTool.htmlVal(value)+"\">";
		}
		
		public static String rs(String name, ResultSet rs1)
		{	return rs(name, name, rs1, inpDefAttr);	}
		public static String rs(String name, ResultSet rs1, String attr)
		{	return rs(name, name, rs1, attr);	}
		public static String rs(String name, String col, ResultSet rs1)
		{	return rs(name, col, rs1, inpDefAttr);	}
		public static String rs(String name, String col, ResultSet rs1, String attr)
		{
			if(name==null || name.equals("")) return "";
			try
			{
				if(col==null||col.length()==0) col=name;
				if(rs1==null || rs1.isAfterLast() || rs1.isBeforeFirst()) return "RS null!";
				return text(name, rs1.getString(col), attr);
			}
			catch(Exception e)
			{	return "RS err!!["+col+", "+e.toString()+"]";	}
		}

		public static String req(String name, JspIO jsp)
		{	return req(name, name, jsp, inpDefAttr); }
		public static String req(String name, String para, JspIO jsp)
		{	return req(name, para, jsp, inpDefAttr); }
		public static String req(String name, JspIO jsp, String attr)
		{	return req(name, name, jsp, attr); }
/**
Make Input from request.
@param name (String) Input "name" attribute.
@param para (String) Request parameter value. [option]
@param jsp (jjTool.JspIO) Source request Object.
@param attr (String) Input attribute texts. [option]
@return HTML Form &lt;INPUT&gt; text.
*/
		public static String req(String name, String para, JspIO jsp, String attr)
		{
			if(name==null || name.equals("")) return "";
			if(para==null||para.length()==0) para=name;
			
			return text(name, StrTool.htmlVal(jsp.reqString(para)), attr);
		}
		
		
	}
	
/** Extends from HtmlTool.Input. */
	public static class I extends Input
	{
		
	}

/** Method about &lt;option&gt; (only) tag. */
	public static class Option
	{
		public static String selectedTag="*";

/** A general method name for all type data input by this inner Class. 
@param options Multi type input data.
@return HTML option tag.
*/
		public static String general(String[] options) // String[]
		{	return integer(options, -1);	}
		public static String general(String[] options, int sel)
		{	return integer(options, sel);	}

		public static String general(String[][] options) // String[][]
		{	return text(options, null); }
		public static String general(String[][] options, String sel)
		{	return text(options, sel); }
		public static String general(String[][] options, int sel)
		{	return text(options, sel+""); }

		public static String general(Map options)
		{
			Iterator keyL=options.keySet().iterator();
			Object k;
			if(keyL.hasNext())
			{
				k=keyL.next();
				String n=k.getClass().getName();
				if(n.equals("java.lang.Integer"))
					return mapI(options, null); 
				else if(n.equals("java.lang.String"))
					return mapS(options, null);
				else return "<option>Unknow value type: ["+n+"]</option>\n";
			}
			else return "";
			
		}

// Map<Integer, String>
//		public static String general(Map<Integer, String> options) 
//		{	return mapI(options, null); }
		public static String general(Map<Integer, String> options, Integer sel) 
		{	return mapI(options, sel); }
// Map<String, String>
//		public static String general(Map<String, String> options) 
//		{	return mapS(options, null); }
		public static String general(Map<String, String> options, String sel) 
		{	return mapS(options, sel); }

// int min, int max
		public static String general(int min, int max) 
		{	return range(min, max, null); }
		public static String general(int min, int max, Integer sel)
		{	return range(min, max, sel); }

// ResultSet
		public static String general(ResultSet RS, String value, String texts) 
		{	return rs(RS, value, texts, null); }
		public static String general(ResultSet RS, String value, String texts, int sel)
		{	return rs(RS, value, texts, sel+""); }
		public static String general(ResultSet RS, String value, String texts, String sel)
		{	return rs(RS, value, texts, sel); }


// intOption
		public static String integer(String[] options)
		{	return integer(options, -1); }
		public static String integer(String[] options, int sel)
		{
			if(options==null)	return "<option>integer.Err: Option null!</option>\n";
			StringBuffer str=new StringBuffer();
			boolean grp=false;
			for(int i=0; i<options.length; i++)
			{
				if(options[i]==null || options[i].equals("")) {}
				else if(options[i].charAt(0)=='*' && options[i].charAt(options[i].length()-1)=='*')
				{
					str.append((grp?"</optgroup>\n":"")+"<optgroup label='"+options[i].substring(1, options[i].length()-1).replace("'", "")+"'>\n");
					grp=true;
				}
				else
					str.append(getOption(i, options[i], sel)+"\n");
				
			}
			if(grp) str.append("</optgroup>\n");
			return str.toString();
		}

		public static String range(int min, int max)
		{	return range(min, max, null); }
/** Make &lt;OPTION&gt; with int range.
@param min (int)start value.
@param max (int)end value.
@param sel (Integer)defalut value. [option]
@return (String) HTML &lt;OPTION&gt; tags.
*/
		public static String range(int min, int max, Integer sel)
		{
			if(min>max)	return "<option>range Err["+min+" ~ "+max+"]!</option>";
				StringBuffer str=new StringBuffer();
			for(int j=min; j<=max; j++)
				str.append(getOption(j, j+"", sel)+"\n");
			return str.toString();
		}


// stringOption[n...][value, txt]
		public static String text(String[][] options)
		{	return text(options, null); }
		public static String text(String[][] options, int sel)
		{	return text(options, sel+""); }
/** Make &lt;OPTION&gt; with (String[][]).
@param options (String[][]) Option data by format String[n...][value, txt]. [option]
@param sel (String|int) Defalut selected. [option]
@return (String) HTML &lt;OPTION&gt; tags.
*/
		public static String text(String[][] options, String sel)
		{
			if(options==null)	return "<option>Option null!</option>\n";
			StringBuffer str=new StringBuffer();
			int g=0;
			for(int i=0; i<options.length; i++)
			{
				if(options[i]==null || options[i][0]==null || options[i][0].equals("")) continue;
				if(options[i][0].indexOf("*")==0)
				{
					if(g>0) str.append("</optgroup>");
					str.append("<optgroup label='"+options[i][1]+"'"+
						(options[i][0].length()>0?" ID='group"+options[i][0].substring(1)+"'":"")+">");
					g++;
					continue;
				}
				String v=options[i].length>1?options[i][1]:options[i][0];
				str.append(getOption(options[i][0], v, sel)+"\n");
			}
			if(g>0) str.append("</optgroup>");
			return str.toString();
		}



// mapOption
		public static String mapI(Map<Integer, String> options)
		{	return  mapI(options, null); }
		public static String mapI(Map<Integer, String> options, Integer sel)
		{
			if(options==null)	return "<option>Option null!</option>\n";
			Iterator<Integer> keyL=options.keySet().iterator();
			StringBuffer str=new StringBuffer();
			Integer k;
			while(keyL.hasNext())
				if(options.get(k=keyL.next()).length()>0)
					str.append(getOption(k, options.get(k), sel)+"\n");
			return str.toString();
		}

		public static String mapS(Map<String, String> options)
		{	return mapS(options, null); }
		public static String mapS(Map<String, String> options, String sel)
		{
			if(options==null)	return "<option>Option null!</option>\n";
			
			Iterator<String> keyL=options.keySet().iterator();
			StringBuffer str=new StringBuffer();
			String k;
			while(keyL.hasNext())
				if(options.get(k=keyL.next()).length()>0)
					str.append(getOption(k, options.get(k), sel)+"\n");
			return str.toString();
		}

		public static String rs(ResultSet RS, String value, String texts)
		{	return rs(RS, value, texts, null); }
		public static String rs(ResultSet RS, String value, String texts, int sel)
		{	return rs(RS, value, texts, sel+""); }
/** Make HTML options from ResultSet.
@param RS (ResultSet) Source data.
@param value (String) Option value by RS column.
@param texts (String) Option label texts by RS columns. (Split multi labels by char "|")
@param sel (String) option selected value. [option]
@return HTML &lt;option&gt; tags.
*/
		public static String rs(ResultSet RS, String value, String texts, String sel)
		{
			if(RS==null) return "<option>RS null!</option>";
			String opt="";
			try
			{
				String str2="", keyList[]=texts.split("\\|");
				StringBuffer str=new StringBuffer();
				while(RS.next())
				{
					if(RS.getString(value).equals("*"))
					{
						str.append((opt.equals("")?"":"</optgroup>\n")+
							"<optgroup label='"+(opt=RS.getString(texts))+"'>\n");
						continue;
					}
					str2="";
					for(int j=0; j<keyList.length; j++)
						str2+=colSplit+RS.getString(keyList[j]);
					str.append(getOption(RS.getString(value), str2.substring(3), sel)+"\n");
				}
				return str.toString();
			}
			catch(Exception e)
			{	return "<option>rs.Err!! ["+value+"]: "+e.toString()+"</option>";	}
		}

		public static String getOption(String val, String txt, String sel)
		{	return "<option value='"+val+"'"+(val!=null&&val.equals(sel)?" selected class='selected'":"")+">"+
				(val.equals(sel)?selectedTag:"")+StrTool.htmlVal(txt)+"</option>";}

		public static String getOption(Integer val, String txt, Integer sel)
		{	return "<option value='"+val+"'"+(val.equals(sel)?" selected class='selected'":"")+">"+
			(val.equals(sel)?selectedTag:"")+StrTool.htmlVal(txt)+"</option>";
		}
	}
	
/** Extends from HtmlTool.Option. 
@see	HtmlTool.Option
*/
	public static class O extends Option {}


/** Method about radio type &lt;input&gt; tag. */
	public static class Radio
	{
/** Default attribute in radio type. */
		static String radioDef="";

/** A general method name for all type data input by this inner Class. 
@param name Name of radio tag.
@param options Multi type input data.
@return HTML radio tag.
*/

//		public static String general(String name, options)
//		{	return }
		public static String general(String name, String[] options)
		{	return integer(name, options, -1, radioDef); }
		public static String general(String name, String[] options, int sel)
		{	return integer(name, options, sel, radioDef); }
		public static String general(String name, String[] options, int sel, String attr)
		{	return integer(name, options, sel, attr); }

		public static String general(String name, String[][] options)
		{	return text(name, options, null, radioDef); }
		public static String general(String name, String[][] options, String sel)
		{	return text(name, options, sel, radioDef); }
		public static String general(String name, String[][] options, String sel, String attr)
		{	return text(name, options, sel, attr); }

		public static String general(String name, int min, int max)
		{	return range(name, min, max, null); }
		public static String general(String name, int min, int max, Integer sel)
		{	return range(name, min, max, sel); }












//intRadio
		public static String integer(String name, String[] options)
		{	return integer(name, options, -1, radioDef); }
		public static String integer(String name, String[] options, int sel)
		{	return integer(name, options, sel, radioDef); }
		public static String integer(String name, String[] options, int sel, String attr)
		{
			if(name==null || name.equals(""))	return "integer.err: name null!";
			if(options==null || options.length==0) return "integer.err: ["+name+"]option null!";

			StringBuffer str=new StringBuffer();
			for(int i=0, j=options.length; i<j; i++)
				if(options[i]!=null && options[i].length()>0)
					str.append(", "+getRadio(name, i+"", options[i], sel+"", attr)+" \n");
			return str.length()>0?str.substring(2):"";
		}

		public static String text(String name, String[][] options)
		{	return text(name, options, null, radioDef); }
		public static String text(String name, String[][] options, String sel)
		{	return text(name, options, sel, radioDef); }
		public static String text(String name, String[][] options, String sel, String attr)
		{
			if(options==null)	return "Radio.text.Err: Option null!\n";
			StringBuffer str=new StringBuffer();
			for(int i=0; i<options.length; i++)
			{
				if(options[i]==null || options[i][0]==null || options[i][0].equals("")) continue;
				String t=options[i].length>1?options[i][1]:options[i][0];
				str.append(getRadio(name, options[i][0], t, sel, attr)+"\n");
			}
			return str.toString();
		}

		
		//rangeRadio
		public static String range(String name, int min, int max)
		{	return range(name, min, max, null); }
		public static String range(String name, int min, int max, Integer sel)
		{
			if(name==null) return "range.err: name null!";
			if(min>max)	return "range.err: range Err["+min+", "+max+"]!";

			String str="";
			for(int j=min; j<=max; j++)
				str+=(", "+getRadio(name, j+"", j+"", sel+"", radioDef)+"\n");
			return str.substring(2);
		}
		
		
		//rsRadio
		public static String rs(String name, ResultSet RS, String value)
		{ return rs(name, RS, value, value, "", ""); }
		public static String rs(String name, ResultSet RS, String value, String texts)
		{ return rs(name, RS, value, texts, "", ""); }
		public static String rs(String name, ResultSet RS, String value, String texts, String sel)
		{ return rs(name, RS, value, texts, sel, ""); }
		public static String rs(String name, ResultSet RS, String value, String texts, String sel, String attr)
		{
			if(RS==null) return "rs.Err: RS null!";
			try
			{
				String str2="", keyList[]=texts.split("\\|");
				StringBuffer str=new StringBuffer();
				while(RS.next())
				{
					str2="";
					for(int j=0; j<keyList.length; j++)
						str2+=colSplit+RS.getString(keyList[j]);
					str.append(getRadio(name, RS.getString(value), str2.substring(3), sel, attr)+"\n");
				}
				return str.toString();
			} 
			catch(Exception e)
			{	return "rs.Err!! ["+value+"]: "+e.toString();	}
		}

		

		public static String getRadio(String name, String value)
		{	return getRadio(name, value, value, "", ""); }
		public static String getRadio(String name, String value, String txt)
		{	return getRadio(name, value, txt, "", ""); }
		public static String getRadio(String name, String value, String txt, String sel)
		{	return getRadio(name, value, txt, sel, ""); }
		public static String getRadio(String name, String value, String txt, String sel, String attr)
		{
			return "<nobr><label class='radioLabel'>"+
			"<input type=radio name="+name+" value='"+value+"'"+(value.equals(sel)?" checked":"")+" "+attr+">"+txt+"</label></nobr>";
		}
		
	}
	
/** Extends from HtmlTool.Radio. 
@see	HtmlTool.Radio
*/
	public static class R extends Radio{	}


/** Method about checkbox type &lt;input&gt; tag. */
	public static class Checkbox
	{
		public static String bitCheck(String name, String[] option1)
		{	return bitCheck(name, option1, 0); }

		public static String bitCheck(String name, String[] option1, int value)
		{
			if(option1 == null) return "Option null!";

			String str="";
			for(int i=0; i<option1.length; i++)
			{
				if(option1[i]!=null && !option1[i].equals(""))
					str+=getCheck(name, ""+(1<<i), option1[i], (value&(1<<i))+"", "");
			}
			return str;
		}

		public static String sqlBitCheck(String name, ResultSet rs1, String key, String texts, int value)
		{
			try
			{
				if(rs1==null)	return "RS null!";
				String str="";
				while(rs1.next())
				{
					int vle=1<<(rs1.getInt(key)-1);
					if(!rs1.getString(texts).equals(""))
						str+=" <nobr><label><input type=checkbox name="+name+" value="+vle+((value&vle)!=0?" checked":"")+">"+
						rs1.getString(texts)+"</label></nobr>\n";
				}
				return str;
			}
			catch(Exception e)
			{	return "RS err!!["+name+", "+e.toString()+"]"; 
			}
		}

		public static String sqlCheckB(String name, ResultSet RS, String keyName)
		{
			try
			{
				if(RS==null)	return "sqlCheckB.Err: RS null!";
				return getCheck(name, "1", keyName, (RS.getBoolean(name)?"1":""));
			}
			catch(Exception e)
			{	return "sqlCheckB.Err!!["+name+", "+e.toString()+"]";	}
		}

		public static String getCheck(String name, String value)
		{	return getCheck(name, value, value, "", ""); }
		public static String getCheck(String name, String value, String txt)
		{	return getCheck(name, value, txt, "", ""); }
		public static String getCheck(String name, int value, String txt)
		{	return getCheck(name, value+"", txt, "", ""); }
		public static String getCheck(String name, int value, String txt, int sel)
		{	return getCheck(name, value+"", txt, sel+"", ""); }
		public static String getCheck(String name, String value, String txt, String sel)
		{	return getCheck(name, value, txt, sel, ""); }
		public static String getCheck(String name, String value, String txt, String sel, String attr)
		{
			if(value==null || name==null || name.equals("")) return "getCheck.null";
			return "<nobr><label class=ckb>"+
			"<input type='checkbox' name='"+name+"' value='"+value+"'"+(value.equals(sel)?" checked":"")+" "+attr+">"+txt+"</label></nobr>\n";
		}
	}
	
/** Extends from HtmlTool.Checkbox . 
@see	HtmlTool.Checkbox
*/
	public static class C extends Checkbox {}

//tab linker
	public static String tabLinks(String url, String[] menuL)
	{ return tabLinks(url, menuL, null, -1); }
	public static String tabLinks(String url, String[] menuL,int sel)
	{ return tabLinks(url, menuL, null, sel); }
	public static String tabLinks(String url, String[] menuL, String[] inf)
	{ return tabLinks(url, menuL, inf, -1); }
	public static String tabLinks(String url, String[] menuL, String[] inf, int sel)
	{
		if(menuL.length==0) return "";
		StringBuffer str=new StringBuffer(" | ");
		for(int i=0; i<menuL.length; i++)
		{
			String inf2=(inf!=null && inf.length>i && inf[i]!=null)?" "+inf[i]:"";
			if(menuL[i]==null ||  menuL[i].equals("")) continue;
			else if(i==sel)
				str.append(menuL[i]+inf2+" | ");
			else
				str.append("<a href='"+url.replace("*", i+"")+"'>"+menuL[i]+"</a>"+inf2+" | ");
		}
		return str.toString();
	}

//pager ------------------------------------------------------------

/** Text in next page link. */
	public static String pagerNext="》";
/** Title attribute in next page link. */
	public static String pagerNextT="下一頁";
/** Text in next previous link. */
	public static String pagerPre="《";
/** Title attribute in previous page link. */
	public static String pagerPreT="上一頁";
/** Title attribute in first page link. */
	public static String pageFirstT="第一頁";
/** Title attribute in last page link. */
	public static String pageLastT="最後頁";
	
	public static String pager(int maxPage, int currentPage, int range, String url)
	{	return pager(maxPage, currentPage, range, url, "page");	}

/** Generate a HTML page switcher list. 
@version 2013/7/2
@param maxPage	(int) Last page value for this pager.
@param currentPage	(int) Current page value.
@param range	(int) Page switcher offset size.
@param url	(String) Link url.
@param pgPara	(String) Page's parameter name in url.
@return	(String) HTML Pageer text.
*/
	public static String pager(int maxPage, int currentPage, int range, String url, String pgPara) // page
	{
		String url2=url.indexOf(pgPara)>0?url.replaceAll(pgPara+"=\\d+", pgPara+"=*")
			:url + (url.indexOf("?")>=0?"&":"?") + pgPara+"=*";
		StringBuffer str=new StringBuffer();
		if(currentPage<1) currentPage=1;
		if(currentPage>maxPage) currentPage=maxPage;
		int p1=Math.max(1,currentPage-range), p2=Math.min(maxPage, currentPage+range);
		
		if(currentPage!=1) // pre
			str.append("<a href='"+url2.replace("*", currentPage-1+"")+"' class=pre title=\"- "+pagerPreT+" -\">"+pagerPre+"</a> ");
		if(p1!=1) // first
			str.append("<a href='"+url2.replace("*", "1")+"' class=first title=\"- "+pageFirstT+" -\">1...</a> ");
		for(; p1<=p2; p1++) // range
			str.append(p1==currentPage?("<span class=act>["+p1+"/"+maxPage+"]</span> "):("<a href='"+url2.replace("*", p1+"")+"' title=\"- 第"+p1+"頁 -\">"+p1+"</a> "));
		if(p2!=maxPage) // next
			str.append("<a href='"+url2.replace("*", maxPage+"")+"' class=last title=\"- "+pageLastT+" -\">..."+maxPage+"</a> ");
		if(currentPage<maxPage) // last
			str.append("<a href='"+url2.replace("*", currentPage+1+"")+"' class=next title=\"- "+pagerNextT+" -\">"+pagerNext+"</a> ");
		
		return str.toString();
	}
}