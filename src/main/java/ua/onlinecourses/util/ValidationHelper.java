
package ua.onlinecourses.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.regex.Pattern;
class ValidationHelper {
    
    final static String DATE_FORMAT = "dd-MM-yyyy";
    
    private ValidationHelper(){
    }
    
    static boolean isStringMatchPattern(String text, String pattern){
        if (text == null || pattern == null){
            return false;
        }
        return Pattern.matches(pattern, text);
    }

    static boolean isNumberBetween(int number, int min, int max) {
        return number >= min && number <= max;
    }

    static boolean isStringLengthBetween(String text, int min, int max) {
        if (text == null) {
            return false;
        }
        int length = text.trim().length();
        return length >= min && length <= max;  
    }
    
    static boolean isDateValid(String date)
{
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
}
    
   static boolean isDateBetween(LocalDate date,LocalDate minDate,LocalDate maxDate) {
       return date.isAfter(minDate) && date.isBefore(maxDate);
   } 
    
    
}
