package jjTool;
import java.util.zip.*;
import java.io.*;
import java.util.*;
import java.nio.charset.Charset;

/** Create zip auto tool. By give File/Folder or String data.
@version 20180315
*/
public class ZipTool // extends java.util.zip.ZipOutputStream
{
	StringBuffer execLog=new StringBuffer();
	javax.servlet.jsp.PageContext jsp0;
	String path0="";
	FileOutputStream fos0;
	bookean abPath=false;
	
/** Java zip object. */
	public ZipOutputStream zos;
	int num0=0;
	
/** This is a error method. Always throw exception. Notice user give zip source.
@throws Exception	Default exception event.
 */
	public ZipTool() throws Exception
	{
		throw new Exception("ZipTool.err: Must give a zip File or String path!!");
	}
	
/** Create a new zip file by specified [path name / File / OutputStream].
@param zip	(String) [path name / File / OutputStream] of the created zip file.<br>
If input is a String path will auto detect absolute/relative.
@throws Exception	Default exception event.
*/
	public ZipTool(String zip) throws Exception
	{
		abPath=zip.charAt(1)==':' || zip.charAt(0)=='/';
		if(abPath) path0=System.getProperty("user.dir");
		FileOutputStream fos = fos0 = new FileOutputStream(path0+(path0.equals("")?"":"/")+zip);
		zos=new ZipOutputStream(fos);
	}

	public ZipTool(String zip, javax.servlet.jsp.PageContext jsp) throws Exception
	{
		path0=(jsp0=jsp).getServletContext().getRealPath("/");
		zos=new ZipOutputStream(fos0 = new FileOutputStream(path0+"/"+zip));
	}

	public ZipTool(File zip) throws Exception
	{	zos=new ZipOutputStream(fos0 = new FileOutputStream(zip));	}
	public ZipTool(FileOutputStream fos) throws Exception
	{	zos=new ZipOutputStream(fos0 = fos);	}


/** Change zip charset and delete old.
@param cSet	(String) Charset of new zip file.
*/
	public void setCharset(String cSet)
	{	zos=new ZipOutputStream(fos0, Charset.forName(cSet));	}
	
	public void setComment(String comment)
	{	zos.setComment(comment);	}
	
//

	public int put(String file) throws Exception
	{	return put(new File((file.charAt(1)==':' || file.charAt(0)=='/'?"":path0+"/")+file), ""); }
	public int put(String file, String path) throws Exception
	{	return put(new File((file.charAt(1)==':' || file.charAt(0)=='/'?"":path0+"/")+file), path); }
	
	public int put(File file) throws Exception
	{	return put(file, ""); }
	public int put(File file, String path) throws Exception
	{
		if(file==null || !file.exists()) return 0;
		
		if(path==null) path="";
		if(!path.equals("") && path.charAt(path.length()-1)!='/' && path.charAt(path.length()-1)!='/') path+="/";
		
		
		if(file.isDirectory())
		{
			path+=file.getName()+"/";
			int num=0;
			File[] fL=file.listFiles();
			for(int i=0; i<fL.length; i++)
			{
				num+= fL[i].isDirectory() 
					? put(fL[i], path+fL[i].getName()+"/")
					: put(fL[i], path);
			}
			return num;
		}
		else
		{
			ZipEntry ze = new ZipEntry(path+file.getName());
			ze.setTime(file.lastModified());
			this.zos.putNextEntry(ze);
			FileInputStream in = new FileInputStream(file);

			int len;
			byte[] buffer = new byte[16384];
			while ((len = in.read(buffer)) > 0)
				this.zos.write(buffer, 0, len);
			in.close();
			return 1;
		}
	}
	
	public int putData(String fName, String val) throws Exception
	{	return putData(fName, val.getBytes(), 0); }
	public int putData(String fName, String val, Date modT) throws Exception
	{	return putData(fName, val.getBytes(), modT.getTime()); }
	public int putData(String fName, String val, long modT) throws Exception
	{	return putData(fName, val.getBytes(), modT); }

	public int putData(String fName, String val, String charset) throws Exception
	{	return putData(fName, val.getBytes(charset), 0); }
	public int putData(String fName, String val, String charset, Date modT) throws Exception
	{	return putData(fName, val.getBytes(charset), modT.getTime()); }
/** Put string data into zip file.

@param	fName	(String) Compressed file name in zip.
@param	val	(String) Compressed data.
@param	charset	(String) Charset of compressed string.
@param	modT	(long/Date) Last modified time label in the zip file [optionally].
@return	(int) Added file numbers. Always return 1.
@throws Exception	Default exception event.
*/
	public int putData(String fName, String val, String charset, long modT) throws Exception
	{	return putData(fName, val.getBytes(charset), modT); }

	public int putData(String fName, byte[] val) throws Exception
	{	return putData(fName, val, 0); }
	public int putData(String fName, byte[] val, Date modT) throws Exception
	{	return putData(fName, val, modT.getTime()); }
/** Put binary data into zip file.

@param	fName	(String) Compressed file name in zip.
@param	val	(byte[]) Compressed data.
@param	modT	(long/Date) Last modified time label in the zip file [optionally].
@return	(int) Added file numbers. Always return 1.
@throws Exception	Default exception event.
*/
	public int putData(String fName, byte[] val, long modT) throws Exception
	{
		ZipEntry ze = new ZipEntry(fName);
		if(modT>0)	ze.setTime(modT);
		this.zos.putNextEntry(ze);
		this.zos.write(val, 0, val.length);
		
		return 1;
	}
//
	public void close() throws Exception
	{
		this.zos.closeEntry();
		this.zos.close();
	}
}
