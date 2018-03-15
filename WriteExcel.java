package jjTool;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import jxl.*;
import java.sql.*;
import jxl.write.*;
import jxl.format.*;
import jxl.write.biff.*;

public class WriteExcel 
{

	private String _inputFile;
	public WritableSheet _sheet;
	public WritableWorkbook _workbook;
	public File _file;
  
	public WriteExcel(String inputFile) throws Exception
	{	init(inputFile, "sheet0", true);	}
	public WriteExcel(String inputFile, String sName) throws Exception
	{	init(inputFile, sName, true);	}
	public WriteExcel(String inputFile, boolean replace) throws Exception
	{	init(inputFile, "sheet0", replace);	}
	public void init(String inputFile, String sheetName, boolean replace) throws Exception
	{
		File file = new File(inputFile), par=file.getParentFile();
		if(par!=null && !par.exists())
			par.mkdirs();
		_inputFile = inputFile;
		if(replace && file.exists())
		{
			file.delete();
			file = new File(inputFile);
		}
		_file=file;
		//WorkbookSettings wbSettings = new WorkbookSettings();
		//wbSettings.setLocale(new Locale("en", "EN"));
		WritableWorkbook workbook = _workbook = Workbook.createWorkbook(file); //, wbSettings);
		
//		workbook.createSheet("Report", 0); //index0
		WritableSheet excelSheet = _sheet = addSheet(0, sheetName);
	}
	
	public WritableSheet addSheet(int idx)
	{	return addSheet(idx, "Sheet"+idx); }
	public WritableSheet addSheet(int idx, String sName)
	{
		_workbook.createSheet(sName, idx); //index0
		return _sheet = _workbook.getSheet(idx);
	}
	
	public WritableSheet getSheet(int idx)
	{	return _sheet = _workbook.getSheet(idx);	}
	
	public File getFile()
	{	return _file;	}

	public void Fin() throws IOException, WriteException 
	{
		_workbook.write();
		_workbook.close();
	}

	
	public void label(int column, int row, String txt) throws Exception
	{	label(_sheet, column, row, txt, null); }
	public void label(int column, int row, String txt, WritableCellFormat fmt) throws Exception
	{	label(_sheet, column, row, txt, fmt); }
	public void label(WritableSheet sheet, int column, int row, String txt) throws Exception
	{	label(sheet, column, row, txt, null); }
/** 
@param sheet	(WritableSheet) Writed sheet.(Option)
@param column	(int) Label column number. Start from 0.
@param row	(int) Label row number. Start from 0.
@param txt	(String) Label value.
@param fmt	(WritableCellFormat) Cell format.(Option)
@throws Exception	Any exception.
*/
	public void label(WritableSheet sheet, int column, int row, String txt, WritableCellFormat fmt) throws Exception
	{
		if(sheet==null) sheet=_sheet;
		Label label = fmt==null 
		? new Label(column, row, txt)
		: new Label(column, row, txt, fmt);
		sheet.addCell(label);
	}

	public void number(int column, int row, double val) throws Exception 
	{	number(_sheet, column, row, val, null); }
	public void number(WritableSheet sheet, int column, int row, double val) throws Exception 
	{	number(sheet, column, row, val, null); }
	public void number(WritableSheet sheet, int column, int row, double val, WritableCellFormat fmt) throws Exception 
	{
		if(sheet==null) sheet=_sheet;
		jxl.write.Number number = fmt==null
		?new jxl.write.Number(column, row, val)
		:new jxl.write.Number(column, row, val, fmt);
		sheet.addCell(number);
	}

	
//image
	public int[] getXY(int column, int row)
	{	return getXY(_sheet, column, row);	}
	public int[] getXY(Sheet sheet, int column, int row)
	{
		if(sheet==null)	return new int[]{-1, -1};
		int x=0, y=0;
		for(int i=0; i<column; i++)
			x+=sheet.getColumnView(i).getSize();

		for(int i=0; i<row; i++)
			y+=sheet.getRowView(i).getSize();
		
		return new int[]{x, y};
	}

	public void imageCR(int column, int row, String path) throws Exception 
	{	imageCR(_sheet, column, row, 1, 1, new File(path));	}
	public void imageCR(int column, int row, File f) throws Exception 
	{	imageCR(_sheet, column, row, 1, 1, f);	}
	public void imageCR(double column, double row, double width, double height, String path) throws Exception
	{	imageCR(_sheet, column, row, width, height, new File(path));	}
	public void imageCR(double column, double row, double width, double height, File f) throws Exception
	{	imageCR(_sheet, column, row, width, height, f);	}
	public void imageCR(WritableSheet sheet, double column, double row, double width, double height, File f) throws Exception 
	{
		if(!f.exists()) return;
		WritableImage img =new WritableImage(column, row, width, height, f);
		img.setImageAnchor(WritableImage.MOVE_WITH_CELLS);
		
		sheet.addImage(img);
	}

	
	
	public String get(int col, int row)
	{	return get(_sheet, col, row); }
	public String get(Sheet sheet, int col, int row)
	{
		return sheet.getCell(col, row).getContents();
	}
	
	
	public int loadSql(String[][] cfg, ResultSet RS, int row) throws Exception 
	{
		if(cfg==null) return -1;
		int n=0;
		
//head
		for(int c=0; c<cfg.length; c++)
			label(c, row, cfg[c][0]);
		
		for(int r=row+1; RS.next(); r++, n++)
		{
			for(int c=0; c<cfg.length; c++)
			{
				if(cfg[c][0].equals("") || cfg[c].length<2 || cfg[c][1].equals("")) continue;
				if(cfg[c].length<3 || cfg[c][2].equals(""))
					label(c, r, RS.getString(cfg[c][1]));
				else if(cfg[c][2].equals("I"))
					number(c, r, RS.getDouble(cfg[c][1]));
				else
					label(c, r, RS.getString(cfg[c][1]));
			}
		}
		
		return n;
	}

//--------------------------------  
	private WritableCellFormat timesBoldUnderline;
	private WritableCellFormat times;
  



} 