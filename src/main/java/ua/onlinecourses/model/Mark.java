//mark.java
package ua.onlinecourses.model;

public enum Mark {
    EXCELLENT(5),
    GOOD(4), 
    PASSED(3),
    SATISFACTORY(2),
    LOW(1),
    NOT_PASSED(0);
    
    private final int value;
    
    Mark(int value) {
       this.value=value;
    }
    
    public int getValue() {
        return value;
    }
}
