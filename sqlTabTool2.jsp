<%@ page import = "jjTool.*" %>
<%@ page import = "java.util.*, java.text.*" %>
<%@ page import = "java.sql.DriverManager, java.sql.Connection, java.sql.Statement, java.sql.ResultSet, java.sql.SQLException" %>
<%! //sqlTabTool2
/**
@version 20171002
*/
public class sqlTabTool2
{
	public TreeMap sumPool=new TreeMap();
	public Map objs=null;
	public ResultSet RS;
	
/**
	Format: [colName, colType, colLabel, colWidth]
*/
	public String[][] cols; 
	public String splitTag="\n";
	public String startTag="<td>";
	public String endTag="</td>";
	public int n=0;
	public String[] keys=null;
	public int tabMode=0;
	
	private TreeMap<Integer, Integer> counted=new TreeMap<Integer, Integer>();
	
	public sqlTabTool2(ResultSet RS, String[][] cols) throws Exception
	{
		if(RS==null || RS.isAfterLast())
			throw new Exception("sqlTabTool2.Err: RS null!!");
			this.RS=RS;
			this.cols=cols;
	}
	
	public sqlTabTool2(ResultSet RS, String[][] cols, Map obj) throws Exception
	{
		if(RS==null || RS.isAfterLast())
			throw new Exception("sqlTabTool2.Err: RS null!!");
			this.RS=RS;
			this.cols=cols;
			objs=obj;
	}
	
	public int setSplit(String st, String en)
	{	return setSplit(st, en, ""); }
	public int setSplit(String st, String en, String sp)
	{
		this.startTag=st;
		this.endTag=en;
		this.splitTag=sp;
		return 1;
	}
	
	public int setKey(String key)
	{
		keys=new String[]{key};
		return 1;
	}
	public int setKey(String key[])
	{
		keys=Arrays.copyOf(key, key.length);
		return 1;
	}
	
	public int csvMode()
	{
		tabMode=1;
		return setSplit("\"", "\"", ",");
	}
	public int tableMode()
	{
		tabMode=0;
		return setSplit("<td>", "</td>", "\n");	
	}
	

	public void clear()
	{
		n=0;
		sumPool=new TreeMap();
	}
	
	public String getHead() throws Exception
	{
		StringBuffer r=new StringBuffer();
		for(int i=0; i<cols.length; i++)
		{
			r.append((i>0?splitTag:"")+startTag);
			r.append(cols[i].length>2 && cols[i][2].length()>0 ? cols[i][2] : "#"+(i+1));
			r.append(endTag);
		}
		return r.toString().replace("td>", "th>");
	}
	
	public String getBlankHead() 
	{	return "<tr>"+getBlankTh(0)+"</tr>";	}
	public String getBlankHead(int offset) 
	{	return "<tr>"+getBlankTh(offset)+"</tr>";	}
	public String getBlankTh()
	{	return getBlankTh(0); }
	public String getBlankTh(int offset)
	{
		StringBuffer s=new StringBuffer();
		for(int i=0, i2=cols.length+offset; i<i2; i++)
			s.append("<th></th>");
		return s.toString();
	}
	
