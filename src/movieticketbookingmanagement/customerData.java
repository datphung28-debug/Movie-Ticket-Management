/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package movieticketbookingmanagement;

import java.sql.Date;
import java.sql.Time;

/**
 *
 * @author DATDEV
 */
public class customerData {
    
    private String title;
    private Integer id;
    private String type;
    private String movieTitle;
    private int quantity;
    private double total;
    private Date date;
    private Time time;

    public customerData(String movieTitle, String title, Integer id, String type, int quantity, double total, Date date, Time time) {
        
        this.title = title;
        this.id = id;
        this.type = type;
        this.movieTitle = movieTitle;
        this.quantity = quantity;
        this.total = total;
        this.date = date;
        this.time = time;
    }
    
    public String getMovieTitle() {
        return movieTitle;
    }
    
    public String getTitle() {
        return title;
    }

    public Integer getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotal() {
        return total;
    }

    public Date getDate() {
        return date;
    }

    public Time getTime() {
        return time;
    }

    
    
    
    
}
