package com.wekj.ner;

import com.wekj.ner.struct.Attr;
import org.ansj.util.MyStaticValue;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 身高体重提取工具类
 *
 * @author sunli
 * @create 2018-03-08 16:54
 */

public class HeightWeightBiz {

    private String text;
    private int minHeight = 130, maxHeight = 210, minWeight = 70, maxWeight = 300;
    private Map<String, Integer> metadata;
    private Pattern pattern;
    private Matcher matcher;

    public static void main(String[] args) {
        MyStaticValue.isQuantifierRecognition = false;
        NumNer ner = new NumNer();
        ner.anaQuestion(null);
    }

    public String getText() {
        return text;
    }

    public HeightWeightBiz(String text) {
        this.text = text;
    }

    public HeightWeightBiz(String text, Map<String, Integer> metadata) {
        this.text = text;
        this.metadata = metadata;
    }

    public HeightWeightBiz(String text, int minHeight, int maxHeight, int minWeight, int maxWeight, Map<String, Integer> metadata) {
        this.text = text;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.minWeight = minWeight;
        this.maxWeight = maxWeight;
        this.metadata = metadata;
    }

    private boolean isBlank(String str) {
        if (null == str || str.trim() == "") {
            return true;
        }
        return false;
    }

    /**
     * 判断提取到的数字是否是合法的身高，返回值单位cm
     *
     * @param height
     * @return
     */
    private double validHeight(String height, String unit) {
        if (isBlank(height)) return -1;
        try {
            double number = Double.valueOf(height);
            if ("M".equals(unit) || "米".equals(unit)
                    || (isBlank(unit) && number * 100 >= minHeight && number * 100 <= maxHeight)) {
                number *= 100;
            }
            if (number >= minHeight && number <= maxHeight) {
                return number;
            }
        } catch (Exception e) {
        }
        return -1;
    }

    /**
     * 判断提取到的数字是否是合法的体重，返回值单位斤
     *
     * @param weight
     * @return
     */
    private double validWeight(String weight, String unit) {
        try {
            if (isBlank(weight)) return -1;
            double number = Double.valueOf(weight);
            if ("KG".equals(unit)) {
                number *= 2;
            }
            if (number >= minWeight && number <= maxWeight) {
                return number;
            } else if (isBlank(unit) && number * 2 >= minWeight && number <= maxWeight) {
                return number * 2;
            }
        } catch (Exception e) {
        }
        return -1;
    }

    /**
     * 特殊问法解析
     */
    private void parseSpecial() {

    }

    public void parseData() {
        MyStaticValue.isQuantifierRecognition = false;
        NumNer ner = new NumNer();
        List<Attr> attrs = ner.anaQuestion(text);
        if (null == attrs || attrs.size() < 1) {
            return;
        }
        attrs.forEach(attr -> {
            attr.getAttribute();
            attr.getNumstr();
            attr.getUnit();
        });
    }

    /**
     * 校验正则是否匹配
     *
     * @param text
     * @param regex
     * @return
     */
    private boolean regexMatch(String text, String regex) {
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(text);
        return matcher.find();
    }

