<%@ page import = "jjTool.*" %>
<%! //datasource

public class DataSource
{
	int mode;
	public boolean hardEx=false;
	public ResultSet rs=null;
	public Map<String, String> map=null;
	public HttpServletRequest req=null;
	
	public DataSource()
	{
		mode=0;
	}
	
	public DataSource(HttpServletRequest req2)
	{
		mode=1;
		req=req2;
	}
	
	public DataSource(ResultSet RS) throws Exception
	{
		mode=2;
		if(hardEx&&(RS==null || RS.isAfterLast() || RS.isBeforeFirst())) throw new Exception("RS Err!!");
		this.rs=RS;
	}
	
	public DataSource(Map<String, String> map2)
	{
		mode=3;
		this.map=map2;
	}
	
	public String get(String name)
	{
		try
		{
			switch(mode)
			{
				case 0:
					return "";
				case 1:
					return req.getParameter(name);
				case 2:
					return rs.getString(name);
				case 3:
					return map.get(name);
				default: return "unlnow mode["+mode+"]";
			}
		}
		catch(Exception e)
		{	return "Err["+name+"]: "+e.toString(); }
	}
}

%>
<%! //formTab2



/** mode 0:none, 1:req, 2:RS, 3:Map
	colList[][colText, colKey, colType, tdAttr, tdFrontText, tdBackText, inpAttr]
	@version 2017/09/01
 */

public String formTab2(String[][] colList)
{	return formTab2(colList, 1,		new TreeMap<String, Object>(), new DataSource()); }

public String formTab2(String[][] colList, int cols)
{	return formTab2(colList, cols,	new TreeMap<String, Object>(), new DataSource()); }

public String formTab2(String[][] colList, int cols, Map<String, Object> listPool)
{	return formTab2(colList, cols,	listPool,	new DataSource()); }

public String formTab2(String[][] colList, int cols, DataSource ds)
{	return formTab2(colList, cols, null, ds); }