	public String getBlankRow()
	{	return "<tr>"+getBlankTd(0)+"</tr>";	}
	public String getBlankRow(int offset) 
	{	return "<tr>"+getBlankTd(offset)+"</tr>";	}
	public String getBlankTd()
	{	return getBlankTd(0); }
	public String getBlankTd(int offset) 
	{
		StringBuffer s=new StringBuffer();
		for(int i=0, i2=cols.length+offset; i<i2; i++)
			s.append("<td></td>");
		return s.toString();
	}
	
	
/**
mode:
R:row num, I:int, F:float, D:date, T:time, t:sec time, L:list(array[] map()), B:boolean
*/
	public String getRow() throws Exception //I F P T 
	{
		StringBuffer r0=new StringBuffer();
		String mode="", col="";
		int i=0, st=0;
		for(; i<cols.length; i++)
		{
			StringBuffer r=new StringBuffer();
//			r.append((i>0?splitTag:"")+startTag);
			int aln=1;
			String cmd[]=cols[i];
			try{
				if(cmd[0].length()==0){}
				else if(cmd.length==1 || cmd[1].indexOf("S")==0 || cmd[1].equals("")) 				//String (force)
				{
					String t2[]=cmd[0].split(" "), t="";
					for(int j=0; j<t2.length; j++)
						t+=((RS.getString(t2[j])==null?"":" "+RS.getString(t2[j])));
					if(t.length()>0)
						t=t.substring(1);
					
					if(cmd[1].indexOf(" H")>=0)
						t=StrTool.htmlTxt(t);
					int idx=cmd[1].indexOf(" L");
					if(idx>0)
					{
						int l=Parser.toInt(StrTool.cutStr(cmd[1], " L", " "));
						if(t.length()>l) r.append(t.substring(0, l)+"...");
						else r.append(t);
					}
					else r.append(t);
				}
				else if(cmd[1].indexOf("R")==0)									//row
				{
					r.append(n+1);
					aln=3;
				}
				else if(cmd[1].indexOf("L")==0)										// List
				{
					if(objs==null) r.append("objs unInit!!");
					else if(objs.get(cmd[0])==null) r.append("obj["+cmd[0]+"] unInit!!");
					else
					{
						String v="";
						String cName=objs.get(cmd[0]).getClass().getName();
						if(objs==null) r.append("obj unInit!!");
						else if(cName.equals("java.util.TreeMap")) //map
						{
							TreeMap<Integer, String> tmp=(TreeMap<Integer, String>)objs.get(cmd[0]);
							int a=RS.getInt(cmd[0]);
							v=(tmp.get(a));
						}
						else if(cName.equals("[Ljava.lang.String;")) //arr[]
						{
							String[] tmp=(String[])objs.get(cmd[0]);
							int a=RS.getInt(cmd[0]);
							v=(tmp[a]);
						}
						else if(cName.equals("[[Ljava.lang.String;")) //arr[][]
						{
							String tmp[][]=(String[][])objs.get(cmd[0])
								, a=RS.getString(cmd[0]), r2="";
							for(int j=0; j<tmp.length; j++)
							{
								if(tmp[j][0].equals(a))
								{
									r2=tmp[j][1];
									break;
								}
								v=(r2);
							}
						}
						else v=("Nuknow type: ["+cName+"]");
						
						r.append(v==null?"":v);
					}
				}
				else if(cmd[1].indexOf("I")==0)									//int
				{
					mode="I";
					aln=3;
					st=0;
					String t2[]=cmd[0].split(" ");
					int v=0;
					for(int j=0; j<t2.length; j+=2) //int
					{
						st=1;
						int v2=Parser.toInt(t2[j], -999999);
						st=2;
						if(v2==-999999) v2=RS.getInt(col=t2[j]);
						st=3;
						if(j==0)	v=v2;
						else if(t2[j-1].equals("+")) v+=v2;
						else if(t2[j-1].equals("-")) v-=v2;
						else if(t2[j-1].equals("*")) v*=v2;
						else if(t2[j-1].equals("/")) v=(v2==0?0:v/v2);
						else if(t2[j-1].equals("%")) v%=v2;
					}
					if(cmd[1].indexOf("IN", 1)>0 && keys!=null)
					{
						String rID="";
						for(int a=0; a<keys.length; a++)
							rID+=","+RS.getString(keys[a]);
						r.append(HtmlTool.Input.num(cmd[0]+rID, v, "style='width:100%;'") );
					}
					else
						r.append(String.format("%,d", v));
					Integer a=(Integer)sumPool.get(i);
					if(a==null)	a=new Integer(v);
					else	a+=v;
					sumPool.put(i, a);
					st=4;
				}
				else if(cmd[1].indexOf("F")==0) 								//float F INP
				{
					String t2[]=cmd[0].split(" ");
					double v=0.0;
					aln=3;
					for(int j=0; j<t2.length; j+=2)
					{
						double v3=Parser.toDouble(t2[j], -999999.0);
						if(v3==-999999.0) v3=RS.getDouble(t2[j]);
						if(j==0)	v=v3;
						else if(t2[j-1].equals("+")) v+=v3;
						else if(t2[j-1].equals("-")) v-=v3;
						else if(t2[j-1].equals("*")) v*=v3;
						else if(t2[j-1].equals("/")) v=(v3==0.0?0.0:v/v3);
						else if(t2[j-1].equals("%")) v%=v3;
					}
					
					int b=cmd[1].indexOf("F"), c=1;
					char f=cmd[1].charAt(b+1);
					if(f>'0' && f<='9') c=f-'0';
					String s, s2="";
					if(cmd[1].indexOf("IN", 1)>1 && keys!=null)					//INPUT
					{
						String rID="";
						for(int a=0; a<keys.length; a++)
							rID+=","+RS.getString(keys[a]);
						r.append(HtmlTool.Input.num(cmd[0]+rID, String.format("%."+c+"f", v), "style='width:100%;'") );
					}
					else
						r.append(String.format("%,."+c+"f", v));

					Double a=(Double)sumPool.get(i);

					if(a==null)	a=new Double(v);
					else	a+=(Double)v;
					sumPool.put(i, a);
				}
				else if(cmd[1].indexOf("P")==0 || cmd[1].indexOf("R")==0)			//percent
				{
					String t2[]=cmd[0].split(" ");
					Double v=0.0;
					aln=3;
					for(int j=0; j<t2.length; j+=2)
					{
						double v3=Parser.toDouble(t2[j], -999999.0);
						if(v3==-999999.0) v3=RS.getDouble(t2[j]);
						if(j==0)	v=v3;
						else if(t2[j-1].equals("+")) v+=v3;
						else if(t2[j-1].equals("-")) v-=v3;
						else if(t2[j-1].equals("*")) v*=v3;
						else if(t2[j-1].equals("/")) v=(v3==0.0?0.0:v/v3);
						else if(t2[j-1].equals("%")) v%=v3;
					}

					int f=(cmd[1].length()>1 ? Parser.toInt(cmd[1].charAt(1)+""):0);
					r.append(String.format("%."+f+"f%%", v*100));

					Double a=(Double)sumPool.get(i);
					if(a==null)	a=new Double(v);
					else	a+=(Double)v;
					sumPool.put(i, a);
				}
				else if(cmd[1].indexOf("D")==0)									// date
				{
					String t2[]=cmd[1].split(" "), t3[]=cmd[1].split("\\|");
					if(RS.getTimestamp(cmd[0])==null){}
					else if(t3.length>1 && t3[0].length()>1)
					{
						java.text.SimpleDateFormat sdf= new java.text.SimpleDateFormat(t3[0].substring(1));
						r.append(sdf.format(RS.getTimestamp(cmd[0])));
					}
					else
						r.append(RS.getString(cmd[0]));
						
				}
				else if(cmd[1].indexOf("T")==0)									// time
				{
					String t=RS.getString(cmd[0]);
					if(t==null)
					{
						r.append("-");
					}
					else
					{
						String ti[]=t.split("\\D");
						aln=3;
						int[] a=(int[])sumPool.get(i);
						if(a==null)	a=new int[3];
						else
						{
							for(int j=0, k=ti.length-1; j<ti.length; j++, k--)
								a[k]+=Parser.toInt(ti[j]);
						}
						sumPool.put(i, a);
						r.append(RS.getTime(cmd[0]));
					}
				}
				else if(cmd[1].indexOf("t")==0 && cmd.length>2)//sec time
				{
					int sec=RS.getInt(cmd[0]);
					aln=3;
					Integer a=(Integer)sumPool.get(i);
					if(a==null)	a=new Integer(0);
					a+=sec;
					sumPool.put(i, a);
					int ti[]=new int[5], lv=cmd[1].length()>1?Parser.toInt(cmd[1].charAt(1)+"", 3):3;
					
					for(int j=0; j<lv-1; j++)
					{
						ti[j]=sec%60;
						sec/=60;
					}
					ti[2]=sec;
					
					
					String tL[]=cmd[1].split("\\|"), s=tL.length>1?tL[1]:"HH:mm:ss";
					s=s.replace("DDD", String.format("%03d", ti[3]))
						.replace("DD", String.format("%02d", ti[3]))
						.replace("HH", String.format("%02d", ti[2]))
						.replace("mm", String.format("%02d", ti[1]))
						.replace("ss", String.format("%02d", ti[0]))
						.replace("D", ti[3]+"")
						.replace("H", ti[2]+"")
						.replace("m", ti[1]+"")
						.replace("s", ti[0]+"");
					
					r.append(s);
				}
				else if(cmd[1].indexOf("B")==0) 																//bool
				{
					String val=RS.getString(cmd[0]), s="Y"
						, falseL="||f|false|0|x|n|no|否|ｘ|Ｘ|╳|×|";
					
					if(val==null || (val.length()>0 && falseL.indexOf("|"+val.toLowerCase()+"|")>=0))
						s="N";
					
					r.append(s);
				}
//old
				else if(cmd[1].equals("IMAP"))												// TreeMap<Integer, String>
				{
					if(objs==null) r.append("objs unInit!!");
					else if(objs.get(cmd[0])==null) r.append("obj["+cmd[0]+"] unInit!!");
					else if(objs.get(cmd[0]).getClass().getName().equals("java.util.TreeMap"))
					{
						TreeMap<Integer, String> tmp=(TreeMap<Integer, String>)objs.get(cmd[0]);
						int a=RS.getInt(cmd[0]);
						if(tmp==null) r.append("data null!!");
						else r.append(tmp.get(a));
					}
					else r.append("Nuknow type: ["+objs.getClass().getName().equals("java.util.TreeMap")+"]");
				}
				else if(cmd[1].equals("A"))								//array
				{
					if(objs==null) r.append("objs unInit!!");
					else if(objs.get(cmd[0])==null) r.append("obj["+cmd[0]+"] unInit!!");
					else
					{
						String tmp[]=(String[])objs.get(cmd[0]);
						int a=RS.getInt(cmd[0]);
						if(tmp==null)
						{
							r.append("obj null!");
						}
						else if(a>=0 && a<tmp.length)
							r.append(tmp[a]);
						else	r.append("overflow! ["+a+"]");
					}
				}
				else															//def
				{
					String t2[]=cmd[0].split(" "), t="";
					for(int j=0; j<t2.length; j++)
						t+=((RS.getString(t2[j])==null?"":" "+RS.getString(t2[j])));
					if(t.length()>0)
						t=t.substring(1);
					
					if(cmd[1].indexOf(" H")>=0)
						t=StrTool.htmlTxt(t);
					
					int idx=cmd[1].indexOf(" L");
					if(idx>0)
					{
						int l=Parser.toInt(StrTool.cutStr(cmd[1], " L", " "));
						if(t.length()>l) r.append(t.substring(0, l)+"...");
						else r.append(t);
					}
					else r.append(t);
					
				}
			}
			catch(Exception e)
			{	r.append(i+mode+" "+st+" "+col+": "+e.toString());	}
			
			String stT=(i>0?splitTag:"")+startTag;
			if(cmd.length>1 && cmd[1].indexOf("SF")==0)	stT=stT.replace("\"", "=\"");

			if(tabMode==0&&cmd[1].indexOf(" NW")>0)	stT=stT.replace(">", " nowrap>");
			if(tabMode==0&&cmd[1].indexOf(" AR")>0)	aln=3;
			if(tabMode==0&&cmd[1].indexOf(" AC")>0)	aln=2;
			if(tabMode==0&&cmd[1].indexOf(" AL")>0)	aln=1;
			if(aln==3)	stT=stT.replace("<td", "<td align=right");
			else if(aln==2)	stT=stT.replace("<td", "<td align=center");
			if(cmd.length>3 && Parser.toInt(cmd[3])>0) stT=stT.replace("<td", "<td width="+cmd[3]);

			r0.append(stT).append(r).append(endTag);
		}
		this.n++;
		return r0.toString();
	}
	
