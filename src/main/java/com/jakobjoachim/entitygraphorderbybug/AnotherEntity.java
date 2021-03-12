package com.jakobjoachim.entitygraphorderbybug;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AnotherEntity {
	@Id
	private Integer id;

	public AnotherEntity() {
	}

	public AnotherEntity(Integer id) {
		this.id = id;
	}
}
