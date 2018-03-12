package com.wekj.ner;

import com.wekj.ner.rule.Rule;
import com.wekj.ner.struct.Attr;
import com.wekj.ner.tool.AttrTool;
import com.wekj.ner.tool.WordToNum;
import com.wekj.ner.utils.NumUtils;
import com.wekj.ner.utils.StreamUtils;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.ansj.util.MyStaticValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class NumNer {
    public Attr getAttr(Term word, Term nextWord) {
        Attr attr = null;
        if(nextWord != null &&
                (nextWord.getNatureStr().equalsIgnoreCase("q") ||
                        nextWord.getNatureStr().equalsIgnoreCase("en")
                ) &&
                word.getNatureStr().equalsIgnoreCase("m")){
            //数词+量词及疑似量词
            attr = new Attr();
            attr.setNumstr(word.getName());
            attr.setUnit(nextWord.getName());
        }
        else if(word.getNatureStr().equalsIgnoreCase("m")){
            //数词
            attr = new Attr();
            attr.setNumstr(word.getName());
        }
        else if(word.getNatureStr().equalsIgnoreCase("mq")){
            //数量词
            attr = new Attr();
            attr.setNumstr(word.getName());
        }
        else {
            Set<Map.Entry<String, Set<String>>> entries = AttrTool.KeyWordsFamilies.entrySet();
            for (Map.Entry<String, Set<String>> entry : entries) {
                String key = entry.getKey();
                Set<String> value = entry.getValue();

                if(value.contains(word.getName())) {
                    attr = new Attr();
                    attr.setAttribute(key);
                    break;
                }
            }

        }

        return attr;
    }

    public List<Attr> anaQuestion(String question) {
        Result parse = DicAnalysis.parse(question);
        List<Term> terms = parse.getTerms();
        List<Term> newTerms = new ArrayList<>(terms.size());

        Term lastTerm = null;
        for (Term term : terms) {
            if(lastTerm != null && Rule.isCombineNature(lastTerm) && Rule.isCombineNature(term) &&
                    lastTerm.getNatureStr().equalsIgnoreCase(term.getNatureStr()))
            {
                lastTerm.setName(lastTerm.getName() + term.getName());
            }
            else {
                lastTerm = new Term(term.getName(), term.getOffe(), term.getNatureStr(), 0);
                newTerms.add(lastTerm);
            }
        }

        System.out.println(terms);
        System.out.println(newTerms);

        List<Attr> attrs = new ArrayList<>(newTerms.size());
        for (int i = 0; i < newTerms.size(); i++) {
            Term curTerm = newTerms.get(i);
            Term nextTerm = null;
            int nextIndex = i + 1;
            if(nextIndex < newTerms.size()){
                nextTerm = newTerms.get(i + 1);
            }

            Attr attr = getAttr(curTerm, nextTerm);
            if(attr != null) {
                attrs.add(0, attr);
            }
        }

        Attr curAttr, nextAttr;
        List<Attr> result = new LinkedList<>();
        for (int i = 0; i < attrs.size(); i++) {
            curAttr = attrs.get(i);
            if(curAttr == null) {
                continue;
            }

            AttrTool.guessAttrName(curAttr);

            nextAttr = null;
            int nextIndex = i + 1;
            if(nextIndex < attrs.size()) {
                nextAttr = attrs.get(nextIndex);
            }

            if(nextAttr != null && curAttr.tryMatch(nextAttr)){
                i ++;
            }

            result.add(curAttr);
        }

        Iterator<Attr> iterator = result.iterator();
        while (iterator.hasNext()) {
            //数字文字转数字
            Attr attr = iterator.next();
            WordToNum.trans(attr);
            if(NumUtils.isZero5(attr.getNum()) || Double.isNaN(attr.getNum())) {
                iterator.remove();
                continue;
            }
            //根据单位猜测属性
            AttrTool.guessAttrName(attr);
        }

        return result;
    }

    public void testFile(String path) throws IOException {
        BufferedReader reader = StreamUtils.getReader(path);
        StringBuilder temp = new StringBuilder();

        while(reader.ready()) {
            String s = reader.readLine();
            List<Attr> attrs = anaQuestion(s);
            temp.append(s).append("\t").append(attrs).append("\n");
        }

        StreamUtils.close(reader);

        StreamUtils.writeFile(path + "test", temp.toString());
    }

    public static void main(String[] args) {
        MyStaticValue.isQuantifierRecognition = false;
        NumNer ner = new NumNer();
        String question;/*
        question = "我188cm130kg可以穿吧";
        System.out.println(question + "\t" + ner.anaQuestion(question));
        question = "我188厘米130公斤可以穿吧";
        System.out.println(question + "\t" + ner.anaQuestion(question));
        question = "我1点88米1两30公斤可以穿吧";
        System.out.println(question + "\t" + ner.anaQuestion(question));
        question = "我身高188,然后1两30公斤可以穿吧";
        System.out.println(question + "\t" + ner.anaQuestion(question));
        question = "我身高1.88,然后1两30公斤可以穿吧";
        System.out.println(question + "\t" + ner.anaQuestion(question));
        question = "我身高1点88,然后1两30公斤可以穿吧";
        System.out.println(question + "\t" + ner.anaQuestion(question));
        question = "身高176体重210穿哪个码";
        System.out.println(question + "\t" + ner.anaQuestion(question));
        question = "一百七十五斤一米七五";
        System.out.println(question + "\t" + ner.anaQuestion(question));
        question = "嗯一米七，一百二十斤左右穿多大？";
        System.out.println(question + "\t" + ner.anaQuestion(question));
        question = "一米六八一百斤左右。";
        System.out.println(question + "\t" + ner.anaQuestion(question));
        question = "额一八五230斤，三尺二腰能穿么";
        System.out.println(question + "\t" + ner.anaQuestion(question));
        question = "1.73米，195斤";
        System.out.println(question + "\t" + ner.anaQuestion(question));
        question = "我170，117斤，修身点，穿多大穿";
        System.out.println(question + "\t" + ner.anaQuestion(question));
        question = "1.6米";
        System.out.println(question + "\t" + ner.anaQuestion(question));
        question = "身高170体重70多斤";
        System.out.println(question + "\t" + ner.anaQuestion(question));
        question = "身高170体重70㎏";
        System.out.println(question + "\t" + ner.anaQuestion(question));
        question = "172，190斤，腰围2尺8、9";
        System.out.println(question + "\t" + ner.anaQuestion(question));*/
        question = "173高110斤穿什么号码";
        System.out.println(question + "\t" + ner.anaQuestion(question));

        question = "180多斤";
        System.out.println(question + "\t" + ner.anaQuestion(question));

        question = "172，190斤，腰围2尺8";
        System.out.println(question + "\t" + ner.anaQuestion(question));

        question = "190斤，1米72";
        System.out.println(question + "\t" + ner.anaQuestion(question));
/*
        try {
            ner.testFile("D:\\xiaowei\\20180309\\新建文本文档.txt");
            ner.testFile("D:\\xiaowei\\20180312\\数字识别.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
