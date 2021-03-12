package com.jakobjoachim.entitygraphorderbybug;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SomeEntityRepository extends CrudRepository<SomeEntity, Integer> {

	// doesn't work
	@EntityGraph(attributePaths = "someOrderedValues", type = EntityGraph.EntityGraphType.LOAD)
	Stream<SomeEntity> findAllByIdIn(List<Integer> ids);

	// works
	@EntityGraph(attributePaths = "someOrderedValues", type = EntityGraph.EntityGraphType.LOAD)
	List<SomeEntity> findAllBySomeOrderedValuesIdIn(List<Integer> ids);

	// works
	@EntityGraph(attributePaths = "someOrderedValues", type = EntityGraph.EntityGraphType.LOAD)
	Stream<SomeEntity> findAllByIdInOrderById(List<Integer> ids);
}
