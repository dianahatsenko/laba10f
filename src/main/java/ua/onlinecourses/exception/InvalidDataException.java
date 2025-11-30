/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ua.onlinecourses.exception;

/**
 *
 * @author dinag
 */
public class InvalidDataException extends RuntimeException {
    public InvalidDataException(){
        super();
    }

    public InvalidDataException(String message){
        super(message);
    }

    public InvalidDataException(Throwable e){
        super(e);
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    } 
}
