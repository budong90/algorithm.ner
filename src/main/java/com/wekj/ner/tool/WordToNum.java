package com.wekj.ner.tool;

import com.wekj.ner.struct.Attr;

import java.math.BigDecimal;
import java.util.*;

public class WordToNum {
    private static final String NUM_PATTERN = "[0-9\\.]+";
    private static final double DECIMAL_DELT = 0.1;
    public static final Map<String, Character> transMap = new HashMap<String, Character>();
    public static final Map<String, Integer> unitMap = new HashMap<>();
    public static final Set<String> decimalMap = new HashSet<>();
    public static final Set<String> decimalMap2 = new HashSet<>();
    public static final char EMPTY_CHAR = '&';

    static {
        transMap.put("一", '1');
        transMap.put("二", '2');
        transMap.put("两", '2');
        transMap.put("三", '3');
        transMap.put("四", '4');
        transMap.put("五", '5');
        transMap.put("六", '6');
        transMap.put("七", '7');
        transMap.put("八", '8');
        transMap.put("九", '9');
        transMap.put("零", '0');
        transMap.put("1", '1');
        transMap.put("2", '2');
        transMap.put("3", '3');
        transMap.put("4", '4');
        transMap.put("5", '5');
        transMap.put("6", '6');
        transMap.put("7", '7');
        transMap.put("8", '8');
        transMap.put("9", '9');
        transMap.put("0", '0');

        decimalMap.add("点");
        decimalMap.add("點");
        decimalMap2.add("米");
        decimalMap2.add("斤");

        unitMap.put("十", 10);
        unitMap.put("百", 100);
        unitMap.put("千", 1000);
        unitMap.put("万", 10000);
    }

    public static void trans(Attr attr){
        String numstr = attr.getNumstr();
        if(numstr == null) {
            return;
        }
        if(numstr.matches(NUM_PATTERN)){
            attr.setNum(Double.valueOf(attr.getNumstr()));
        }
        else {
            _trans(attr);
        }
    }

    private static void _trans(Attr attr){
        String numstr = attr.getNumstr();

        char[] chars = numstr.toCharArray();

        char[] temp = new char[chars.length];
        double[] unit = new double[chars.length + 1];

        Arrays.fill(temp, EMPTY_CHAR);
        Arrays.fill(unit, -1);

        double decimalPos = 1;
        for(int i = 0; i < chars.length; i++) {
            String aChar = String.valueOf(chars[i]);

            Character s = transMap.get(aChar);
            Integer unitValue = unitMap.get(aChar);

            if(s != null) {
                //===========================
                //特殊情况:“两”作为量词
                if (decimalPos < 1 && aChar.equalsIgnoreCase("两")){
                    s = EMPTY_CHAR;
                }
                //===========================
                temp[i] = s.charValue();
            }
            else {

            }

            //=================================
            //小数处理逻辑
            if(s == null && decimalMap.contains(aChar)){
                //举例 一点七八
                decimalPos = decimalPos * DECIMAL_DELT;
            }
            else if(s == null && decimalMap2.contains(aChar)){
                decimalPos = decimalPos * DECIMAL_DELT;
                //这是一种特殊情况，一米七八，一斤六两等等，单位和小数合并了
                attr.setUnit(aChar);
            }
            else if(s != null && decimalPos < 1){
                unit[i] = decimalPos;
                decimalPos = decimalPos * DECIMAL_DELT;
            }
            //=================================
            else if(s == null && unitValue != null && decimalPos >= 1){
                unit[i - 1] = unitValue;
            }
        }

        double value = 0.0;
        try {
            for(int i = 0; i < temp.length; i++){
                char c = temp[i];
                if(c != EMPTY_CHAR) {
                    int pv = Character.getNumericValue(c);

                    double unitV = 1;
                    if(unit[i] > 0) {
                        unitV = unit[i];
                    }
                    value += pv * unitV;
                }
                else {
                    continue;
                }
            }
        }
        catch (Exception e) {
            return;
        }

        BigDecimal bigDecimal = new BigDecimal(value);
        attr.setNum(bigDecimal.setScale(8, BigDecimal.ROUND_HALF_DOWN).doubleValue());
    }

    public static void main(String[] args) {
        Attr attr = new Attr();
//        attr.setNumstr("一百八十七");
//        trans(attr);
//        System.out.println(attr.getNum());
//
//        attr.setNumstr("198");
//        trans(attr);
//        System.out.println(attr.getNum());
//
//        attr.setNumstr("1.98");
//        trans(attr);
//        System.out.println(attr.getNum());
//
//        attr.setNumstr("一点零八零七");
//        trans(attr);
//        System.out.println(attr.getNum());
//
//        attr.setNumstr("一米七五");
//        trans(attr);
//        System.out.println(attr.getNum());

        attr.setNumstr("一八五230");
        trans(attr);
        System.out.println(attr.getNum());
    }
}
