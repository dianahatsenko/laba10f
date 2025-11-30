
package ua.onlinecourses.util;

public class ModuleUtils {
    public ModuleUtils(){
}
    public static boolean isValidTitle(String title){
        return ValidationHelper.isStringLengthBetween(title, 1, 30);
    }
    public static boolean isValidContent(String content){
        return ValidationHelper.isStringLengthBetween(content, 1, 2000);
    }
    
    
}
