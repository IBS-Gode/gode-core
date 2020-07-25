package org.ibs.cds.gode.pagination;


public class Sortable {

    private Type sortType;
    private String field;
    public Sortable(Type sortType, String field) {
        this.sortType = sortType;
        this.field = field;
    }

    public static Sortable by(String field) {
        return new Sortable(Type.ASC, field);
    }

    public static Sortable by(Type type, String field) {
        return new Sortable(type == null ? Type.ASC : type, field);
    }

    public Type getSortType() {
        return sortType;
    }

    public void setSortType(Type sortType) {
        this.sortType = sortType;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public enum Type {
        ASC,
        DESC;
    }
}