	public String sumLabel="總計";
	public String getSum() //throws Exception
	{
		StringBuffer r0=new StringBuffer();
		for(int i=0; i<cols.length; i++)
		{
			StringBuffer r=new StringBuffer();
			int aln=1;
			String cmd[]=cols[i];
			char c0=cmd[1].length()>0?cmd[1].charAt(0):'S';
			if(cmd==null || cmd[0].length()==0 || cmd.length==1) {}
			else if(c0=='R' || cmd[1].indexOf("K")>0)																	//row
			{
				r.append(sumLabel);
				aln=3;
			}
			else if(cmd[1].indexOf("S")==-1) r.append("-");
			else if(c0=='I')
			{
				aln=3;
				Integer a=(Integer)sumPool.get(i);
				r.append(a==null?"0":String.format("%,d", a));
			}
			else if(c0=='F')
			{
				aln=3;
				Double a=(Double)sumPool.get(i);
				if(a==null)
					r.append(0.0);
				else
				{
					int b=cmd[1].indexOf("F"), c=-1;
					String s;
					if(cmd[1].length()>b+1 && (s=cmd[1].charAt(b+1)+"").matches("\\d")) 
						r.append(String.format("%,."+Parser.toInt(s)+"f", a));
					else	r.append(a);
				}
			}
			else if(cmd[1].indexOf("P")==0)
			{
				aln=3;
				Double a=(Double)sumPool.get(i);
				int f=(cmd[1].length()>1 ? Parser.toInt(cmd[1].charAt(1)+""):0);
				if(a==null)	r.append("0%");
				else	r.append(String.format("%."+f+"f%%", a*100/this.n));
			}
			else if(cmd[1].indexOf("T")==0)
			{
				aln=3;
				int a[]=(int[])sumPool.get(i), tp=Parser.toInt(cmd[1].charAt(1)+"");
				if(tp==0) tp=3;
				for(int j=0; j<tp-1; j++)
				{
					if(a[j]>=60)
					{
						a[j+1]+=a[j]/60;
						a[j]%=60;
					}
				}
				int j=tp-1;
				String s="";
				for(; j>=0; j--) s+=String.format(":%02d", a[j]);
				r.append(s.substring(1));
			}
			else if(cmd[1].indexOf("t")==0 && cmd.length>2)
			{
				aln=3;
					Integer a=(Integer)sumPool.get(i);
					if(a==null)	a=new Integer(0);
					int b=a;
					int[] ti=new int[3];
					for(int j=0; j<2; j++)
					{
						ti[j]=b%60;
						b/=60;
					}
					ti[2]=b;
					
					String tL[]=cmd[1].split("\\|"), s=tL.length>1?tL[1]:"HH:mm:ss";
					s=s.replace("HH", String.format("%02d", ti[2]))
						.replace("mm", String.format("%02d", ti[1]))
						.replace("ss", String.format("%02d", ti[0]))
						.replace("H", ti[2]+"")
						.replace("m", ti[1]+"")
						.replace("s", ti[0]+"");
					
					r.append(s);
				
			}
			else r.append("-");

			String stT=(i>0?splitTag:"")+startTag;
			if(aln==3)	stT=stT.replace("<td", "<td align=right");
			else if(aln==2)	stT=stT.replace("<td", "<td align=left");

			r0.append(stT).append(r).append(endTag);
		}
		return r0.toString();
	}
	
