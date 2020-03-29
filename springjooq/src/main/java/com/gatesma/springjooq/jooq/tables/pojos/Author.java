/*
 * This file is generated by jOOQ.
 */
package com.gatesma.springjooq.jooq.tables.pojos;


import java.io.Serializable;
import java.time.LocalDate;

import javax.annotation.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Author implements Serializable {

    private static final long serialVersionUID = 1369996516;

    private Integer   id;
    private String    firstName;
    private String    lastName;
    private LocalDate dateOfBirth;
    private Integer   yearOfBirth;
    private Integer   distinguished;

    public Author() {}

    public Author(Author value) {
        this.id = value.id;
        this.firstName = value.firstName;
        this.lastName = value.lastName;
        this.dateOfBirth = value.dateOfBirth;
        this.yearOfBirth = value.yearOfBirth;
        this.distinguished = value.distinguished;
    }

    public Author(
        Integer   id,
        String    firstName,
        String    lastName,
        LocalDate dateOfBirth,
        Integer   yearOfBirth,
        Integer   distinguished
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.yearOfBirth = yearOfBirth;
        this.distinguished = distinguished;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getYearOfBirth() {
        return this.yearOfBirth;
    }

    public void setYearOfBirth(Integer yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public Integer getDistinguished() {
        return this.distinguished;
    }

    public void setDistinguished(Integer distinguished) {
        this.distinguished = distinguished;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Author (");

        sb.append(id);
        sb.append(", ").append(firstName);
        sb.append(", ").append(lastName);
        sb.append(", ").append(dateOfBirth);
        sb.append(", ").append(yearOfBirth);
        sb.append(", ").append(distinguished);

        sb.append(")");
        return sb.toString();
    }
}