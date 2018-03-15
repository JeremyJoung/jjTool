package jjTool;

import java.io.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;


public class ImageTool
{
	public static String zoomImage(String inFile, String outFile, int width, int height)
	{
		try
		{
			File zoomFile = new File(outFile), inFile0 = new File(inFile), par=zoomFile.getParentFile() ;
			if(!inFile0.exists()) return "Error : no File!!";
			if(par==null || !par.exists()) par.mkdirs();
			
			BufferedImage Bi = ImageIO.read(inFile0); 
			if ((Bi.getHeight()>height) || (Bi.getWidth()>width))
			{
				double rW=width*1.0/Bi.getWidth(), rH=height*1.0/Bi.getHeight(), Ratio = rW<rH?rW:rH; 

				Image Itemp = Bi.getScaledInstance(width, height, Image.SCALE_SMOOTH); //畫布
				AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(Ratio, Ratio), null); //建立變焦
				Itemp = op.filter(Bi, null); //製圖
					ImageIO.write((BufferedImage)Itemp, "jpg", zoomFile); //寫入
					Icon ret = new ImageIcon(zoomFile.getPath());  //讀取測試
			}
			else
			{
				InputStream inStream = new FileInputStream(inFile); 
				FileOutputStream fs = new FileOutputStream(outFile);
				byte[] buffer = new byte[65536];
				int length, byteread;
				while ( (byteread = inStream.read(buffer))>0)
					fs.write(buffer, 0, byteread);
				inStream.close();
				fs.close();
			}
			return zoomFile.getPath();
		}
		catch (Exception ex)
		{	return ("Error : " +ex); }
	}

}