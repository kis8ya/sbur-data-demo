package com.github.kis8ya.datademo.repositories;

import com.github.kis8ya.datademo.model.Aircraft;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface AircraftRepository extends CrudRepository<Aircraft, Long> {
}
