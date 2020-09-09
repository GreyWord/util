package auto.util.random;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @Date: 2018/11/9 11:14
 * @Description:
 */
public class DoubleRate {

    public static void main(String[] args) {
        System.out.println(create());
    }
    public static ArrayList<Integer> create(){
        ArrayList<Integer> integers = new ArrayList<>();
        int current;
        for (int i=1;i<100;i++){
            if(integers.size()>5)
                break;
            current=RandomUtil.randomInt(32)+1;
            if(integers.contains(current))
                continue;
            integers.add(current);
        }
        integers.sort(Integer::compareTo);
        integers.add(RandomUtil.randomInt(15)+1);
        return integers;
    }
}
