
package ua.onlinecourses.util;

public class InstructorUtils {
    
    private InstructorUtils(){

    }
    public static String formatName(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            return "";
        }
        return firstName.trim() + " " + lastName.trim();
    }
    public static boolean isValidExpertise(int expertise){
        return ValidationHelper.isNumberBetween (expertise, 1, 60);
    }
    
    public static boolean isValidName(String name){
        return ValidationHelper.isStringLengthBetween(name, 1, 50);
    }
    
}
