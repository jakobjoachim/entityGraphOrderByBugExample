When using an EntityGraph and a stream query, the entities contained in the root entity need to be returned by the SQL in adjacent rows. When this is not the case (for example by using `orderBy`) the repository call will return one result for each line in the resulting SQL
Example:
```Java
@Entity
public class SomeEntity {
	@Id
	private Integer id;

	@OneToMany
	@OrderBy
	private List<AnotherEntity> someOrderedValues;

       ...
}

@Entity
public class AnotherEntity {
	@Id
  	private Integer id;

	public AnotherEntity() {
	}

	...
}

@Repository
public interface SomeEntityRepository extends CrudRepository<SomeEntity, Integer> {
	@EntityGraph(attributePaths = "someOrderedValues", type = EntityGraph.EntityGraphType.LOAD)
	Stream<SomeEntity> findAllByIdIn(List<Integer> ids);
}
```
We have two Objects in the database (H2, JSON just for ease of overview):
```JSON
{
    "id": 99,
    "someOrderedValue": [{
         "id": 1
    }, {
         "id": 3
    }]
}

{
    "id": 100,
    "someOrderedValue": [{
         "id": 2
    }, {
         "id": 4
    }]
}
```
When we call the repository the SQL will look like this:
```SQL
select someentity0_.id                     as id1_1_0_,
       anotherent2_.id                     as id1_0_1_,
       someordere1_.some_entity_id         as some_ent1_2_0__,
       someordere1_.some_ordered_values_id as some_ord2_2_0__
from some_entity someentity0_
         left outer join some_entity_some_ordered_values someordere1_
                         on someentity0_.id = someordere1_.some_entity_id
         left outer join another_entity anotherent2_
                         on someordere1_.some_ordered_values_id = anotherent2_.id
where someentity0_.id in (99, 100)
order by anotherent2_.id asc
```
Resulting in the following:

|      | someentity0_.id | anotherent2_.id | someordere1_.some_entity_id | someordere1_.some_ordered_values_id |
| :---: | :-------------: | :-------------: | :-------------------------: | :---------------------------------: |
|1     | 99              | 1               | 99                          | 1                                   |
|2     | 100             | 2               | 100                         | 2                                   |
|3     | 99              | 3               | 99                          | 3                                   |
|4     | 100             | 4               | 100                         | 4                                   |

Because we are streaming the results we get 4 results (one for each line) because as soon as the cursor finds a new entity it will return the current one. When a `groupById` is added to the Repository method everything will work correctly (line 2 and 3 are swapped in the table).
When using `List` instead of `Stream` in the Repository Method everything will work correctly as well.

This problem is present when using Stream + EntityGraph in situations where the SQL doesn't return the entities in adjacent rows.

The Github Issue is here: https://github.com/spring-projects/spring-data-jpa/issues/2174
