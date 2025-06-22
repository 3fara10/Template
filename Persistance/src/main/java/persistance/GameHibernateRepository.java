package persistance;


import model.Game;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameHibernateRepository extends AbstractCrudRepository<Integer, Game> implements IGameRepository {

    private static final Logger log = LogManager.getLogger(GameHibernateRepository.class);

    public GameHibernateRepository() {
        super(Game.class);
        log.info("Initializing GameHibernateRepository.");
    }
}