	public String avgLabel="平均";
	public String getAvg() throws Exception
	{
		StringBuffer r0=new StringBuffer();
		for(int i=0; i<cols.length; i++)
		{
			int aln=1;
			StringBuffer r=new StringBuffer();
			String cmd[]=cols[i];
			if(cmd[0].length()==0 || cmd.length==1) {}
			else if(n==0 || cmd[1].indexOf("R")==0)					//row
			{	r.append(avgLabel);
				aln=3;
			}
			else if(cmd[1].indexOf("I")==0 && cmd[1].indexOf("A")>0)
			{
				Integer a=(Integer)sumPool.get(i);
				r.append(a==null?0:String.format("%,.2f", a*1.0/this.n));
			}
			else if(cmd[1].indexOf("F")==0 && cmd[1].indexOf("A")>0)
			{
				Double a=(Double)sumPool.get(i);
				if(a==null)
					r.append(0.0);
				else
				{
					int b=cmd[1].indexOf("F"), c=-1;
					String s="2";
					int s2=2;
					if(cmd[1].length()>b+1 && (s=cmd[1].charAt(b+1)+"").matches("\\d")) s2= Parser.toInt(s);
					r.append(String.format("%,."+s2+"f", a/this.n));
				}
			}
			else if(cmd[1].indexOf("P")==0 && cmd[1].indexOf("A")>0)
			{
				Double a=(Double)sumPool.get(i);
				int f=(cmd[1].length()>1 ? Parser.toInt(cmd[1].charAt(1)+""):0);
				if(a==null)	r.append("0%");
				else	r.append(String.format("%."+f+"f%%", a*100/this.n));
			}
			else if(cmd[1].indexOf("T")==0 && cmd[1].indexOf("A")>0)
			{
				int a[]=(int[])sumPool.get(i), tp=0;
				for(int j=0; j<2; j++)
				{
					if(a[j]>=60)
					{
						a[j+1]+=a[j]/60;
						a[j]%=60;
					}
				}
				int j=2;
				while(j>0 && a[j]==0) j--;
				String s="";
				for(; j>=0; j--) s+=String.format(":%02d", a[j]);
				r.append(s.substring(1));
			}
			else if(cmd[1].indexOf("t")==0 && cmd[1].indexOf("A")>0 && cmd.length>2)
			{
				aln=3;
					Integer a=(Integer)sumPool.get(i);
					if(a==null)	a=new Integer(0);
					a/=this.n;
					int b=a;
					int[] ti=new int[3];
					for(int j=0; j<2; j++)
					{
						ti[j]=b%60;
						b/=60;
					}
					ti[2]=b;
					
					String s=cmd[2];
					s=s.replace("HH", String.format("%2d", ti[2]))
						.replace("mm", String.format("%2d", ti[1]))
						.replace("ss", String.format("%2d", ti[0]))
						.replace("H", ti[2]+"")
						.replace("m", ti[1]+"")
						.replace("s", ti[0]+"");
					
					r.append(s);
				
			}
			else r.append("-");

			String stT=(i>0?splitTag:"")+startTag;
			if(aln==3)	stT=stT.replace("<td", "<td align=right");
			else if(aln==2)	stT=stT.replace("<td", "<td align=left");
			r0.append(stT).append(r).append(endTag);
		}
		return r0.toString();
	}
	
