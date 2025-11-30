
package ua.onlinecourses.util;
import ua.onlinecourses.model.Course;
import java.time.LocalDate;
import java.time.Month;
public class CourseUtils {
    private CourseUtils(){
    }
    
    public static boolean isValidstartDate(LocalDate startDate){
        LocalDate minDate=LocalDate.now().minusYears(1);
        LocalDate maxDate=LocalDate.now().plusYears(1);
        return ValidationHelper.isDateBetween (startDate, minDate, maxDate);
        
    }
     public static boolean isValidCredit(int credit){
        return ValidationHelper.isNumberBetween (credit, 1, 5);
    }
     public static boolean isValidDescription(String descriptions){
        return ValidationHelper.isStringLengthBetween(descriptions, 1, 500);
    }
     public static boolean isValidTitle(String title){
        return ValidationHelper.isStringLengthBetween(title, 1, 100);
    }
}
