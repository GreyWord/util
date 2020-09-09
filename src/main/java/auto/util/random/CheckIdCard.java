package auto.util.random;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Created by ZhangXu on 2018/2/9.
 */
public class CheckIdCard {
    private String cardNumber; // 完整的身份证号码
    private final static int NEW_CARD_NUMBER_LENGTH = 18;
    private final static char[] VERIFY_CODE = { '1', '0', 'X', '9', '8', '7',
            '6', '5', '4', '3', '2' }; // 18位身份证中最后一位校验码
    private final static int[] VERIFY_CODE_WEIGHT = { 7, 9, 10, 5, 8, 4, 2, 1,
            6, 3, 7, 9, 10, 5, 8, 4, 2 };// 18位身份证中，各个数字的生成校验码时的权值

    public static void main(String[] args) throws IOException {
        String start = "13102519931222%03d";
        String temp,id,e;
        ArrayList<String> list = new ArrayList<>(10);
        for(int i = 800,j=0;i<1000 && j<100;i++,i++,j++){
            temp=String.format(start,i);
            e=""+calculateVerifyCode(temp);
            id=temp+e;
            //final String s = Net_tools.post("https://way.jd.com/freedt/api_rest_police_identity?appkey=88e81fa5d7a18767fe39bb104fa8efbb"
            //        , Arrays.asList("name=%E5%BC%A0%E6%97%AD&idCard=",id), Net_tools.SendType.form);
            //if(!e.equals("X")){
//                System.out.print("\"");
//                System.out.println(id);
//                System.out.print("\",");
                list.add(id);
            //}
            //System.out.println(calculateVerifyCode("13102519931010000"));
        }
        System.out.print(list.toString().replace(", ","\",\"").replace("[","[\"").replace("]","\"]"));
    }
    public boolean validate() {
            boolean result = true;
            result = result && (null != cardNumber); // 身份证号不能为空
            result = result && NEW_CARD_NUMBER_LENGTH == cardNumber.length(); // 身份证号长度是18(新证)
            // 身份证号的前17位必须是阿拉伯数字
            for (int i = 0; result && i < NEW_CARD_NUMBER_LENGTH - 1; i++) {
                char ch = cardNumber.charAt(i);
                result = result && ch >= '0' && ch <= '9';
            }
            // 身份证号的第18位校验正确
            result = result
                    && (calculateVerifyCode(cardNumber) == cardNumber
                    .charAt(NEW_CARD_NUMBER_LENGTH - 1));
            return true;
    }
    /**
     * 校验码（第十八位数）：
     *
     * 十七位数字本体码加权求和公式 S = Sum(Ai * Wi), i = 0...16 ，先对前17位数字的权求和；
     * Ai:表示第i位置上的身份证号码数字值 Wi:表示第i位置上的加权因子 Wi: 7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4
     * 2; 计算模 Y = mod(S, 11)< 通过模得到对应的校验码 Y: 0 1 2 3 4 5 6 7 8 9 10 校验码: 1 0 X 9
     * 8 7 6 5 4 3 2
     *
     * @param cardNumber
     * @return
     */
    private static char calculateVerifyCode(CharSequence cardNumber) {
        int sum = 0;
        for (int i = 0; i < NEW_CARD_NUMBER_LENGTH - 1; i++) {
            char ch = cardNumber.charAt(i);
            sum += ((ch - '0')) * VERIFY_CODE_WEIGHT[i];
        }
        return VERIFY_CODE[sum % 11];
    }
}