	public String getRate(int total)
	{
		StringBuffer r=new StringBuffer();
		for(int i=0; i<cols.length; i++)
		{
			r.append(startTag);
			String cmd[]=cols[i];
			if(cmd[0].length()==0 || cmd.length==1) {}
			else if(cmd[1].indexOf("I")==0 && cmd[1].indexOf("A")>0)
			{
				Integer a=(Integer)sumPool.get(i);
				r.append(a==null?0:String.format("%.2f%%", a*100.0/this.n/total));
			}
			else if(cmd[1].indexOf("F")==0 && cmd[1].indexOf("A")>0)
			{
				Double a=(Double)sumPool.get(i);
				if(a==null)	r.append(0.0);
				else
				{
					int b=cmd[1].indexOf("F"), c=2;
					String s;
					if(cmd[1].length()>b+1 && (s=cmd[1].charAt(b+1)+"").matches("\\d"))	c=Parser.toInt(s);
					
					r.append(String.format("%."+c+"f%%", 100*a/this.n/total));
				}
			}
			else if(cmd[1].indexOf("P")==0 && cmd[1].indexOf("A")>0)
			{
				Double a=(Double)sumPool.get(i);
				int f=(cmd[1].length()>1 ? Parser.toInt(cmd[1].charAt(1)+""):0);
				if(a==null)	r.append("0%");
				else	r.append(String.format("%."+f+"f%%", a*100/this.n));
			}
			else r.append("-");
			r.append(endTag);
		}
		return r.toString();
	}
	
}

