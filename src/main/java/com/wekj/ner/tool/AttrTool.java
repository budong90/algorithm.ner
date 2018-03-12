package com.wekj.ner.tool;

import com.wekj.ner.struct.Attr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AttrTool {
    public static final Map<String, Set<String>> KeyWordsFamilies = new HashMap<String, Set<String>>();
    public static final Map<String, Set<String>> UnitFamilies = new HashMap<String, Set<String>>();

    static {

        Set<String> set = new HashSet<String>();
        set.add("身高");
        set.add("高");
        set.add("身");

        KeyWordsFamilies.put("身高", set);

        set = new HashSet<String>();
        set.add("cm");
        set.add("米");
        set.add("厘米");
        set.add("公分");

        UnitFamilies.put("身高", set);

        set = new HashSet<String>();
        set.add("体重");
        set.add("重");
        set.add("体");

        KeyWordsFamilies.put("体重", set);

        set = new HashSet<String>();
        set.add("千克");
        set.add("斤");
        set.add("公斤");
        set.add("kg");
        set.add("㎏");

        UnitFamilies.put("体重", set);

        set = new HashSet<String>();
        set.add("腰围");
        set.add("腰");

        KeyWordsFamilies.put("腰围", set);

        set = new HashSet<String>();
        set.add("尺");
        set.add("寸");

        UnitFamilies.put("腰围", set);
    }

    public static void guessAttrName(Attr attr) {
        if (attr.getAttribute() != null || attr.getUnit() == null) {
            return;
        }
        String unitName = attr.getUnit().toLowerCase();
        Set<Map.Entry<String, Set<String>>> entries = UnitFamilies.entrySet();
        for (Map.Entry<String, Set<String>> entry : entries) {
            String key = entry.getKey();
            Set<String> value = entry.getValue();
            if(value.contains(unitName)) {
                attr.setAttribute(key);
                break;
            }
        }
    }
}
