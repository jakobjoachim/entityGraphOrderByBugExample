package com.jakobjoachim.entitygraphorderbybug;

import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class SomeEntityRepositoryTest {
	@Autowired
	SomeEntityRepository someEntityRepository;

	@Autowired
	AnotherEntityRepository anotherEntityRepository;

	@Autowired
	TransactionTemplate transactionTemplate;

	// doesn't work
	@Test
	void streamOrderByDoesntGroupEntitiesTogether() {
		AnotherEntity anotherEntityA1 = new AnotherEntity(1);
		AnotherEntity anotherEntityB1 = new AnotherEntity(2);
		AnotherEntity anotherEntityA2 = new AnotherEntity(3);
		AnotherEntity anotherEntityB2 = new AnotherEntity(4);

		transactionTemplate.executeWithoutResult(s -> {
			anotherEntityRepository.save(anotherEntityA1);
			anotherEntityRepository.save(anotherEntityB1);
			anotherEntityRepository.save(anotherEntityA2);
			anotherEntityRepository.save(anotherEntityB2);
		});

		SomeEntity someEntityA = new SomeEntity(5, Lists.list(anotherEntityA1, anotherEntityA2));
		SomeEntity someEntityB = new SomeEntity(6, Lists.list(anotherEntityB1, anotherEntityB2));

		transactionTemplate.executeWithoutResult(s -> {
			someEntityRepository.save(someEntityA);
			someEntityRepository.save(someEntityB);
		});


		List<SomeEntity> result = transactionTemplate.execute(s -> someEntityRepository.findAllByIdIn(Lists.list(5, 6))).collect(Collectors.toList());

		assertThat(result)
				.usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrder(someEntityA, someEntityB);
	}

	// works
	@Test
	void streamOrderByDoesntGroupEntitiesTogetherButGroupByIdIsUsed() {
		AnotherEntity anotherEntityA1 = new AnotherEntity(1);
		AnotherEntity anotherEntityB1 = new AnotherEntity(2);
		AnotherEntity anotherEntityA2 = new AnotherEntity(3);
		AnotherEntity anotherEntityB2 = new AnotherEntity(4);

		transactionTemplate.executeWithoutResult(s -> {
			anotherEntityRepository.save(anotherEntityA1);
			anotherEntityRepository.save(anotherEntityB1);
			anotherEntityRepository.save(anotherEntityA2);
			anotherEntityRepository.save(anotherEntityB2);
		});

		SomeEntity someEntityA = new SomeEntity(5, Lists.list(anotherEntityA1, anotherEntityA2));
		SomeEntity someEntityB = new SomeEntity(6, Lists.list(anotherEntityB1, anotherEntityB2));

		transactionTemplate.executeWithoutResult(s -> {
			someEntityRepository.save(someEntityA);
			someEntityRepository.save(someEntityB);
		});


		List<SomeEntity> result = transactionTemplate.execute(s -> someEntityRepository.findAllByIdInOrderById(Lists.list(5, 6))).collect(Collectors.toList());

		assertThat(result)
				.usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrder(someEntityA, someEntityB);
	}

	// works
	@Test
	void listOrderByDoesntGroupEntitiesTogether() {

		AnotherEntity anotherEntityA1 = new AnotherEntity(1);
		AnotherEntity anotherEntityB1 = new AnotherEntity(3);
		AnotherEntity anotherEntityA2 = new AnotherEntity(2);
		AnotherEntity anotherEntityB2 = new AnotherEntity(4);

		transactionTemplate.executeWithoutResult(s -> {
			anotherEntityRepository.save(anotherEntityA1);
			anotherEntityRepository.save(anotherEntityB1);
			anotherEntityRepository.save(anotherEntityA2);
			anotherEntityRepository.save(anotherEntityB2);
		});

		SomeEntity someEntityA = new SomeEntity(5, Lists.list(anotherEntityA1, anotherEntityA2));
		SomeEntity someEntityB = new SomeEntity(6, Lists.list(anotherEntityB1, anotherEntityB2));

		transactionTemplate.executeWithoutResult(s -> {
			someEntityRepository.save(someEntityA);
			someEntityRepository.save(someEntityB);
		});


		List<SomeEntity> result = transactionTemplate.execute(s -> someEntityRepository.findAllBySomeOrderedValuesIdIn(Lists.list(1,2,3,4)));

		assertThat(result)
				.usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrder(someEntityA, someEntityB);
	}

	// works
	@Test
	void streamOrderByAlreadyGroupsEntitiesTogether() {

		AnotherEntity anotherEntityA1 = new AnotherEntity(1);
		AnotherEntity anotherEntityB1 = new AnotherEntity(3);
		AnotherEntity anotherEntityA2 = new AnotherEntity(2);
		AnotherEntity anotherEntityB2 = new AnotherEntity(4);

		transactionTemplate.executeWithoutResult(s -> {
			anotherEntityRepository.save(anotherEntityA1);
			anotherEntityRepository.save(anotherEntityB1);
			anotherEntityRepository.save(anotherEntityA2);
			anotherEntityRepository.save(anotherEntityB2);
		});

		SomeEntity someEntityA = new SomeEntity(5, Lists.list(anotherEntityA1, anotherEntityA2));
		SomeEntity someEntityB = new SomeEntity(6, Lists.list(anotherEntityB1, anotherEntityB2));

		transactionTemplate.executeWithoutResult(s -> {
			someEntityRepository.save(someEntityA);
			someEntityRepository.save(someEntityB);
		});


		List<SomeEntity> result = transactionTemplate.execute(s -> someEntityRepository.findAllByIdIn(Lists.list(5, 6))).collect(Collectors.toList());

		assertThat(result)
				.usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrder(someEntityA, someEntityB);
	}
}
