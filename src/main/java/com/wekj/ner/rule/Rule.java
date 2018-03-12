package com.wekj.ner.rule;

import org.ansj.domain.Nature;
import org.ansj.domain.Term;

public class Rule {
    private static final Nature NATURE_M = new Nature("m");

    public static boolean isCombineNature(Term token) {
        if(token.getNatureStr().equalsIgnoreCase("m")){
            return true;
        }

        if(token.getName().equalsIgnoreCase("点")){
            token.setNature(NATURE_M);
            return true;
        }

        if(token.getName().equalsIgnoreCase("两米") ||
                token.getName().equalsIgnoreCase("米") ||
                token.getName().equalsIgnoreCase("一米")){
            token.setNature(NATURE_M);
            return true;
        }

        if(token.getName().equalsIgnoreCase("一尺") ||
                token.getName().equalsIgnoreCase("二尺") ||
                token.getName().equalsIgnoreCase("两尺") ||
                token.getName().equalsIgnoreCase("三尺") ||
                token.getName().equalsIgnoreCase("尺")
                ){
            token.setNature(NATURE_M);
            return true;
        }

        return false;
    }
}
