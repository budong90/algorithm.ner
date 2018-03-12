package com.wekj.ner.struct;

public class Attr {
    private String attribute = null;
    private double num = Double.NaN;
    private String numstr = null;
    private String unit = null;

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public double getNum() {
        return num;
    }

    public void setNum(double num) {
        this.num = num;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getNumstr() {
        return numstr;
    }

    public void setNumstr(String numstr) {
        this.numstr = numstr;
    }

    public boolean tryMatch(Attr other){
        if(other == null) {
            return false;
        }
        if(other.attribute == null && this.numstr == null){
            this.numstr = other.numstr;
            this.unit = other.unit;
            return true;
        }
        else if(this.attribute == null && other.numstr == null){
            this.attribute = other.attribute;
            return true;
        }

        return false;
    }

    public String toString() {
        return this.attribute + "|" + this.numstr + "|" + this.num + "|" + this.unit;
    }
}
