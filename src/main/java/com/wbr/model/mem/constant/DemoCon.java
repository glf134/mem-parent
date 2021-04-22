package com.wbr.model.mem.constant;

/**
 * @author glf
 */
public class DemoCon {
    public static String tableName(String dataType){
        String table="";
        for (int i = 0; i < dataType.length(); i++) {
            if (Character.isUpperCase(dataType.charAt(i))) {
                table += "_" + Character.toLowerCase(dataType.charAt(i));
            }else{
                table += dataType.charAt(i);
            }
        }
        return table;
    }
}
