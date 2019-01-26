package com.peppermintcommunications.intimasiaregistration;

import android.os.Parcel;
import android.os.Parcelable;

public class Exhibitor implements Parcelable {

    private int id;
    private String name, brands, city, stall, product, company_addr, hall, notes;

    public Exhibitor(int id, String name, String brands, String city, String stall, String product,
                     String company_addr, String hall, String notes) {
        this.id = id;
        this.name = name;
        this.brands = brands;
        this.city = city;
        this.stall = stall;
        this.product = product;
        this.company_addr = company_addr;
        this.hall = hall;
        this.notes = notes;
    }

    protected Exhibitor(Parcel in) {
        id = in.readInt();
        name = in.readString();
        brands = in.readString();
        city = in.readString();
        stall = in.readString();
        product = in.readString();
        company_addr = in.readString();
        hall = in.readString();
        notes = in.readString();
    }

    public static final Creator<Exhibitor> CREATOR = new Creator<Exhibitor>() {
        @Override
        public Exhibitor createFromParcel(Parcel in) {
            return new Exhibitor(in);
        }

        @Override
        public Exhibitor[] newArray(int size) {
            return new Exhibitor[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrands() {
        return brands;
    }

    public String getCity() {
        return city;
    }

    public String getStall() {
        return stall;
    }

    public String getProduct() {
        return product;
    }

    public String getCompany_addr() {
        return company_addr;
    }

    public String getHall() {
        return hall;
    }

    public String getNotes() {
        return notes;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBrands(String brands) {
        this.brands = brands;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStall(String stall) {
        this.stall = stall;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setCompany_addr(String company_addr) {
        this.company_addr = company_addr;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(brands);
        parcel.writeString(city);
        parcel.writeString(stall);
        parcel.writeString(product);
        parcel.writeString(company_addr);
        parcel.writeString(hall);
        parcel.writeString(notes);
    }
}
