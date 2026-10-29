
package com.example.smartpantry;

public class Ingredient {
    public long id;
    public String name;
    public double quantity;
    public String unit;
    public String expiryDate;

    public Ingredient(long id, String name, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit == null ? "" : unit;
        this.expiryDate = expiryDate == null ? "" : expiryDate;
    }
}
