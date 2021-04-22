package com.wbr.model.mem.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 功能: [POI实现把数据库数据导出到Excel]
 *  一对一 和 一对多导出不考虑
 * 作者: JML
 * 版本: 1.0
 */
public class ExportExcel {
	private static Log log = LogFactory.getLog(ExportExcel.class);
	private static Pattern matPat = Pattern.compile("^\\d+(\\.\\d+)?$");

	/**
	 * 功能: 导出为Excel工作簿
	 * 参数: sheetName[工作簿中的一张工作表的名称]
	 * 参数: titleName[表格的表头名称(code)]
	 * 参数: headers[表格每一列的列名标题]
	 * 参数: dataSet[要导出的数据源]
	 * 参数: pattern[时间类型数据的格式]
	 */
	public static void exportExcel(String sheetName,String titleName,String[] headers,String[] code,Collection<?> dataSet,String pattern,HttpServletResponse response) {

		doExportExcel(sheetName,titleName,headers,code,dataSet,pattern, response);

	}
	/**
	 * 功能:真正实现导出
	 */
	private static void doExportExcel(String sheetName,String titleName,String[] headers,String[] code,Collection<?> dataSet,String pattern,HttpServletResponse response) {
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 生成一个工作表
		HSSFSheet sheet = workbook.createSheet(sheetName);

		HSSFRow row;
		setHeaderAndTitleStyle(workbook, titleName, headers,code, sheet); // 设置表头和标题样式
		HSSFCellStyle dataSetStyle = getDefaultContentStyle(workbook); // 设置[表中数据]样式
		// 创建表中数据行-增加样式-赋值
		if(dataSet!=null){
			Iterator<?> it = dataSet.iterator();
			int index = 2;
			int lastSheetIndex = 0;
			while (it.hasNext()) {
				if(index > 50000){//数据超过5万将会分页显示
					lastSheetIndex++;
					sheet = workbook.createSheet(sheetName + lastSheetIndex);
					setHeaderAndTitleStyle(workbook, titleName, headers,code, sheet);
					index = 1;
				}
				index++;
				row = sheet.createRow(index);   //创建行
				Object t = it.next();
				if (null != code) {
					// 利用反射，根据参数设置标题顺序
					setDataByParameter(code, pattern, workbook, dataSetStyle,row, t, t.getClass());
				}else {
					// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
					setDataByBean(pattern, workbook, dataSetStyle, row, t);
				}
			}
		}
		//输出流
		downloadExcel(response,workbook,sheetName);
	}
	