    /**
     * 从取到的数值中分析出身高和体重
     *
     * @param numbers
     * @return 数组0:身高;1:体重
     */
    public String[] analyze(String[] numbers) {
        String[] array = {"", ""}; // 身高、体重
        if (null == numbers || numbers.length < 1) {
            return array;
        }
        String number;
        int len = 0;
        double tmp = 0, tmp2 = 0;
        double minH = 139, maxH = 199; // 身高最小、最大
        double minW = 75, maxW = 250; // 体重最小、最大
        if (numbers.length == 1 || numbers[0].length() > 4) {
            number = numbers[0];
            len = number.length();
            if (len == 2) { // 体重
                tmp = Double.valueOf(number);
                array[1] = (tmp < 75 ? tmp * 2 : tmp) + "";
            } else if (len == 3) { // 身高或体重
                if (number.startsWith("1.")) {
                    array[0] = Double.valueOf(number) * 100 + "";
                } else {
                    tmp = Double.valueOf(number);
                    if (tmp > minH && tmp < maxH) {
                        array[0] = tmp + "";
                    } else if (tmp > minW && tmp < maxW) {
                        array[1] = tmp + "";
                    }
                }
            } else if (len == 4) { // 体重或身高
                array[0] = Double.valueOf(number) * 100 + "";
                array[1] = Double.valueOf(number) * 2 + "";
            } else if (len < 8) { // 身高+体重
                if (number.charAt(0) != '1') { // 体重+身高
                    tmp = Double.valueOf(number.substring(0, 2));
                    array[1] = (tmp < 75 ? tmp * 2 : tmp) + "";
                    if (number.charAt(2) == '.') {
                        tmp = Double.valueOf(number.substring(3));
                    } else {
                        tmp = Double.valueOf(number.substring(2));
                    }
                    array[0] = (tmp < 2 ? tmp * 100 : tmp) + "";
                } else {
                    int index = number.indexOf(".");
                    if (index < 0) {
                        tmp = Double.valueOf(number.substring(0, 3));
                        tmp2 = Double.valueOf(number.substring(3, (number.length() > 6 ? 6 : number.length())));
                        if (tmp2 < 75) {
                            tmp2 *= 2;
                            array[1] = tmp2 + "";
                            array[0] = tmp + "";
                        } else {
                            //  判断两个数合理性
                            if (tmp < tmp2) { // 178190
                                if (tmp2 > 185 && tmp > 170) {
                                    array[0] = tmp + "";
                                    array[1] = tmp2 + "";
                                } else {
                                    array[0] = tmp2 + "";
                                    array[1] = tmp + "";
                                }
                            } else {
                                array[0] = tmp + "";
                                array[1] = tmp2 + "";
                            }
                        }
                    } else if (index == 1) {
                        if (number.charAt(3) == '1') {
                            tmp = Double.valueOf(number.substring(0, 3)) * 100;
                            tmp2 = Double.valueOf(number.substring(3));
                        } else {
                            tmp = Double.valueOf(number.substring(0, (number.length() == 5 ? 3 : 4))) * 100;
                            tmp2 = Double.valueOf(number.substring(number.length() == 5 ? 3 : 4));
                        }
                        array[0] = tmp + "";
                        array[1] = (tmp2 < 75 ? tmp2 * 2 : tmp2) + "";
                    } else if (index < 4) {
                        tmp = Double.valueOf(number.substring(0, index));
                        tmp2 = Double.valueOf(number.substring(index + 1));
                        if (tmp2 < 75) {
                            tmp2 *= 2;
                            array[1] = tmp2 + "";
                            array[0] = tmp + "";
                        } else {
                            if (tmp < tmp2) {
                                array[0] = tmp2 + "";
                                array[1] = tmp + "";
                            } else {
                                array[0] = tmp + "";
                                array[1] = tmp2 + "";
                            }
                        }
                    } else { // 体重+身高
                        array[1] = Double.valueOf(number.substring(0, 3)) + "";
                        array[0] = Double.valueOf(number.substring(3)) * 100 + "";
                    }
                }
            }
        } else {
            tmp = Double.valueOf(numbers[0]);
            tmp2 = Double.valueOf(numbers[1]);
            if (tmp < 2) { // 身高
                tmp *= 100;
                array[0] = tmp + "";
                array[1] = (tmp2 < 75 ? tmp2 * 2 : tmp2) + "";
            } else if (tmp < 75) { // 体重
                tmp *= 2;
                array[1] = tmp + "";
                if (tmp2 < 2) {
                    tmp2 *= 100;
                }
                array[0] = tmp2 + "";
            } else {
                if (tmp2 < 2) {
                    tmp2 *= 100;
                    array[0] = tmp2 + "";
                    array[1] = tmp + "";
                } else if (tmp2 < 75) {
                    tmp2 *= 2;
                    array[1] = tmp2 + "";
                    array[0] = tmp + "";
                } else {
                    // 判断两个数合理性
                    if (tmp < tmp2) {
                        if (tmp2 > 185 && tmp > 170) {
                            array[0] = tmp + "";
                            array[1] = tmp2 + "";
                        } else {
                            array[0] = tmp2 + "";
                            array[1] = tmp + "";
                        }
                    } else {
                        array[0] = tmp + "";
                        array[1] = tmp2 + "";
                    }
                }
            }
        }
        if (!"".equals(array[0])) {
            tmp = Double.valueOf(array[0]);
            if (!(tmp > minH && tmp < maxH)) {
                array[0] = "";
            }
        }
        if (!"".equals(array[1])) {
            tmp = Double.valueOf(array[1]);
            if (!(tmp > minW && tmp < maxW)) {
                array[1] = "";
            }
        }
        return array;
    }
}
