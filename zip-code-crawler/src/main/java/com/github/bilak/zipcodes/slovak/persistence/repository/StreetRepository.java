package com.github.bilak.zipcodes.slovak.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;

import com.github.bilak.zipcodes.slovak.persistence.model.QUlica;
import com.github.bilak.zipcodes.slovak.persistence.model.Ulica;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;

/**
 * Repository operations for {@link Ulica street}.
 *
 * @author Lukáš Vasek
 */
public interface StreetRepository extends JpaRepository<Ulica, String>, QuerydslPredicateExecutor<Ulica>, QuerydslBinderCustomizer<QUlica> {

    @Override
    default void customize(final QuerydslBindings bindings, final QUlica root) {
        bindings.bind(String.class).first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
        bindings.excluding(root.id);
    }
}
