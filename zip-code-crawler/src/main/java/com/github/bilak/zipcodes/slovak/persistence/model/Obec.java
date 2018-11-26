package com.github.bilak.zipcodes.slovak.persistence.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing city.
 *
 * @author Lukáš Vasek
 */
@Entity
@Table
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Obec {

    @Id
    private String id;
    private String dobec;
    private String obec;
    private String okres;
    private String psc;
    private String dposta;
    private String posta;
    private String kodOkresu;
    private String kraj;
}
