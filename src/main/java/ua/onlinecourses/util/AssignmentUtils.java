
package ua.onlinecourses.util;

import java.time.LocalDate;

public class AssignmentUtils {
    public static boolean isValidMaxPoints(int maxPoints){
        return ValidationHelper.isNumberBetween (maxPoints, 1, 100);
    }
    
    public static boolean isValidDueDate(LocalDate dueDate){
        LocalDate minDate=LocalDate.now().minusYears(1);
        LocalDate maxDate=LocalDate.now().plusYears(1);
        return ValidationHelper.isDateBetween (dueDate, minDate, maxDate);
        
    }
}
