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
        decimalMap.add(".");
        decimalMap2.add("米");
        decimalMap2.add("斤");
        decimalMap2.add("尺");

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

        //对各个位数的进制进行处理
        resizeUnit(unit, temp);

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
        //识别数字
        BigDecimal bigDecimal = new BigDecimal(value);
        attr.setNum(bigDecimal.setScale(8, BigDecimal.ROUND_HALF_DOWN).doubleValue());
    }

    /**
     * 通用进制赋值
     * @param unit 进制
     * @param temp 各位数值
     */
    private static void resizeUnit(double[] unit, char[] temp){
        //随便一个值，反正不是10的倍数或者1或者0.1的N次方即可
        //选择99是因为数字吉利
        double base = 99;
        int pos = -1;

        for(int j = 0; j < unit.length - 1; j++) {
            double v = unit[j];
            if(v > 0){
                pos = j;
                base = v;
                break;
            }
        }

        if(base == 99){
            //此时读不到任何信息,那么倒数第二数反向推导进制，注意位数信息数组比数值信息数组长度多一
            pos = 2;

            for(int j = 0; j < unit.length - 1; j++) {
                char num = temp[j];
                if (num == EMPTY_CHAR) {
                    //这种情况表示数字文字可能不是纯数字，后面存在合并的单位或者“左右”，“多”之类的词
                    pos = unit.length - j + 1;
                    break;
                }
            }

            base = 1;
            for(int j = unit.length - pos; j >= 0; j--) {
                unit[j] = base;
                base = base * 10;
            }
        }
    }

    public static void main(String[] args) {
        Attr attr = new Attr();
        attr.setNumstr("一百八十七");
        trans(attr);
        System.out.println(attr.getNumstr() + "\t" + attr.getNum());

        attr.setNumstr("198");
        trans(attr);
        System.out.println(attr.getNumstr() + "\t" + attr.getNum());

        attr.setNumstr("1.98");
        trans(attr);
        System.out.println(attr.getNumstr() + "\t" + attr.getNum());

        attr.setNumstr("一点零八零七");
        trans(attr);
        System.out.println(attr.getNumstr() + "\t" + attr.getNum());

        attr.setNumstr("一米七五");
        trans(attr);
        System.out.println(attr.getNumstr() + "\t" + attr.getNum());

        attr.setNumstr("一八五230");
        trans(attr);
        System.out.println(attr.getNumstr() + "\t" + attr.getNum());

        attr.setNumstr("五九千二百");
        trans(attr);
        System.out.println(attr.getNumstr() + "\t" + attr.getNum());

        attr.setNumstr("70多斤");
        trans(attr);
        System.out.println(attr.getNumstr() + "\t" + attr.getNum());

        attr.setNumstr("700里面左右");
        trans(attr);
        System.out.println(attr.getNumstr() + "\t" + attr.getNum());
    }
}