	private static void setDataByBean(String pattern,HSSFWorkbook workbook, HSSFCellStyle dataSetStyle, HSSFRow row,Object t) {
		Field[] fields = t.getClass().getDeclaredFields();                                                 //获取属性
		for (short i = 0; i < fields.length; i++) {
			@SuppressWarnings("deprecation")
			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(dataSetStyle);
			Field field = fields[i];
			String fieldName = field.getName();
			String getMethodName = "get"+ fieldName.substring(0, 1).toUpperCase()+ fieldName.substring(1);//获取属性的get方法
			try {
				@SuppressWarnings("rawtypes")
				Class tCls = t.getClass();
				@SuppressWarnings("unchecked")
				Method getMethod = tCls.getMethod(getMethodName,new Class[] {});
				Object value = getMethod.invoke(t, new Object[] {});                                      //获取对象属性值

				// 如果是时间类型,按照格式转换
				String textValue = null;
				if (value instanceof Date) {
					Date date = (Date) value;
					SimpleDateFormat sdf = new SimpleDateFormat(pattern);
					textValue = sdf.format(date);
				} else if(false){//关联对象 value instanceof bfpValue
					Method getMethod1 = value.getClass().getMethod("getName",new Class[] {});
					Object value1 = getMethod1.invoke(value, new Object[] {});
					textValue=value1.toString();
				} else if(value.equals(true)){
					textValue = "是";	
				}  else if(value.equals(false)){
					textValue = "否";	
				}else{
					// 其它数据类型都当作字符串简单处理
					textValue = value.toString();
				}

				// 利用正则表达式判断textValue是否全部由数字组成
				if (textValue != null) {
					Matcher matcher = matPat.matcher(textValue);
					if (matcher.matches()) {
						// 是数字当作double处理
						cell.setCellValue(Double.parseDouble(textValue));
					} else {
						// 不是数字做普通处理
						cell.setCellValue(textValue);
					}
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} finally {
				//清理资源
				 try {
 		             workbook.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        } 
			}
		}
	}
	private static void setDataByParameter(String[] code,
			String pattern, HSSFWorkbook workbook, HSSFCellStyle dataSetStyle,
			HSSFRow row, Object t, Class tc) {
		for (int i = 0; i < code.length; i++) {
			try {
				@SuppressWarnings("deprecation")
				HSSFCell cell = row.createCell(i);
				cell.setCellStyle(dataSetStyle);
				String str=code[i];
				Object value = null;
				if(t instanceof Map){//map类型直接取值
					value=((Map)t).get(str);
				}else{
					String methodName="get"+str.substring(0, 1).toUpperCase() + str.substring(1);
					Method getMethod = tc.getMethod(methodName,new Class[] {});
					value = getMethod.invoke(t, new Object[] {});
				}
				// 如果是时间类型,按照格式转换
				String textValue = null;
				if(value != null){
					 if (value instanceof Date) {
						 Date date = (Date) value;
						 SimpleDateFormat sdf = new SimpleDateFormat(pattern);
						 textValue = sdf.format(date);
					 } else if(false){//关联对象 value instanceof bfpValue
						 Method getMethod1 = value.getClass().getMethod("getName",new Class[] {});
						 Object value1 = getMethod1.invoke(value, new Object[] {});
						 textValue=value1.toString();
					 } else if(value.equals(true)){
						 textValue = "是";
					 } else if(value.equals(false)){
						 textValue = "否";
					 } else if(false){//子集 value instanceof Collection
						 Collection coll=(Collection) value;
						 String names="";
						 for (Iterator it= coll.iterator();it.hasNext();) {
							 Object object= it.next();
							 Class ccc =object.getClass();
							 Method getLineMethod = ccc.getMethod("getName",new Class[] {});
							 Object valueLine = getLineMethod.invoke(object, new Object[] {});
							 String name=backValue(textValue,valueLine,pattern);
							 names+=name+",";
						 }
						 if (!"".equals(names)) {
							 textValue=names.substring(0, names.length()-1);
						 }
					 } else{
						 // 其它数据类型都当作字符串简单处理
						 textValue = value.toString();
					 }
				}
				// 利用正则表达式判断textValue是否全部由数字组成
				if (textValue != null) {
					Matcher matcher = matPat.matcher(textValue);
					if (matcher.matches()) {
						// 是数字当作double处理
						cell.setCellValue(Double.parseDouble(textValue));
					} else {
						// 不是数字做普通处理
						cell.setCellValue(textValue);
					}
				}
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}	 catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	/**
     * 提示用户下载excel
     * @param response 响应对象
     * @param fileName excel文件名
     * create by ronghui.xiao @2015-8-5
     */
    public static void downloadExcel(HttpServletResponse response,HSSFWorkbook workbook, String fileName){
        try{
            if(StringUtils.isNotBlank(fileName)){
                fileName = new String(fileName.getBytes("gb2312"), "iso8859-1");                 //给文件名重新编码
            }else{
                fileName = "excel";
            }
			response.reset();// 清空输出流
//			response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setContentType("text/html;charset=utf-8");                                  //设置响应编码
            response.setContentType("application/x-msdownload");                                 //设置为文件下载
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName + ".xls");   //设置响应头信息
            OutputStream outputStream = response.getOutputStream();                              //创建输出流

			try {
				workbook.write(outputStream);  // 将数据写出去
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				outputStream.flush();
				outputStream.close();
			}

        }catch (IOException e){
            log.error(e);
        }
    }
    /**
     * 返回对应value属性的值
     * @param textValue
     * @param value
     * @param dateType
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
	 private static  String backValue(String textValue,Object value,String dateType) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
	        if (value instanceof Date) {
				Date date = (Date) value;
				SimpleDateFormat sdf = new SimpleDateFormat(dateType);
				textValue = sdf.format(date);
			} else if(false){//关联对象 value instanceof bfpValue
				Method getMethod1 = value.getClass().getMethod("getName",new Class[] {});
				Object value1 = getMethod1.invoke(value, new Object[] {});
				textValue=value1.toString();
			} else if(value.equals(true)){
				textValue = "是";	
			} else if(value.equals(false)){
				textValue = "否";	
			} else {
				// 其它数据类型都当作字符串简单处理
				textValue = value.toString();
			} 
	        return textValue;
	    }
	   /**
		 * 设置表头和标题
		 * @param titleName 表头名称
		 * @param headers 标题
		 * @param sheet
		 */
		private static void setHeaderAndTitleStyle(HSSFWorkbook workbook,String titleName,String[] headers, String[] codes , HSSFSheet sheet) {
			HSSFCellStyle titleStyle = getDefaultTitleStyle(workbook);// 设置[标题]样式

		 	HSSFCellStyle headersStyle = getDefaultHeadersStyle(workbook);// 设置[表头]样式
		 	HSSFCellStyle hiddenStyle = getDefaultContentStyle(workbook);// 设置[隐藏行]样式
			// 设置工作表默认列宽度为20个字节
			sheet.setDefaultColumnWidth((short) 20);
			//在工作表中合并首行并居中
			sheet.addMergedRegion(new CellRangeAddress(0,0,0,headers.length-1));
			//创建标题行-增加样式-赋值
			HSSFRow titleRow = sheet.createRow(0);
			HSSFCell titleCell = titleRow.createCell(0);
			titleCell.setCellStyle(titleStyle);
	 		titleCell.setCellValue(titleName);
			// 创建列首-增加样式-赋值
			HSSFRow row = sheet.createRow(1);
			if(null != headers){
				for (short i = 0; i < headers.length; i++) {
					@SuppressWarnings("deprecation")
					HSSFCell cell = row.createCell(i);
					cell.setCellStyle(headersStyle);
					HSSFRichTextString text = new HSSFRichTextString(headers[i]);
					cell.setCellValue(text);
				}
			}
			//创建隐藏列 设置code
			if(null != codes){
				HSSFRow hiddenRow = sheet.createRow(2);
				for (short i = 0; i < codes.length; i++) {
					@SuppressWarnings("deprecation")
					HSSFCell cell = hiddenRow.createCell(i);
					cell.setCellStyle(hiddenStyle);
					HSSFRichTextString text = new HSSFRichTextString(codes[i]);
					cell.setCellValue(text);
				}
				hiddenRow.setZeroHeight(true);//设置隐藏
			}
		}
	    /*获取表头样式*/
		public static HSSFCellStyle getDefaultTitleStyle(HSSFWorkbook workbook){
	        // 创建[表头]样式
			HSSFCellStyle titleStyle = workbook.createCellStyle();
			// 设置[表头]样式
			titleStyle.setFillForegroundColor(HSSFColor.WHITE.index);     //设置背景色
			titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			titleStyle.setBorderBottom(BorderStyle.THIN);                 //设置底部边线
			titleStyle.setBorderLeft(BorderStyle.DASH_DOT_DOT);           //设置左部边线
			titleStyle.setBorderRight(BorderStyle.THIN);                  //设置右部边线
			titleStyle.setBorderTop(BorderStyle.THIN);                    //设置顶部边线
			titleStyle.setAlignment(HorizontalAlignment.CENTER);          //表头内容水平居中
			//创建[表头]字体
			HSSFFont titleFont = workbook.createFont();
			//设置[表头]字体
			titleFont.setColor(HSSFColor.BLACK.index);
			titleFont.setFontHeightInPoints((short) 15);
			titleFont.setBold(true);                                    //粗体
		    titleFont.setFontName("微软雅黑");                          //微软雅黑
			titleStyle.setFont(titleFont);
		    return titleStyle;
	    }
	    /*获取默认列首样式*/
	    public static HSSFCellStyle getDefaultHeadersStyle(HSSFWorkbook workbook) {
			// 创建[列首]样式
			HSSFCellStyle headersStyle = workbook.createCellStyle();
			// 设置[列首]样式
			headersStyle.setFillForegroundColor(HSSFColor.WHITE.index);		 //设置背景色
		 	headersStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND); 
			headersStyle.setBorderBottom(BorderStyle.THIN);                  //设置底部边线
			headersStyle.setBorderLeft(BorderStyle.DASH_DOT_DOT);            //设置左部边线
			headersStyle.setBorderRight(BorderStyle.THIN);                   //设置右部边线
			headersStyle.setBorderTop(BorderStyle.THIN);                     //设置顶部边线
			headersStyle.setAlignment(HorizontalAlignment.CENTER);           //内容水平居中
			//创建[列首]字体
			HSSFFont headersFont = workbook.createFont();
			//设置[列首]字体
			headersFont.setFontHeightInPoints((short) 12);
		 	headersFont.setBold(true);
			// 把[列首字体]应用到[列首样式]
			headersStyle.setFont(headersFont);
			return headersStyle;
		}
	    /*获取默认内容样式*/
	    public static HSSFCellStyle getDefaultContentStyle(HSSFWorkbook workbook){
	        HSSFCellStyle contentStyle = workbook.createCellStyle();
	        contentStyle.setBorderBottom(BorderStyle.THIN);                 //设置底部边线
	        contentStyle.setBorderLeft(BorderStyle.DASH_DOT_DOT);           //设置左部边线
	        contentStyle.setBorderRight(BorderStyle.THIN);                  //设置右部边线
	        contentStyle.setBorderTop(BorderStyle.THIN);                    //设置顶部边线
	        contentStyle.setAlignment(HorizontalAlignment.CENTER);          //表头内容水平居中
	        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);    //表头内容垂直居中
	        HSSFFont contentFont = workbook.createFont();                   //定义内容字体样式
	        contentFont.setColor(HSSFColor.BLACK.index);                  	//字体颜色
	        contentFont.setFontHeightInPoints((short) 9);                	//字号
	        contentFont.setBold(false);                                		//普通粗细
	        contentFont.setFontName("微软雅黑");                        		//微软雅黑
	        contentStyle.setFont(contentFont);                          	//设置该字体样式
	        contentStyle.setWrapText(true);
	        return contentStyle;
	    }
}