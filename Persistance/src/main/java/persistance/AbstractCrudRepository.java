package persistance;

import jakarta.persistence.TypedQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import persistance.utils.HibernateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AbstractCrudRepository<ID, T> implements IRepository<ID, T> {

    protected final static Logger log = LogManager.getLogger();
    protected Class<T> entityClass;

    public AbstractCrudRepository() {
        log.info("Initializing AbstractCrudRepository: {}"); // Log inițial, va fi îmbunătățit de constructorul cu entityClass
    }

    public AbstractCrudRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
        log.info("Initializing AbstractCrudRepository for entity: {}.", entityClass.getSimpleName());
    }

    @Override
    public Optional<T> add(T entity) {
        log.traceEntry("Saving {} entity: {}", entityClass.getSimpleName(), entity);
        try(Session session = HibernateUtils.getInstance().openSession()){
            session.beginTransaction();
            ID id = (ID) session.save(entity);
            session.getTransaction().commit();
            log.traceExit("Successfully saved {} entity with new ID: {}");
            return Optional.of(entity);
        }
        catch(Exception ex){
            log.error("Error saving {} entity: {}", entityClass.getSimpleName(), ex.getMessage(), ex);
            log.traceExit();
            return Optional.empty();
        }
    }

    @Override
    public Optional<T> delete(ID id){
        log.traceEntry("Removing {} entity with id: {}", entityClass.getSimpleName(), id);
        try(Session session = HibernateUtils.getInstance().openSession()) {
            session.beginTransaction();
            T entity = session.get(entityClass, id);
            if (entity != null) {
                session.remove(entity);
                session.getTransaction().commit();
                log.traceExit("Successfully removed {} entity with id: {}");
                return Optional.of(entity);
            } else {
                session.getTransaction().rollback();
                log.warn("{} entity with id {} not found for deletion.", entityClass.getSimpleName(), id);
                log.traceExit();
                return Optional.empty();
            }
        }
        catch(Exception ex){
            log.error("Error removing {} entity with id {}: {}", entityClass.getSimpleName(), id, ex.getMessage(), ex);
            log.traceExit();
            return Optional.empty();
        }
    }

    @Override
    public Optional<T> update(T entity, T newEntity) {
        log.traceEntry("Updating {} entity: {}", entityClass.getSimpleName(), entity);
        try(Session session = HibernateUtils.getInstance().openSession()){
            session.beginTransaction();
            session.update(entity);
            session.getTransaction().commit();
            log.traceExit("Successfully updated {} entity: {}");
            return Optional.of(entity);
        }
        catch(Exception ex){
            log.error("Error updating {} entity: {}", entityClass.getSimpleName(), ex.getMessage(), ex);
            log.traceExit();
            return Optional.empty();
        }
    }

    @Override
    public Optional<T> findOne(ID id)  {
        log.traceEntry("Finding {} entity with id: {}", entityClass.getSimpleName(), id);
        try(Session session = HibernateUtils.getInstance().openSession()){
            session.beginTransaction();
            T entity = session.get(entityClass, id);
            session.getTransaction().commit();
            if (entity != null) {
                log.traceExit();
                return Optional.of(entity);
            } else {
                log.warn("{} entity with id {} not found.", entityClass.getSimpleName(), id);
                log.traceExit();
                return Optional.empty();
            }
        }
        catch(Exception ex){
            log.error("Error finding {} entity by id {}: {}", entityClass.getSimpleName(), id, ex.getMessage(), ex);
            log.traceExit();
            return Optional.empty();
        }
    }

    public Iterable<T> findAll(){
        log.traceEntry("Finding all {} entities.", entityClass.getSimpleName());
        try(Session session =  HibernateUtils.getInstance().openSession()){
            session.beginTransaction();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
            Root<T> root = criteriaQuery.from(entityClass);
            criteriaQuery.select(root);
            TypedQuery<T> typedQuery = session.createQuery(criteriaQuery);
            List<T> allEntities = typedQuery.getResultList();
            session.getTransaction().commit();
            log.traceExit("{} entities returned: {}");
            return allEntities;
        }
        catch(Exception ex){
            log.error("Error finding all {} entities: {}", entityClass.getSimpleName(), ex.getMessage(), ex);
            log.traceExit();
            return new ArrayList<>();
        }
    }
}