<%@ page import = "java.util.zip.*, java.io.*, java.util.*"%>
<%! //ZipTool
/**
@version 20180315
*/
public class ZipTool // extends java.util.zip.ZipOutputStream
{
	public ZipOutputStream zos;
	HttpServletRequest req;
	public ZipTool() throws Exception
	{
		throw new Exception("ZipTool.err: Must give a zip file/path!!");
	}
	
	public ZipTool(String zip) throws Exception
	{
		FileOutputStream fos = new FileOutputStream(getServletContext().getRealPath(zip));
		zos=new ZipOutputStream(fos);
	}
	
	public ZipTool(File zip) throws Exception
	{
		FileOutputStream fos = new FileOutputStream(zip);
		zos=new ZipOutputStream(fos);
	}
	
	public ZipTool(FileOutputStream fos) throws Exception
	{
		zos=new ZipOutputStream(fos);
	}
	
//

	public int put(String file) throws Exception
	{	return put(new File(getServletContext().getRealPath(file)), ""); }
	public int put(String file, String path) throws Exception
	{	return put(new File(getServletContext().getRealPath(file)), path); }
	
	public int put(File file) throws Exception
	{	return put(file, ""); }
	public int put(File file, String path) throws Exception
	{
		if(file==null || !file.exists()) return 0;
		
		if(path==null) path="";
		if(!path.equals("") && path.charAt(path.length()-1)!='\\' && path.charAt(path.length()-1)!='/') path+="\\";
		
		
		if(file.isDirectory())
		{
			path+=file.getName()+"/";
			int num=0;
			File[] fL=file.listFiles();
			for(int i=0; i<fL.length; i++)
			{
				if(fL[i].isDirectory())
					num+=put(fL[i], path+fL[i].getName()+"/");
				else
				{
					put(fL[i], path);
					num++;
				}
				
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
//
	public void close() throws Exception
	{
		this.zos.closeEntry();
		this.zos.close();
	}
}




%>
<%!

void zipFiles(List<File> fileList, String destZipFile) 
{
	byte[] buffer = new byte[1024];
	try {
		FileOutputStream fos = new FileOutputStream(destZipFile);
		ZipOutputStream zos = new ZipOutputStream(fos);
		System.out.println("Creating Zip Archive : " + destZipFile);
		for (File file : fileList) {
			System.out.println("Added " + file);
			ZipEntry ze = new ZipEntry(file.getName());
			
			ze.setTime(file.lastModified());
			zos.putNextEntry(ze);
			FileInputStream in = new FileInputStream(file);

			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}
			in.close();
		}
		zos.closeEntry();
		zos.close();
		System.out.println("Done");
	} catch (IOException ex) {
		ex.printStackTrace();
	}
}

%>