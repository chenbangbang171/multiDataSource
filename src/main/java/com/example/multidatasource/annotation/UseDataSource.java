package com.example.multidatasource.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * multiData anno
 * Created by hzlaojiaqi on 2017/12/26.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UseDataSource {

     /**
      * use member in method to calculate hash key then choose the dataSource
      *
      * @return
      */
     boolean memberHash() default false;



     /**
      * use spel expression to calculate hash key then choose the dataSource
      * @return
      */
     String  hashExp() default "";


     /**
      * assign a dataSource key,this key is in {@link com.example.multidatasource.aop.DataSourceAsp}
      * targetDataSource map
      * @return
      */
     String source() default "";


     /**
      * 数据源
      * @return
      */
     DataSourceType value() default DataSourceType.SOURCE_1;


     /**
      * 是否使用hashkey,若为true,则使用对应字段的哈希值进行计算，选择数据源，
      * 且指定的{@link DataSourceType}不起作用
      * @return
      */
     boolean useHashKey() default false;


}
