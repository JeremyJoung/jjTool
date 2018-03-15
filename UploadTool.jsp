<%@ page import = "org.apache.commons.fileupload.*"%>
<%@ page import = "org.apache.commons.fileupload.servlet.ServletFileUpload"%>
<%@ page import = "org.apache.commons.fileupload.disk.DiskFileItemFactory"%>
<%@ page import = "org.apache.commons.fileupload.DiskFileUpload"%>
<%@ page import = "org.apache.commons.fileupload.FileItem"%>
<%@ page import = "org.apache.commons.io.FilenameUtils"%>
<%@ page import = "java.io.*, java.util.*, java.text.*" %>
<%@ page import = "jjTool.*" %>
<%! //class UploadTool 
public class UploadTool
{
	public TreeMap<String, ArrayList<String>> formMap=new TreeMap<String, ArrayList<String>>();
	public ArrayList<FileItem> fileList = new ArrayList<FileItem>();
	public boolean err=false, hardEx=false, hasPost=false;
	public String errMsg="";
	public int fileSize=204857600;
	public String tempPath="/temp/";
	
	public UploadTool(HttpServletRequest req, HttpServlet jsp, String enc) throws Exception
	{
		try
		{
			if(!ServletFileUpload.isMultipartContent(req))
			{
				doEx("not Multipart!!");
				return;
			}

			File tf=new File(getServletContext().getRealPath(tempPath));
			if(!tf.exists())	tf.mkdirs();
			
DiskFileItemFactory factory = new DiskFileItemFactory();
factory.setSizeThreshold(fileSize);
factory.setRepository(tf);
ServletFileUpload upload = new ServletFileUpload(factory);
upload.setSizeMax(fileSize);

			Iterator iter = null;

			try
			{	iter = upload.parseRequest(req).iterator(); }	// Parse the request
			catch (Exception ex)
			{
				doEx(ex);
				return;
			}

			FileItem item = null;
			String fieldName="";
			for(int i=0; iter.hasNext(); i++) 
			{
				item = (FileItem) iter.next();
				hasPost=true;

				if (item.isFormField()) //資料讀取
				{
					ArrayList<String> t =formMap.get(fieldName = item.getFieldName());
					if(t==null) formMap.put(fieldName, t=new ArrayList<String>());
					t.add(item.getString(enc));
				}
				else
					fileList.add(item);
			}
		}
		catch(Exception e)
		{
			doEx(e);
		}
	}
	
	public UploadTool(HttpServletRequest req, HttpServlet jsp) throws Exception
	{	this(req, jsp, "utf-8"); }
	
//--------------------------------------------------------------string
	public String[] reqs(String name)
	{
		ArrayList<String> t = formMap.get(name);
		if(t==null)	return new String[0];
		else	return (String[])t.toArray();
	}
	
	public String req(String name)
	{	return this.req(name, ""); }
	public String req(String name, String ifNull)
	{
		ArrayList<String> t = formMap.get(name);
		if(t==null)	return ifNull;
		else	return (String)t.get(0);
	}
	
	public String reqSql(String name)
	{	return this.reqSql(name, ""); }
	public String reqSql(String name, String ifNull)
	{
		ArrayList<String> t =formMap.get(name);
		if(t==null)	return ifNull;
		else	return (String)t.get(0).replace("'", "''");
	}
	
	public String[] reqSqls(String name)
	{
		ArrayList<String> t =formMap.get(name);
		if(t==null)	return new String[0];
		else
		{	String[] s = (String[])t.toArray();
			for(int i=0; i<s.length; i++)
				s[i]=s[i].replace("'", "''");
			return s;
		}
	}
	
//-------------------------------------------------------------int
	public int reqInt(String name)
	{	return reqInt(name, 0); }
	public int reqInt(String name, int ifNull)
	{
		ArrayList<String> t = formMap.get(name);
		if(t==null)	return ifNull;
		else	return Parser.toInt(t.get(0), ifNull);
	}
	
	public int[] reqInts(String name, int ifNull)
	{
		ArrayList<String> t = formMap.get(name);
		if(t==null)	return new int[0];
		else
		{
			int t2[]=new int[t.size()];
			for(int i=0; i<t2.length; i++)
				t2[i]=Parser.toInt(t.get(i), ifNull);
			return t2;
		}
	}

//------------------------------------------------------------date
/*	
	public Date reqDate(String name)
	{
		return smartDate(this.get(name));
	}
*/
	
//--------------------------------------------------------------file

	public String fileType(int col) throws Exception
	{
		if(col<0 || col>=fileList.size())
		{
			doEx("fileType.err: out of index. ["+col+"]");
			return null;
		}
		FileItem item=fileList.get(col);
		String fType=item.getName().substring(item.getName().lastIndexOf(".")+1).toLowerCase();

		return fType;
	}
	
	public int fileCount()
	{	return fileList.size();	}
	
	public File saveFile(String path) throws Exception
	{	return saveFile(0, new File(path));	}
	public File saveFile(File f) throws Exception
	{	return saveFile(0, f); }
	public File saveFile(int col, String path) throws Exception
	{	return saveFile(col, new File(path));	}
	public File saveFile(int col, File f) throws Exception
	{
		if(col<0 || col>=fileList.size())
		{
			doEx("saveFile.err: out of index. ["+col+"]");
			return null;
		}
		FileItem item=fileList.get(col);
		long n=item.getSize();
		if(n>0)
		{
			try
			{
				item.write(f);
				return f;
			}
			catch(Exception e)
			{
				doEx(e);
				return null;
			}
		}
		return null;
	}
	
//----------------------------------------------------------Exception
	public void doEx(Exception e) throws Exception
	{
		if(hardEx)
		{
			throw e;
		}
		else
		{
			this.err=true;
			this.errMsg=e.toString();
		}
	}
	
	public void doEx(String msg) throws Exception
	{
		if(hardEx)
		{
			throw new Exception(msg);
		}
		else
		{
			this.err=true;
			this.errMsg=msg;
		}
	}

}
%>