/*
	public String getCol(int col)
	{
		if(col<0 || col>=cols.length) return "overflow!["+col+"]";
		
		StringBuffer r=new StringBuffer();
		int i=col;
		
			r.append(startTag);
			try
			{
				String cmd[]=cols[i];
				if(cmd[0].length()==0){}
				else if(cmd.length==1) r.append(RS.getString(cmd[0])==null?"":RS.getString(cmd[0]));
				else if(cmd[1].indexOf("I")==0)
				{
					String t2[]=cmd[0].split(" ");
					int v=0;
					for(int j=0; j<t2.length; j+=2) //int
					{
						//"-?\\d+"
						int v2=Parser.toInt(t2[j], -999999);
						if(v2==-999999) v2=RS.getInt(t2[j]);
						if(j==0)	v=v2;
						else if(t2[j-1].equals("+")) v+=v2;
						else if(t2[j-1].equals("-")) v-=v2;
						else if(t2[j-1].equals("*")) v*=v2;
						else if(t2[j-1].equals("/")) v=(v2==0?0:v/v2);
						else if(t2[j-1].equals("%")) v%=v2;
					}
					r.append(String.format("%,d", v));
					Integer a=(Integer)sumPool.get(i);
					if(a==null)	a=new Integer(v);
					else	a+=v;
					sumPool.put(i, a);
				}
				else if(cmd[1].indexOf("F")==0) //float
				{
					String t2[]=cmd[0].split(" ");
					double v=0.0;
					for(int j=0; j<t2.length; j+=2)
					{
						double v3=Parser.toDouble(t2[j], -999999.0);
						if(v3==-999999.0) v3=RS.getDouble(t2[j]);
						if(j==0)	v=v3;
						else if(t2[j-1].equals("+")) v+=v3;
						else if(t2[j-1].equals("-")) v-=v3;
						else if(t2[j-1].equals("*")) v*=v3;
						else if(t2[j-1].equals("/")) v=(v3==0.0?0.0:v/v3);
						else if(t2[j-1].equals("%")) v%=v3;
					}
					
					int b=cmd[1].indexOf("F"), c=-1;
					String s, s2="";
					if(cmd[1].length()>b+1 && (s=cmd[1].charAt(b+1)+"").matches("\\d")) r.append(String.format("%,."+Parser.toInt(s)+"f", v));
					else	r.append(v);
					
					Double a=(Double)sumPool.get(i);
					if(a==null)	a=new Double(v);
					else	a+=(Double)v;
					sumPool.put(i, a);
				}
				else if(cmd[1].indexOf("P")==0) //percent
				{
					String t2[]=cmd[0].split(" ");
					Double v=0.0;
					for(int j=0; j<t2.length; j+=2)
					{
						double v3=Parser.toDouble(t2[j], -999999.0);
						if(v3==-999999.0) v3=RS.getDouble(t2[j]);
						if(j==0)	v=v3;
						else if(t2[j-1].equals("+")) v+=v3;
						else if(t2[j-1].equals("-")) v-=v3;
						else if(t2[j-1].equals("*")) v*=v3;
						else if(t2[j-1].equals("/")) v=(v3==0.0?0.0:v/v3);
						else if(t2[j-1].equals("%")) v%=v3;
					}
					
					int f=(cmd[1].length()>1 ? Parser.toInt(cmd[1].charAt(1)+""):0);
					r.append(String.format("%."+f+"f%%", v*100));
	//				r.append(new Double(100*v).intValue()+"%");
					
					Double a=(Double)sumPool.get(i);
					if(a==null)	a=new Double(v);
					else	a+=(Double)v;
					sumPool.put(i, a);
				}
				else if(cmd[1].indexOf("T")==0)
				{
					String ti[]=RS.getString(cmd[0]).split("\\D");
					int[] a=(int[])sumPool.get(i);
					if(a==null)	a=new int[3];
					else
					{
						for(int j=0, k=ti.length-1; j<ti.length; j++, k--)
							a[k]+=Parser.toInt(ti[j]);
					}
					sumPool.put(i, a);
					r.append(RS.getString(cmd[0]));
				}
				else if(cmd[1].indexOf("t")==0)
				{
					int sec=RS.getInt(cmd[0]);
					Integer a=(Integer)sumPool.get(i);
					if(a==null)	a=new Integer(0);
					a+=sec;
					sumPool.put(i, a);
					int[] ti=new int[3];
					for(int j=0; j<2; j++)
					{
						ti[j]=sec%60;
						sec/=60;
					}
					ti[2]=sec;
					
					String s=cmd[2];
					s=s.replace("HH", String.format("%02d", ti[2]))
						.replace("mm", String.format("%02d", ti[1]))
						.replace("ss", String.format("%02d", ti[0]))
						.replace("H", ti[2]+"")
						.replace("m", ti[1]+"")
						.replace("s", ti[0]+"");
					
					r.append(s);
				}
				else if(cmd[1].equals("A"))
				{
					if(objs==null) r.append("obj null!!");
					else
					{
						String tmp[]=(String[])objs.get(cmd[0]);
						int a=RS.getInt(cmd[0]);
						if(tmp==null)
						{
							r.append("obj null!");
						}
						else if(a>=0 && a<tmp.length)
							r.append(tmp[a]);
						else	r.append("overflow! ["+a+"]");
					}
				}
				else if(cmd[1].equals("IMAP"))
				{
					TreeMap<Integer, String> tmp=(TreeMap<Integer, String>)objs.get(cmd[0]);
					int a=RS.getInt(cmd[0]);
					if(tmp==null)
					{
						r.append("obj null!");
					}
					else 
						r.append(tmp.get(a));
					
				}
				else r.append(RS.getString(cmd[0])==null?"":RS.getString(cmd[0]));
			}
			catch(Exception e)
			{	r.append(e.toString());	}
			r.append(endTag);
		
		return r.toString();
		
	}

*/

%>