package jjTool;
import java.util.*;
import java.io.*;
import javax.servlet.http.*;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
//import org.apache.commons.fileupload.DiskFileUpload;
//import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;


public class UploadTool
{
	public HashMap<String, ArrayList<String>> formMap=new HashMap<String, ArrayList<String>>();
	public ArrayList<FileItem> fileList = new ArrayList<FileItem>();
	public boolean err=false, hasPost=false;
	public String errMsg="";
	
	public UploadTool(HttpServletRequest req, HttpServlet jsp, String enc)
	{
		try{
			if(!ServletFileUpload.isMultipartContent(req))
			{
				this.err=true;
				this.errMsg="not Multipart!!";
				return;
			}

DiskFileItemFactory factory = new DiskFileItemFactory();
factory.setSizeThreshold(20485760);
File temp=new File("temp/");
temp.mkdir();
factory.setRepository(temp);
ServletFileUpload upload = new ServletFileUpload(factory);
upload.setSizeMax(20485760);
			Iterator iter = null;

			try
			{	iter = upload.parseRequest(req).iterator(); }	// Parse the request
			catch (FileUploadException ex)
			{	this.errMsg=ex.toString();
				this.err=true;
				return;
			}

			FileItem item = null;
			String fieldName="";
			for(int i=0; iter.hasNext(); i++) 
			{
				item = (FileItem) iter.next();
				hasPost=true;

				if (item.isFormField()) //¸ê®ÆÅª¨ú
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
			this.err=true;
			this.errMsg=e.toString();
//			print1(e.toString());
		}
	}
	public UploadTool(HttpServletRequest req, HttpServlet jsp)
	{	this(req, jsp, "utf-8"); }
	
//string
	public String[] gets(String name)
	{
		ArrayList<String> t = formMap.get(name);
		if(t==null)	return new String[0];
		else	return (String[])t.toArray();
	}
	
	public String get(String name)
	{	return this.get(name, ""); }
	public String get(String name, String ifNull)
	{
		ArrayList<String> t = formMap.get(name);
		if(t==null)	return ifNull;
		else	return (String)t.get(0);
	}
	
	public String getSql(String name)
	{	return this.getSql(name, ""); }
	public String getSql(String name, String ifNull)
	{
		ArrayList<String> t = formMap.get(name);
		if(t==null)	return ifNull;
		else	return StrTool.sqlVal(t.get(0));
	}
	
	public String[] getSqls(String name)
	{
		ArrayList<String> t =formMap.get(name);
		if(t==null)	return new String[0];
		else
		{	String[] s = (String[])t.toArray();
			for(int i=0; i<s.length; i++)
				s[i]=StrTool.sqlVal(s[i]);
			return s;
		}
	}
	
//int
	public int getInt(String name)
	{	return getInt(name, 0); }
	public int getInt(String name, int ifNull)
	{
		ArrayList<String> t = formMap.get(name);
		if(t==null)	return ifNull;
		else	return Parser.toInt(t.get(0), ifNull);
	}
	
	public int[] getInts(String name, int ifNull)
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

//date
	public Date getDate(String name)
	{	return Parser.toDate(this.get(name));	}
	
	
//file

	public String fileType(int col)
	{
		if(col<0 || col>=fileList.size())
		{
			this.err=true;
			this.errMsg="out of index. ["+col+"]";
			return null;
		}
		FileItem item=fileList.get(col);
		String fType=item.getName().substring(item.getName().lastIndexOf(".")+1).toLowerCase();

		return fType;
	}
	
	public int fileCount()
	{	return fileList.size();	}
	
	public File saveFile(int col, String path)
	{	return saveFile(col, new File(path));	}
	public File saveFile(int col, File f)
	{
		if(col<0 || col>=fileList.size())
		{
			this.err=true;
			this.errMsg="out of index. ["+col+"]";
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
				this.err=true;
				this.errMsg=e.toString();
				return null;
			}
		}
		return null;
	}

}
