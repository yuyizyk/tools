package cn.yuyizyk.tools.files.offices;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * 
 */
public class ExcelUtil
{
    
    @SuppressWarnings("deprecation")
	public static List<Map<String, Object>> presExcel(InputStream inStream)
    {
        // inStream = new FileInputStream(new File("F:\\q\\duty.xlsx"));
        Workbook workBook = null;
        List<Map<String, Object>> infos = new ArrayList<Map<String, Object>>();
        try
        {
            workBook = WorkbookFactory.create(inStream);
            
            Sheet sheet = workBook.getSheetAt(0);
            int numOfRows = sheet.getLastRowNum() + 1;
            Map<String, Object> dutyInfo = new HashMap<String, Object>();
            
            List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
            Map<String, Object> tempMap = new HashMap<String, Object>();
            
            List<Map<String, Object>> details = new ArrayList<Map<String, Object>>();
            Map<String, Object> dutyDetail = new HashMap<String, Object>();
            int days = 0;
            Row getrow = sheet.getRow(3);
            days = getrow.getLastCellNum() / 6;
            for (int i = 0; i < days; i++)
            {
                
                int infoIndex = 1;
                dutyInfo = new HashMap<String, Object>();
                
                for (int x = 0; x < numOfRows; x++)
                {
                    Row row = sheet.getRow(x);
                    int checkIndex = 0;
                    if (row != null)
                    {
                        boolean isNullRow = false;
                        while (checkIndex <= 6)
                        {
                            if (row.getCell(checkIndex++) != null
                                    && row.getCell(checkIndex).getStringCellValue() != null
                                    && !"".equals(row.getCell(checkIndex).getStringCellValue()))
                            {
                                isNullRow = true;
                                break;
                            }
                        }
                        if (!isNullRow && (numOfRows - 1) != x)
                        {
                            continue;
                        }
                        
                        for (int j = i * 6; j < i * 6 + 6; j++)
                        {
                            Cell cell = row.getCell(j);
                            if (x == 0)
                            {
                                String title = cell.getStringCellValue();
                                if (title != null && !"".equals(title))
                                {
                                    dutyInfo.put("DUTY_NAME", title);
                                    
                                }
                            }
                            else if (x == 1)
                            {
                                Cell cell1 = row.getCell(j);
                                if (cell1.getCellStyle().getDataFormat() == 31)
                                {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd");
                                    double value = cell.getNumericCellValue();
                                    Date date = org.apache.poi.ss.usermodel.DateUtil
                                            .getJavaDate(value);
                                    String dutyDate = sdf.format(date);
                                    dutyInfo.put("DUTY_DATE", dutyDate);
                                }
                                else
                                {
                                    cell1.setCellType(Cell.CELL_TYPE_STRING);
                                    String dutyPerson = cell1.getStringCellValue();
                                    if (dutyPerson != null && !"".equals(dutyPerson))
                                    {
                                        dutyInfo.put("DUTY_PERSON", dutyPerson);
                                    }
                                    
                                }
                            }
                            else if (x > 2)
                            {
                                Cell cell1 = row.getCell(j);
                                if (cell1 == null)
                                {
                                    /*
                                     * if (!isLast) { infoIndex = 2; isLast =
                                     * true; }
                                     */
                                    continue;
                                }
                                cell1.setCellType(Cell.CELL_TYPE_STRING);
                                if (i * 6 == j)
                                {
                                    if (cell1.getStringCellValue() != null
                                            && !"".equals(cell1.getStringCellValue()))
                                    {
                                        if (details.size() > 0)
                                        {
                                            tempMap.put("items", details);
                                            tempList.add(tempMap);
                                            tempMap = new HashMap<>();
                                            details = new ArrayList<>();
                                        }
                                        if (cell1.getStringCellValue().contains("故障原因及解决方法"))
                                        {
                                            infoIndex = 2;
                                        }
                                        else if (cell1.getStringCellValue().contains("备注及交代事宜"))
                                        {
                                            infoIndex = 3;
                                        }
                                        else
                                        {
                                            if (infoIndex == 2)
                                            {
                                                dutyInfo.put("DUTY_FAULT_REASON",
                                                        cell1.getStringCellValue());
                                                
                                            }
                                            else if (infoIndex == 3)
                                            {
                                                dutyInfo.put("DUTY_REMARK",
                                                        cell1.getStringCellValue());
                                            }
                                            else
                                            {
                                                tempMap.put("ITEM_NAME", cell1.getStringCellValue());
                                            }
                                        }
                                    }
                                }
                                else if (i * 6 + 5 == j)
                                {
                                    if (cell1.getStringCellValue() != null && infoIndex == 1
                                            && !"".equals(cell1.getStringCellValue()))
                                    {
                                        tempMap.put("ITEM_EXPLAIN", cell1.getStringCellValue());
                                    }
                                }
                                else if (i * 6 + 1 == j)
                                {
                                    if (cell1.getStringCellValue() != null && infoIndex == 1)
                                    {
                                        dutyDetail.put("DETAIL_NAME", cell1.getStringCellValue());
                                    }
                                }
                                else if (i * 6 + 2 == j)
                                {
                                    if (cell1.getStringCellValue() != null && infoIndex == 1)
                                    {
                                        dutyDetail.put("AM_TIME", cell1.getStringCellValue());
                                    }
                                }
                                else if (i * 6 + 3 == j)
                                {
                                    if (cell1.getStringCellValue() != null && infoIndex == 1)
                                    {
                                        dutyDetail.put("PM_TIME", cell1.getStringCellValue());
                                    }
                                }
                                else if (i * 6 + 4 == j)
                                {
                                    if (cell1.getStringCellValue() != null && infoIndex == 1)
                                    {
                                        dutyDetail.put("NE_TIME", cell1.getStringCellValue());
                                    }
                                    
                                    if (dutyDetail.size() > 0)
                                    {
                                        details.add(dutyDetail);
                                        dutyDetail = new HashMap<>();
                                    }
                                }
                            }
                            
                        }
                    }
                    
                }
                if (tempList.size() > 0)
                {
                    dutyInfo.put("infos", tempList);
                    tempList = new ArrayList<Map<String, Object>>();
                }
                infos.add(dutyInfo);
            }
        }
        catch (Exception e)
        {
            return null;
        }
        return infos;
        
    }

	public static void setExcelUtilScaleFactor(String pathAndName, int i) {
		// TODO Auto-generated method stub
		
	}
}
