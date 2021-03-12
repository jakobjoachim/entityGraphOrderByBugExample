package com.jakobjoachim.entitygraphorderbybug;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnotherEntityRepository extends CrudRepository<AnotherEntity, Integer> {
}
