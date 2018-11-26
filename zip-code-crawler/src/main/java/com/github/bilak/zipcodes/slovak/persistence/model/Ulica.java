package com.github.bilak.zipcodes.slovak.persistence.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing street.
 *
 * @author Lukáš Vasek
 */
@Table
@Entity
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Ulica {

    @Id
    private String id;
    private String dulica;
    private String ulica;
    private String psc;
    private String dposta;
    private String posta;
    private String poznamka;
    private String obce;

}
