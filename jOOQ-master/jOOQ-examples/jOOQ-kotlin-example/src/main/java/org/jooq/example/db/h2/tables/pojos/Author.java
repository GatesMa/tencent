/*
 * This file is generated by jOOQ.
 */
package org.jooq.example.db.h2.tables.pojos;


import java.io.Serializable;
import java.time.LocalDate;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Author implements Serializable {

    private static final long serialVersionUID = -1160184137;

    private Integer   id;
    private String    firstName;
    private String    lastName;
    private LocalDate dateOfBirth;
    private Integer   yearOfBirth;
    private String    address;

    public Author() {}

    public Author(Author value) {
        this.id = value.id;
        this.firstName = value.firstName;
        this.lastName = value.lastName;
        this.dateOfBirth = value.dateOfBirth;
        this.yearOfBirth = value.yearOfBirth;
        this.address = value.address;
    }

    public Author(
        Integer   id,
        String    firstName,
        String    lastName,
        LocalDate dateOfBirth,
        Integer   yearOfBirth,
        String    address
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.yearOfBirth = yearOfBirth;
        this.address = address;
    }

    /**
     * Getter for <code>PUBLIC.AUTHOR.ID</code>.
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Setter for <code>PUBLIC.AUTHOR.ID</code>.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Getter for <code>PUBLIC.AUTHOR.FIRST_NAME</code>.
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Setter for <code>PUBLIC.AUTHOR.FIRST_NAME</code>.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Getter for <code>PUBLIC.AUTHOR.LAST_NAME</code>.
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Setter for <code>PUBLIC.AUTHOR.LAST_NAME</code>.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Getter for <code>PUBLIC.AUTHOR.DATE_OF_BIRTH</code>.
     */
    public LocalDate getDateOfBirth() {
        return this.dateOfBirth;
    }

    /**
     * Setter for <code>PUBLIC.AUTHOR.DATE_OF_BIRTH</code>.
     */
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Getter for <code>PUBLIC.AUTHOR.YEAR_OF_BIRTH</code>.
     */
    public Integer getYearOfBirth() {
        return this.yearOfBirth;
    }

    /**
     * Setter for <code>PUBLIC.AUTHOR.YEAR_OF_BIRTH</code>.
     */
    public void setYearOfBirth(Integer yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    /**
     * Getter for <code>PUBLIC.AUTHOR.ADDRESS</code>.
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Setter for <code>PUBLIC.AUTHOR.ADDRESS</code>.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Author (");

        sb.append(id);
        sb.append(", ").append(firstName);
        sb.append(", ").append(lastName);
        sb.append(", ").append(dateOfBirth);
        sb.append(", ").append(yearOfBirth);
        sb.append(", ").append(address);

        sb.append(")");
        return sb.toString();
    }
}