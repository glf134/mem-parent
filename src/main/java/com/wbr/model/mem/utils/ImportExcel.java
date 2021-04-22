package com.wbr.model.mem.utils;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 功能: [POI实现把Excel数据导入到数据库]
 * 作者: glf
 * 版本: 1.0
 */
public class ImportExcel{
    private static Log log = LogFactory.getLog(ImportExcel.class);
    //正则表达式 用于匹配属性的第一个字母
    private static final String REGEX = "[a-zA-Z]";
    /**
     * 功能: Excel数据导入到数据库
     * 参数: requestFile 请求流
     * 参数: startRow[从第几行开始]
     * 参数: endRow[到第几行结束
     *                  (0表示所有行;
     *                  正数表示到第几行结束;
     *                  负数表示到倒数第几行结束)]
     * 参数: clazz[要返回的对象集合的类型] 为null 则返回 map对象
     */
    public static List<?> importExcel(MultipartFile requestFile, Class<?> clazz) throws IOException {
        //是否打印提示信息
        boolean showInfo=true;
        return doImportExcel(requestFile,2,0,showInfo,clazz);
    }
    public static List<?> importExcelByRow(MultipartFile requestFile, int startRow, int endRow, Class<?> clazz) throws IOException {
        //是否打印提示信息
        boolean showInfo=true;
        return doImportExcel(requestFile,startRow,endRow,showInfo,clazz);
    }
    /**
     * 功能:真正实现导入
     */
    private static List<Object> doImportExcel(MultipartFile requestFile,int startRow,int endRow,boolean showInfo,Class<?> clazz) throws IOException {
        if(requestFile == null){
           return null;
        }
        List<String> hiddenCodes =new ArrayList<String>();
        String fileName = requestFile.getOriginalFilename();
        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
            log.error("上传文件格式不正确");
        }
        HSSFWorkbook wb = null;
        InputStream fis = null;
        List<Row> rowList = new ArrayList<Row>();
        try {
            fis = requestFile.getInputStream();
            // 去读Excel
            wb = new HSSFWorkbook(fis);
            for (int k = 0; k < wb.getNumberOfSheets(); k++) {
            	Sheet sheet = wb.getSheetAt(k);
            	// 获取最后行号
            	int lastRowNum = sheet.getLastRowNum();
            	if (lastRowNum > 0) { // 如果>0，表示有数据
            		out("\n开始读取名为【" + sheet.getSheetName() + "】的内容：",showInfo);
            	}
            	Row row = null;
            	// 循环读取
            	for (int i = startRow; i <= lastRowNum + endRow; i++) {
            		row = sheet.getRow(i);
            		if (row != null) {
            			//判断是否为隐藏行
            			if(row.getZeroHeight()){
            				for (int j = 0; j < row.getLastCellNum(); j++) {
                				String value = getCellValue(row.getCell(j));
                				if (!"".equals(value)) {
                					hiddenCodes.add(value);//保存隐藏行code
                				}
                			}
                		}else{
                			rowList.add(row);
                			out("第" + (i + 1) + "行：",showInfo,false);
                			// 获取每一单元格的值
                			for (int j = 0; j < row.getLastCellNum(); j++) {
                				String value = getCellValue(row.getCell(j));
                				if (!"".equals(value)) {
                					out(value + " | ",showInfo,false);
                				}
                			}
                			out("",showInfo);
                		}
            			
            		}
            	}
			}
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
             wb.close();
        }
        return returnObjectList(hiddenCodes,rowList,clazz);
    }

    /**
     * 功能:获取单元格的值
     */
    private static String getCellValue(Cell cell) {
        Object result = "";
        if (cell != null) {
            switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                result = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                result = cell.getNumericCellValue();
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                result = cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_FORMULA:
                result = cell.getCellFormula();
                break;
            case Cell.CELL_TYPE_ERROR:
                result = cell.getErrorCellValue();
                break;
            case Cell.CELL_TYPE_BLANK:
                break;
            default:
                break;
            }
        }
        return result.toString();
    }

    /**
     * 功能:返回指定的对象集合
     */
    private static List<Object> returnObjectList(List<String> hiddenCodes,List<Row> rowList,Class<?> clazz) {
        if(rowList == null || rowList.size() == 0){
            return null;
        }
        List<Object> objectList=null;
        Object obj=null;
        String attribute=null,value=null;
        try {   
            objectList=new ArrayList<Object>();
            if(clazz != null){ //clazz 不等于null则封装成实体对象
                Field[] declaredFields = clazz.getDeclaredFields();
                for (Row row : rowList) {
                    obj = clazz.newInstance();
                    if(null != hiddenCodes){                  //判断集合中是否存在隐藏的自定义code
                        for (int j=0; j<hiddenCodes.size(); j++){
                            value = getCellValue(row.getCell(j));
                            attribute=hiddenCodes.get(j);
                            setAttrributeValue(obj,attribute,value);
                        }
                    }else{
                        int j=0;
                        for (Field field : declaredFields) {
                            if(null != hiddenCodes){                  //判断集合中是否存在隐藏的自定义code
                                attribute=hiddenCodes.get(j);
                            }else{                                    //否则根据class的属性定义
                                attribute=field.getName().toString();
                            }
                            value = getCellValue(row.getCell(j));
                            setAttrributeValue(obj,attribute,value);
                            j++;
                        }
                    }
                    objectList.add(obj);
                }
            }else{
                Map map=null;
                for (Row row : rowList) {
                    for (int j=0; j<hiddenCodes.size(); j++){
                        value = getCellValue(row.getCell(j));
                        attribute=hiddenCodes.get(j);
                        map=new HashMap<>();
                        map.put(attribute,value);
                        objectList.add(map);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objectList;
    }

    /**
     * 功能:给指定对象的指定属性赋值
     */
    private static void setAttrributeValue(Object obj,String attribute,String value) {
        if(StringUtils.isBlank(value)){
            return;
        }
        //得到该属性的set方法名
        String methodName = convertToMethodName(attribute,obj.getClass(),true);
        Method[] methods = obj.getClass().getMethods();
        for (Method method : methods) {
            /**
             * 因为这里只是调用bean中属性的set方法，属性名称不能重复
             * 所以set方法也不会重复，所以就直接用方法名称去锁定一个方法
             * （注：在java中，锁定一个方法的条件是方法名及参数）
            */
            if(method.getName().equals(methodName))
            {
                Class<?>[] parameterC = method.getParameterTypes();
                try {
                    /**如果是(整型,浮点型,布尔型,字节型,时间类型),
                     * 按照各自的规则把value值转换成各自的类型
                     * 否则一律按类型强制转换(比如:String类型)
                    */
                    if(parameterC[0] == int.class || parameterC[0]== Integer.class)
                    {
                        value = value.substring(0, value.lastIndexOf("."));
                        method.invoke(obj,Integer.valueOf(value));
                        break;
                    }else if(parameterC[0] == float.class || parameterC[0]== Float.class){
                        method.invoke(obj, Float.valueOf(value));
                        break;
                    }else if(parameterC[0] == double.class || parameterC[0]== Double.class)
                    {
                        method.invoke(obj, Double.valueOf(value));
                        break;
                    }else if(parameterC[0] == byte.class || parameterC[0]== Byte.class)
                    {
                        method.invoke(obj, Byte.valueOf(value));
                        break;
                    }else if(parameterC[0] == boolean.class|| parameterC[0]== Boolean.class)
                    {
                    	if ("是".equals(value)) {
                    		method.invoke(obj, Boolean.valueOf(true));
						}else{
							method.invoke(obj, Boolean.valueOf(false));
						    
						} 
                        break;
                    }else if(parameterC[0] == BigDecimal.class|| parameterC[0]== BigDecimal.class)
                    {
                    	if(null == value || "".equals(value)){
                    		method.invoke(obj, BigDecimal.ZERO);
                    	}else{
                    		method.invoke(obj, new BigDecimal(value));
                    	}
                        break;
                    }else if(false)//关联  bfpValue.class.isAssignableFrom(parameterC[0])
                    {
                    	 
                    }else if(false)//集合 Collection.class.isAssignableFrom(parameterC[0])
                    {
                    	 
                    }else if(parameterC[0] == Date.class)
                    {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date date=null;
                        try {
                            date=sdf.parse(value);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        method.invoke(obj,date);
                        break;
                    }else
                    {
                        method.invoke(obj,parameterC[0].cast(value));
                        break;
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 功能:根据属性生成对应的set/get方法
     */
    private static String convertToMethodName(String attribute,Class<?> objClass,boolean isSet) {
        /** 通过正则表达式来匹配第一个字符 **/
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(attribute);
        StringBuilder sb = new StringBuilder();
        /** 如果是set方法名称 **/
        if(isSet)
        {
            sb.append("set");
        }else{
        /** get方法名称 **/
            try {
                Field attributeField = objClass.getDeclaredField(attribute);
                /** 如果类型为boolean **/
                if(attributeField.getType() == boolean.class||attributeField.getType() == Boolean.class)
                {
                    sb.append("is");
                }else
                {
                    sb.append("get");
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        /** 针对以下划线开头的属性 **/
        if(attribute.charAt(0)!='_' && m.find())
        {
            sb.append(m.replaceFirst(m.group().toUpperCase()));
        }else{
            sb.append(attribute);
        }
        return sb.toString();
    }

    /**
     * 功能:输出提示信息(普通信息打印)
     */
    private static void out(String info, boolean showInfo) {
        if (showInfo) {
            log.info(info + (showInfo ? "\n" : ""));
        }
    }

    /**
     * 功能:输出提示信息(同一行的不同单元格信息打印)
     */
    private static void out(String info, boolean showInfo, boolean nextLine) {
        if (showInfo) {
            if(nextLine)
            {
                log.info(info + (showInfo ? "\n" : ""));
            }else
            {
                log.info( info );
            }
        }
    }
}