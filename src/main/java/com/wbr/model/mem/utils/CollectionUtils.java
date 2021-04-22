package com.wbr.model.mem.utils;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

import java.util.*;

/**
 * @author glf
 */
public class CollectionUtils {
    /**
     * 根据ognl条件取元素
     * @param c
     * @param ognlWhere
     * @return
     */
    public static Object fetchFirst(Collection c, String ognlWhere) {
        Object o = null;
        if(null!=c && ! StringUtils.isEmpty(ognlWhere)){
            for(Iterator it = c.iterator(); it.hasNext();){
                Object element = it.next();
                if(null!=element){
                    try {
                        Object b = Ognl.getValue(ognlWhere,new OgnlContext(null,null,new DefaultMemberAccess(true)),element);
                        if(null!=b && b.equals(true)){
                            o = element;
                            break;
                        }
                    } catch (OgnlException e) {
                    }
                }
            }
        }
        return o;
    }
    public static List fetch(Collection c,String expression) {
        List rs = new ArrayList();
        if(null!=c && !StringUtils.isEmpty(expression)){
            for(Iterator it=c.iterator();it.hasNext();){
                Object element = it.next();
                if(null!=element){
                    try {
                        Object b = Ognl.getValue(expression,new OgnlContext(null,null,new DefaultMemberAccess(true)),element);
                        if(null!=b && b.equals(true)){
                            rs.add(element);
                        }
                    } catch (OgnlException e) {
                    }
                }
            }
        }
        return rs.size()==0 ? null : rs;
    }
    /**
     * 根据ognl属性路径，对List排序
     */
    public static void sort(List list,String sortPropertyPath){
        sort(list,sortPropertyPath,false);
    }
    /**
     * 根据ognl属性路径，对List排序
     */
    public static void sort(List list,final String sortPropertyPath,final boolean desc){
        if(null!=list && list.size()>0){
            Collections.sort(list, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    if(!StringUtils.isEmpty(sortPropertyPath)){
                        try {
                            o1 = Ognl.getValue(sortPropertyPath, o1);
                            o2 = Ognl.getValue(sortPropertyPath, o2);
                        } catch (OgnlException e) {
                        }
                    }
                    if(desc){
                        Object temp=o1;
                        o1 = o2;
                        o2 = temp;
                    }
                    if((o1 instanceof Comparable) && (o2 instanceof Comparable) && o1.getClass()==o2.getClass()){
                        return ((Comparable)o1).compareTo(((Comparable)o2));
                    }else if((o1 instanceof Number) && (o2 instanceof Number)){
                        return ((Number)o1).doubleValue()>((Number)o2).doubleValue() ? 1 :
                                (((Number)o1).doubleValue()==((Number)o2).doubleValue() ? 0 : -1);
                    }else{
                        return 0;
                    }
                }
            });
        }
    }
    public static boolean isEmpty(Collection list) {
        return (list==null || list.isEmpty());
    }
    public static boolean isNotEmpty(Collection list) {
        return !isEmpty(list);
    }
}
