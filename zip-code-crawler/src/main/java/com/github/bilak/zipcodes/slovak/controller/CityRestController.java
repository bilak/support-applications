package com.github.bilak.zipcodes.slovak.controller;


import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.bilak.zipcodes.slovak.persistence.model.Obec;
import com.github.bilak.zipcodes.slovak.persistence.repository.CityRepository;
import com.querydsl.core.types.Predicate;

/**
 * Rest controller for operations on cities.
 *
 * @author Lukáš Vasek
 */
@RestController
@RequestMapping("/slovak/cities")
public class CityRestController {


    private final CityRepository repository;

    public CityRestController(final CityRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<Iterable<Obec>> query(@QuerydslPredicate(root = Obec.class) final Predicate predicate) {
        return ResponseEntity.ok(repository.findAll(predicate));
    }
}
