package irk.staryo.utils;

import java.util.ArrayList;
import java.util.List;

public class StringBitOperation {
    public static int stringBitOrder(String bits){
        int result = 0;
        for (Character c : bits.toCharArray()){
            if (c == '1'){
                result += 1;
            }
        }
        return result;
    }

    public static String findCommonBits(String s1, String s2){
        assert s1.length() == s2.length() : "Both bit string needs to be the same length";
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < s1.length(); i++){
            if (s1.charAt(i) == '1' && s2.charAt(i) == '1'){
                sb.append('1');
            } else {
                sb.append('0');
            }
        }

        return sb.toString();
    }

    public static List<String> getLowerOrderSubsets(String s){
        List<String> lowerOrderSubsets = new ArrayList<>();
        char[] chars = s.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '1') {
                char[] tempChars = s.toCharArray();
                tempChars[i] = '0';
                lowerOrderSubsets.add(new String(tempChars));
            }
        }
        return lowerOrderSubsets;
    }

    public static List<Integer> toIndexList(String s){
        List<Integer> indexlist = new ArrayList<>();
        char[] chars = s.toCharArray();

        for (int i = 0; i < chars.length; i++){
            if (chars[i] == '1'){
                indexlist.add(i);
            }
        }

        return indexlist;
    }

    public static String indexToStringbit(int index, int length){
        assert index < length : "Index needs to be smaller than length in Stringbit indexToStringbit";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++){
            if (i == index){
                sb.append('1');
            } else {
                sb.append('0');
            }
        }

        return sb.toString();
    }

    public static String complement(String bountiful, String lesser){
        assert bountiful.length() == lesser.length() : "Both bit string needs to be the same length";
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < bountiful.length(); i++){
            if (bountiful.charAt(i) == '1' && lesser.charAt(i) == '0'){
                sb.append('1');
            } else {
                sb.append('0');
            }
        }

        return sb.toString();
    }

    public static String emptyStringBitN(Integer n){
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < n; i++){
            sb.append('0');
        }

        return sb.toString();
    }
}
