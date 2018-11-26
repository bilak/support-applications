package com.github.bilak.zipcodes.slovak.controller;

import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.bilak.zipcodes.slovak.persistence.model.Ulica;
import com.github.bilak.zipcodes.slovak.persistence.repository.StreetRepository;
import com.querydsl.core.types.Predicate;

/**
 * Rest controller for operations on streets.
 *
 * @author Lukáš Vasek
 */
@RestController
@RequestMapping("/slovak/streets")
public class StreetRestController {

    private final StreetRepository repository;

    public StreetRestController(final StreetRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public ResponseEntity<Iterable<Ulica>> query(@QuerydslPredicate(root = Ulica.class) final Predicate predicate) {
        return ResponseEntity.ok(repository.findAll(predicate));
    }
}
