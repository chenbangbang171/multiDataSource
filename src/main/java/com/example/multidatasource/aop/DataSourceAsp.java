package com.example.multidatasource.aop;

import com.example.multidatasource.annotation.DSKey;
import com.example.multidatasource.annotation.DataSourceType;
import com.example.multidatasource.annotation.UseDataSource;
import com.example.multidatasource.util.CustomSpelParser;
import com.example.multidatasource.util.DataSourceSwitcher;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Multiple DataSource Aspect
 * Created by hzlaojiaqi on 2017/12/26.
 */
@Aspect
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class DataSourceAsp implements InitializingBean {

    /**
     * dataSourceSwitcher
     */
    private AbstractRoutingDataSource mDataSourceSwitcher;
    /**
     * DataSource List
     */
    private List<String> mDataSourceKeys = new CopyOnWriteArrayList<>();

    /**
     * mDataSourceKeys's size
     */
    private int mKeySize;
    /**
     * local cache for speed up to find @DsKey parameter's position
     */
    private static Cache<String, Integer> LOCAL_CACHE = CacheBuilder
            .newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(50)
            .build();


    @Override
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() throws Exception {
        Preconditions.checkArgument(this.mDataSourceSwitcher != null, "mDataSourceSwitcher is null");
        Field resolvedDataSources = FieldUtils.getField(mDataSourceSwitcher.getClass(), "resolvedDataSources", true);
        Preconditions.checkArgument(resolvedDataSources != null, "resolvedDataSources is null");
        Map<Object, DataSource> dataSourceMap = (Map<Object, DataSource>) resolvedDataSources.get(mDataSourceSwitcher);
        dataSourceMap.forEach((k, v) -> {
            this.mDataSourceKeys.add(String.valueOf(k));
        });
        this.mKeySize = mDataSourceKeys.size();
        Preconditions.checkArgument(mKeySize != 0, "dataSource size is 0!");
    }


    /**
     * pointcut 定义一个切点，所有使用UseDataSource这个注解的方法都会执行这个切点相关的方法
     */
    @Pointcut("@annotation(com.example.multidatasource.annotation.UseDataSource)")
    public void useDataSource() {
    }

    /**
     * core method
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("useDataSource() && @annotation(anno)")
    public Object dataSourceSwitcher(ProceedingJoinPoint joinPoint, UseDataSource anno) throws Throwable {
        String ds="";
        //判断注解中的useHashKey属性，如果为true说明需要动态切换数据源，此属性默认为false
        if(anno.useHashKey()){
            //通过getHashKeyFromMethod来获取hash值，再用getByKey来获取某一数据源
            ds= DataSourceType.getByKey(getHashKeyFromMethod(joinPoint));
        }else{
            //如果使用指定数据源，则获取注解上写好的值
            DataSourceType value = anno.value();
            ds=value.getSource();
        }
        //切换数据源
        DataSourceSwitcher.setDataSourceKey(ds);
        try {
            //执行方法
            Object result = joinPoint.proceed();
            //返回结果
            return result;
        }catch (Exception e){
            throw e;
        }finally {
            DataSourceSwitcher.setDataSourceKey(DataSourceType.SOURCE_1.getSource());
        }
    }


    /**
     * get key from joinPoint for hash
     *
     * @param joinPoint
     * @return
     */
    public String getHashKeyFromMethod(ProceedingJoinPoint joinPoint) {
        //获取方法签名
        MethodSignature signature = MethodSignature.class.cast(joinPoint.getSignature());
        //获取调用的方法
        Method method = signature.getMethod();
        Integer positionFromCache = getPositionFromCache(method);
        //获取方法的参数列表
        Object[] args = joinPoint.getArgs();
        if (positionFromCache != null) {
            return String.valueOf(args[positionFromCache]);
        }
        //获取方法的参数名
        Parameter[] declaredFields = method.getParameters();
        int index = 0;
        for (Parameter temp : declaredFields) {
            Annotation[] annotations = temp.getAnnotations();
            //对每一个参数名获取其注解
            for (Annotation anTemp : annotations) {
                //对该参数上的注解依次做判断，是不是DSKey，如果是，则代表该参数的hash值就是选取数据源的依据
                if (anTemp instanceof DSKey) {
                    putToCache(method, index);
                    return String.valueOf(args[index]);
                }
            }
            index++;
        }
        throw new IllegalArgumentException("can not get field with @DsKey annotation");
    }

    /**
     * get DataSource Key
     *
     * @param anno
     * @param joinPoint
     * @return
     */
    private String getDsKey(UseDataSource anno, ProceedingJoinPoint joinPoint) {
        String ds = "";
        String source = anno.source();
        String spel = anno.hashExp();
        //use method member
        if (anno.memberHash()) {
            int i = Math.abs(getHashKeyFromMethod(joinPoint).hashCode()) % mKeySize;
            ds = mDataSourceKeys.get(i);
        }
        // use spel expression
        else if (!StringUtils.isEmpty(spel)) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String dynamicValue = CustomSpelParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), spel);
            int i = Math.abs(dynamicValue.hashCode()) % mKeySize;
            ds = mDataSourceKeys.get(i);
            // direct use dataSource
        } else if (!StringUtils.isEmpty(source)) {
            ds = source;
            if (!isInKeyList(ds)) {
                throw new IllegalArgumentException(String.format("dataSource key %s is not in key List %s", ds, mDataSourceKeys));
            }
        }
        if (StringUtils.isEmpty(ds)) {
            throw new IllegalArgumentException("dataSource is empty!");
        }
        return ds;
    }


    /**
     * check dataSource is in source list
     *
     * @param ds
     * @return false not in,otherwise true
     */
    private boolean isInKeyList(String ds) {
        Preconditions.checkArgument(mDataSourceKeys != null, "mDataSourceKeys is not init!");
        for (String temp : mDataSourceKeys) {
            if (temp.equals(ds)) {
                return true;
            }
        }
        return false;
    }

    /**
     * put position to cache
     *
     * @param method
     * @param pos
     */
    private void putToCache(Method method, Integer pos) {
        LOCAL_CACHE.put(method.toString(), pos);
    }


    /**
     * get position from cache
     *
     * @param method
     */
    private Integer getPositionFromCache(Method method) {
        Integer value = LOCAL_CACHE.getIfPresent(method.toString());
        return value;
    }

    public void setmDataSourceSwitcher(AbstractRoutingDataSource mDataSourceSwitcher) {
        this.mDataSourceSwitcher = mDataSourceSwitcher;
    }


}
