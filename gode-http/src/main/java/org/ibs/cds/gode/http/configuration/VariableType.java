package org.ibs.cds.gode.http.configuration;

import java.util.ArrayList;

/**
 *
 * @author manugraj
 */
public enum VariableType {

    String(String.class){
        @Override
        public String value(String value) {
            return value;
        }
    },
    Number(Long.class){
        @Override
        public Long value(String value) {
            return Long.valueOf(value);
        }
    },
    Decimal(Double.class){
        @Override
        public Double value(String value) {
            return Double.valueOf(value);
        }
    },
    Boolean(Boolean.class){
        @Override
        public Boolean value(String value) {
            return java.lang.Boolean.valueOf(value);
        }
    },
    Array(ArrayList.class){
     @Override
        public Boolean value(String value) {
            return java.lang.Boolean.valueOf(value);
        }
    
    };
    
    private final Class<?> classType;

    public Class getClassType() {
        return classType;
    }
    VariableType(Class<?> classType){
        this.classType = classType;
    }
    
    public abstract <T> T value(String value);
    
    public  <T> T value(Object value){
        return  (T) this.getClassType().cast(value);
    };
}
