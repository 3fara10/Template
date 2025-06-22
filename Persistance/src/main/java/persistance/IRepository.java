package persistance;

import java.util.Optional;

/**
 * Generic repository interface that defines standard CRUD operations.
 *
 * @param <ID> The type of the entity identifier
 * @param <T> The type of the entity, which must extend Entity<ID>
 */
public interface IRepository<ID, T> {

    /**
     * @param id - the id of the entity to be returned
     *                 id must not be null
     * @return the entity with the specified id
     *         or null if there is no entity with the given id
     * @throws IllegalArgumentException - if id is null
     */
    Optional<T> findOne(ID id);

    /**
     * @return all entities
     */
    Iterable<T> findAll();

    /**
     * saves the given entity in repository
     * @param entity - entity must not be null
     * @return null - if the given entity is saved
     *                otherwise returns the entity (id already exists)
     * @throws IllegalArgumentException - if the given entity is null
     */
    Optional<T> add(T entity);

    /**
     * removes the entity with the specified id
     * @param id - id must not be null
     * @return the removed entity or null if there is no entity with the given id
     * @throws IllegalArgumentException - if the given id is null
     */
    Optional<T> delete(ID id);

    /**
     *
     * @param entity - entity must not be null
     * @return null - if the entity is updated
     *                otherwise returns the entity - (e.g. id does not exist)
     * @throws IllegalArgumentException - if the given entity is null
     */
    Optional<T> update(T entity,T newEntity);
}