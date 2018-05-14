package utils;

import java.util.regex.Pattern;

public class NameUtils {
    public static String toCamelCase(String underScoreStr){
        StringBuilder sb= new StringBuilder();
        boolean is =false;
        for (int i = 0; i < underScoreStr.length(); i++) {
            if(underScoreStr.charAt(i)=='_') {
                is = true;
                continue;
            }
            if(is) {
                sb.append(underScoreStr.charAt(i)-32);
                is=false;
            }else {
                sb.append(underScoreStr.charAt(i));
            }
        }
        return sb.toString();
    }
    public static String toUnderScore(String camelCaseStr){
        StringBuilder sb= new StringBuilder();
        boolean allUpper = true;
        for (int i = 0; i < camelCaseStr.length(); i++) {
            if(camelCaseStr.charAt(i)<'a') {
                sb.append('_');
            }else {
                allUpper=false;
            }
            sb.append(Character.toLowerCase(camelCaseStr.charAt(i)));
        }
        if(allUpper) return camelCaseStr.toLowerCase();
        if(sb.charAt(0)=='_') sb.deleteCharAt(0);
        return sb.toString();
    }
}
