package testapi;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelReader {
    public static void main(String[] args) {
        ExcelReader obj = new ExcelReader();
        // 此处为我创建Excel路径：E:/zhanhj/studysrc/jxl下
        File file = new File("/home/zhouwei/文档/Tencent Files/1073394859/FileRecv/必修整高中合.xls");
        Map<String,String> excelmap = obj.readExcel(file);
        System.out.println("list中的数据打印出来");
        TextWriter writer = new TextWriter("tmp.txt");
        for(Map.Entry e : excelmap.entrySet())
        {
            writer.writeLines(e.getKey()+"\t"+e.getValue());
            System.out.println(e.getKey()+" "+e.getValue());
        }
        writer.close();
    }
    // 去读Excel的方法readExcel，该方法的入口参数为一个File对象
    public Map<String,String> readExcel(File file) {
        try {
            // 创建输入流，读取Excel
            InputStream is = new FileInputStream(file.getAbsolutePath());
            // jxl提供的Workbook类
            Workbook wb = Workbook.getWorkbook(is);
            // Excel的页签数量
            int sheet_size = wb.getNumberOfSheets();
            Map<String,String> outerList=new HashMap<String, String>();

            for (int index = 0; index < sheet_size; index++) {
                // 每个页签创建一个Sheet对象
                Sheet sheet = wb.getSheet(index);
                // sheet.getRows()返回该页的总行数
                for (int i = 0; i < sheet.getRows(); i++) {
                    // sheet.getColumns()返回该页的总列数
//                    for (int j = 0; j < sheet.getColumns(); j++) {
                    String name = sheet.getCell(0, i).getContents();
                    if (name.equals("")) {
                        continue;
                    }
                    String content = sheet.getCell(2, i).getContents();
                    if (content.equals("")) {
                        continue;
                    }
                    outerList.put(name, content);
                }
            }
            return outerList;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}