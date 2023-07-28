import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class 正则 {

    public static void main(String[] args) {
        String sql = "select * from ${张} ${b} ${c}";
        List<String> list = getKey(sql);
        System.out.println(list);

        //String s = "select * from sys_user limit ${sum:5}".replaceAll("\\$\\{" + "sum:5" + "}", "5");
        String s = "select * from sys_user limit ${sum:5}".replaceAll(String.format("\\$\\{%s}", "sum:5"), "5");
        System.out.println(s);

    }

    public static List<String> getKey(String sql) {
        Pattern regex = Pattern.compile("\\$\\{([^}]*)\\}");
        Matcher matcher = regex.matcher(sql);
        List<String> list = new ArrayList<>();
        while(matcher.find()) {
            list.add(matcher.group(1));
        }
        return list;
    }

    @Test
    public void test() {
        String aa = "性别[男=1][女=2]:1 or 1=1";
        List<String> list = getIf(aa);
        list.forEach(System.out::println);
    }

    public static List<String> getIf(String sql) {
        Pattern regex = Pattern.compile("\\[.*?]");
        Matcher matcher = regex.matcher(sql);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;
    }


    @Test
    public void test2() {
        boolean b = isInteger("a");
        System.out.println(b);
    }
    // 判断是否是数字
    public boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
}