public String formTab2(String[][] colList, int cols, Map<String, Object> listPool, DataSource ds)
{
	StringBuffer str=new StringBuffer();
	String colLab0[]={}, colLab="", col="", tp0[]={}, tp="", tp1="";
	int i=0, rowUsed[]=new int[colList.length+10], x=0, y=0;
	try
	{
		// colList[colLab:colspan:rowspan, col, colType, tdAttr, frontTxt, endTxt, inpAttr]
		for( ;i<colList.length; i++, x++)
		{
			String tdAttr="", frontTxt="", endTxt="", inpAttr="";
			if(cols>=0 && x>=cols) 
			{
				str.append("<tr>"); 								//換行
				x=rowUsed[++y];
			}
			
			if(colList[i].length>3 && colList[i][2].equals("M")) 				//手動
			{
				str.append(colList[i][3]);
				continue;
			}
			
			
//value split
			colLab=(colLab0=colList[i][0].trim().split(":"))[0];
			switch(colList[i].length)
			{
				case 8:
				case 7:
					inpAttr=colList[i][6].trim();
				case 6:
					endTxt=colList[i][5].trim();
				case 5:
					frontTxt=colList[i][4].trim();
				case 4:
					tdAttr=colList[i][3].trim();
				case 3:
					tp1=(tp=(tp0=colList[i][2].trim().split(":"))[0]).split(" ")[0];
				case 2:
					col=colList[i][1].trim();
			}
		
			if(colLab.equals("")) continue; // skep
			String reqed=(colLab.indexOf("*")==0?" required":"")+" "+inpAttr;				//必選

			int cSpan=(colLab0.length>1?Parser.toInt(colLab0[1], 1):1)
				, rSpan=(colLab0.length>2?Parser.toInt(colLab0[2], 1):1);
			str.append((cols>=0 ? "<th class=colTitle"+(rSpan>1?" rowspan="+rSpan:"")+">"+ // title
				colLab.replace("*", "<font color=red>*</font>")+"</th>"+
				(cols==0?"</tr>\n<tr>":""):"")+
				
				"<td class=colContent"+(colList[i].length>3?" "+colList[i][3]:"") // input
					+(cSpan>1?" colspan="+(cSpan*2-1):"")
					+(rSpan>1?" rowspan="+rSpan:"")+">"+					//td
				frontTxt);
			
			if(rSpan>1)
			{
				for(int a=1; a<rSpan; a++)
					rowUsed[y+a]+=cSpan;
			}
			if(cSpan>1) x+=cSpan;
			
			if(col.equals("")) continue; //no value
			
			String val=ds.get(col);
			int valI=-1;
			if(ds.mode>0) valI=Parser.toInt(val);
			
			if(tp.indexOf("N")==0)												//null
				str.append("<input name='"+col+"' "+reqed+"/>");
			else if(tp.indexOf("V")==0)											//view
				str.append(StrTool.htmlVal(val).replace("\n", "<br>\n")+
					HtmlTool.Input.hidden(col, val));
			else if(tp.indexOf("HIDE")==0)										//hide
				str.append(HtmlTool.Input.hidden(col, val));
			else if(tp.indexOf("H")==0)											//HTML
				str.append(val);
			else if(tp.indexOf("LV")==0||tp.indexOf("IV")==0)					//list view
			{
				Object obj=listPool.get(col);
				String cName="";
				if(obj==null)
				{	str.append("obj null! ["+col+"]");	}
				else if((cName=obj.getClass().getName()).indexOf("Map")>=0)
				{
					Map<Integer, String> l=(Map<Integer, String>)obj;
					str.append(l.get(valI)!=null?l.get(valI):"");
				}
				else if(cName.equals("[Ljava.lang.String;")) // String[]
				{
					String l[]=(String[])obj;
					str.append(valI>=0&&valI<l.length?l[valI]:"");
				}
				else if(cName.equals("[[Ljava.lang.String;")) // String[][]
				{
					String[][] l=(String[][])obj;
					for(int a=0; a<l.length; a++)
					{
						if(l[a][0].equals(val))
						{
							str.append(l[a][1]);
							break;
						}
					}
				}
				else	str.append("unknow data["+cName+"]! ("+valI+")");
				str.append(HtmlTool.Input.hidden(col, val));
			}
			else if(tp.indexOf("SEL")==0)										//int map select
			{
				Object obj=listPool.get(col);
				String cName="";
				if(obj==null)
					str.append("obj null! ["+col+"]");
				else if((cName=obj.getClass().getName()).indexOf("Map")>=0)
					str.append("<select name='"+col+"'"+reqed+"><option></option>"+
						HtmlTool.Option.mapI((Map<Integer, String>)obj, valI)+"</select>");
				else if(cName.equals("[Ljava.lang.String;"))
					str.append("<select name='"+col+"'"+reqed+"><option></option>"+
						HtmlTool.Option.integer((String[])obj, valI)+"</select>");
				else if(cName.equals("[[Ljava.lang.String;"))
					str.append("<select name='"+col+"'"+reqed+"><option></option>"+
						HtmlTool.Option.text((String[][])obj, val)+"</select>");
				else	str.append("unknow data["+cName+"]!");
			}
			else if(tp1.equals("R")) 											//int radio
			{
				Object obj=listPool.get(col);
				String cName="";
				if(obj==null)
					str.append("obj null! ["+col+"]");
				else if((cName=obj.getClass().getName()).equals("[Ljava.lang.String;"))
					str.append(HtmlTool.Radio.integer(col, (String[])obj, valI, reqed));
				else if(cName.equals("[[Ljava.lang.String;"))
					str.append(HtmlTool.Radio.text(col, (String[][])obj, val, reqed));
				else	str.append("unknow data["+cName+"]!");
			}
			else if(tp1.equals("B")) 											// bool
			{
				str.append(HtmlTool.Checkbox.getCheck(col, "1", "", val));
			}
			else if(tp1.equals("CB")) 											//checkbox
			{
				str.append(HtmlTool.Checkbox.bitCheck(col, (String[])listPool.get(col), valI));
			}
			else if(tp1.equals("D")) //date
				str.append(HtmlTool.Input.text(col, val, " onclick='displayCalendar(this, \"yyyy-mm-dd\", this);' size='10' title='日期格式: yyyy-mm-dd' pattern='\\d{4}-\\d{1,2}-\\d{1,2}' placeholder='yyyy-mm-dd' "+reqed));
			else if(tp1.equals("I"))
				str.append(HtmlTool.Input.num(col, Parser.toInt(val), reqed+(tp0.length>2?" min='"+tp0[1]+"' max='"+tp0[2]+"'":"")));
			else if(tp1.equals("F"))
				str.append("<input name='"+col+"' value='"+Parser.toFloat(val)+"'"+reqed+" />");
			else if(tp.indexOf("TA")==0) 							//textarea [TA:*cols:*rows]
			{

				
				str.append("<textarea name='"+col+"'"+(tp0.length>1?" cols="+tp0[1]:"")+(tp0.length>2?" rows="+tp0[2]:"")+reqed+">"+val+"</textarea>");
			}
			else if(tp1.equals("MAIL"))											//email
				str.append(HtmlTool.Input.text(col, val, reqed+" type=email"));
			else 													//default
				str.append(HtmlTool.Input.text(col, val, reqed+(tp0.length>1?" maxlength='"+tp0[1]+"'":"")));
			str.append(endTxt+"</td>\n");
		}
		return str.toString();
	}
	catch(Exception e)
	{	return "formTab.Err["+i+", "+col+": "+tp+": "+colLab+"]: < "+e.toString()+" >";	}
}

public String formHead2(String[][] colList)
{
	StringBuffer str=new StringBuffer();
	for(int i=0 ;i<colList.length; i++)
	{
		str.append("<th class=colTitle>"+
			colList[i][0].replace("*", "<font color=red>*</font>")+"</th>");
	}
	return str.toString();
}


String mustCols2(String[][] colList)
{
	StringBuffer str2=new StringBuffer("");
	for(int i=0; i<colList.length; i++)
		if(colList[i][0].indexOf("*")==0)
			str2.append(", ['"+colList[i][1]+"', '"+colList[i][0].substring(1)+"']");
	
	if(str2.length()>0) return str2.substring(2);
	else return "";
}
%>
