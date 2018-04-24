package utils;

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
        for (int i = 0; i < camelCaseStr.length(); i++) {
            if(camelCaseStr.charAt(i)>'z') {
                sb.append('_');
            }
            sb.append(camelCaseStr.charAt(i));
        }
        return sb.toString();
    }
}
