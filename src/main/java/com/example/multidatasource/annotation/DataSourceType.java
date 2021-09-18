package com.example.multidatasource.annotation;

import lombok.Getter;

@Getter
public enum DataSourceType {
    SOURCE_1("ds1", "数据源1-默认数据源"),
    SOURCE_2("ds2", "数据源2");

    DataSourceType(String source, String desc) {
        this.source = source;
        this.desc = desc;
    }

    String source;

    String desc;

    /*
     * @param hashKey
     * @return
     */
    public static String getByKey(String hashKey){
        //根据hashkey来获取所需要的数据源
        int i = Math.abs(hashKey.hashCode()) % DataSourceType.values().length;
        return DataSourceType.values()[i].getSource();
    }

}
