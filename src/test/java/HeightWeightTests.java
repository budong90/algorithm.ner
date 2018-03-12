import com.wekj.ner.HeightWeightBiz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 身高体重提取测试
 *
 * @author sunli
 * @create 2018-03-12 10:39
 */

public class HeightWeightTests {
    public static void main(String[] args) {
        HeightWeightBiz service;
        String text;
        Map<String, Integer> metadata;
        while (true) {

            System.out.print("请输入问题：");
            text = getInputString();
            if ("-1".equals(text)) {
                break;
            }
            metadata = new HashMap<>();
            service = new HeightWeightBiz(text, metadata);
        }
    }

    private static String getInputString() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
