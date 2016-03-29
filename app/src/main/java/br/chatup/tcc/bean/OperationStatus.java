package br.chatup.tcc.bean;

/**
 * Created by jadson on 3/26/16.
 */
public enum OperationStatus {

    SUCESS("sucess"),
    ERROR("error");

    private String descr;

    private OperationStatus(String descr) {
        this.descr = descr;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

}
