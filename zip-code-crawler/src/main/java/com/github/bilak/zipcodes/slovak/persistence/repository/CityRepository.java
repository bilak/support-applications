package com.github.bilak.zipcodes.slovak.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;

import com.github.bilak.zipcodes.slovak.persistence.model.Obec;
import com.github.bilak.zipcodes.slovak.persistence.model.QObec;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;

/**
 * Repository operations for {@link Obec city}.
 *
 * @author Lukáš Vasek
 */
public interface CityRepository extends JpaRepository<Obec, String>, QuerydslPredicateExecutor<Obec>, QuerydslBinderCustomizer<QObec> {

    @Override
    default void customize(final QuerydslBindings bindings, final QObec root) {
        bindings.bind(String.class).first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
        bindings.excluding(root.id);
    }
}
