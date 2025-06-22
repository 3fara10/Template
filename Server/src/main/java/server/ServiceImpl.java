package server;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import model.Game;
import model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistance.GameHibernateRepository;
import persistance.SQLRepositoryUser;
import service.IGameService;
import service.IObserver;
import service.ServiceEvent;
import service.ServiceException;

public class ServiceImpl implements IGameService {

    private static final Logger log = LogManager.getLogger(ServiceImpl.class);

    private final GameHibernateRepository gameRepository;
    private final SQLRepositoryUser userRepository;


    private final Map<String, IObserver> loggedClients;
    private final int defaultThreadsNo = 5;

    public ServiceImpl(Properties props) {
        this.gameRepository = new GameHibernateRepository();
        this.userRepository = new SQLRepositoryUser(props);

        this.loggedClients = new ConcurrentHashMap<>();

        log.info("GameService initialized with manual DI");
    }

    public ServiceImpl(SQLRepositoryUser userRepository, GameHibernateRepository gameRepository, Map<String, IObserver> loggedClients) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.loggedClients = loggedClients;
        log.info("GameService initialized with explicit DI");
    }

    @Override
    public synchronized void addObserver(String identifier, IObserver observer) throws ServiceException {
        if (loggedClients.get(identifier) != null) {
            log.warn("Attempted to add observer for already logged-in user: {}", identifier);
            throw new ServiceException("User already logged in.");
        }
        loggedClients.put(identifier, observer);
        log.info("Observer added for user: {}. Current logged clients: {}", identifier, loggedClients.size());
    }

    @Override
    public synchronized void removeObserver(String identifier, IObserver observer) throws ServiceException {
        if(observer.equals(loggedClients.get(identifier))) {
            loggedClients.remove(identifier);
            log.info("Observer removed for user: {}. Current logged clients: {}", identifier, loggedClients.size());
        }
        else {
            log.warn("Attempted to remove observer for user {} who is not logged in or observer mismatch.", identifier);
            throw new ServiceException("User not logged in.");
        }
    }

    @Override
    public void notifyObservers(ServiceEvent serviceEvent, Object data) throws ServiceException {
        ExecutorService executor= Executors.newFixedThreadPool(defaultThreadsNo);
        log.info("Notifying {} observers about event: {}", loggedClients.size(), serviceEvent);
        for(IObserver us : loggedClients.values()) {
            executor.execute(() -> {
                try {
                    us.handleEvent(serviceEvent, data);
                } catch (ServiceException e) {
                    log.error("Error notifying observer: {}", e.getMessage(), e);
                }
            });
        }
        executor.shutdown();
    }

    @Override
    public User login(String username, String password, IObserver clientObserver) throws ServiceException {
        log.traceEntry("Attempting login for user: {}", username);

        if (loggedClients.containsKey(username)) {
            log.warn("User {} already logged in.", username);
            throw new ServiceException("User already logged in.");
        }
        Optional<User> userOptional = userRepository.findByAlias(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) { // Asigură-te că getPassword() returnează parola stocată
                addObserver(username, clientObserver); // Adăugăm observatorul
                log.info("User {} logged in successfully.", username);
                return user;
            } else {
                log.warn("Login failed for user {}: Invalid password.", username);
                throw new ServiceException("Invalid username or password."); // Mesaj generic pentru securitate
            }
        } else {
            log.warn("Login failed for user {}: User not found.", username);
            throw new ServiceException("Invalid username or password."); // Mesaj generic pentru securitate
        }
    }

    @Override
    public synchronized boolean logout(String username, IObserver clientObserver) throws ServiceException {
        log.info("Attempting logout for username: {}", username);
        if (loggedClients.get(username) != clientObserver) {
            log.warn("Logout failed for username {}: client observer mismatch.", username);
            throw new ServiceException("User " + username + " is not logged in with this client.");
        }
        try {
            removeObserver(username, clientObserver);
            log.info("[ SERVER UPDATE ] Nr of users logged in {}", loggedClients.size());
            log.info("User {} logged out successfully.", username);
            return true;
        } catch (ServiceException e) {
            log.error("Logout failed for username {}: {}", username, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Game startNewGame(String playerUsername, IObserver clientObserver) throws ServiceException {
        log.traceEntry("Attempting to start a new game for player: {}", playerUsername);
        try {
            Game newGame = new Game(null, this.userRepository.findByAlias(playerUsername).get().getId(), 0, LocalDateTime.now());
            Optional<Game> addedGame = gameRepository.add(newGame);

            if (addedGame.isPresent()) {
                Game game = addedGame.get();
                log.info("New game started successfully for player {} with ID: {}", playerUsername, game.getId());
                return game;
            } else {
                log.error("Failed to add new game to repository for player: {}", playerUsername);
                throw new ServiceException("Failed to start new game: could not persist game data.");
            }
        } catch (Exception e) {
            log.error("Error starting new game for player {}: {}", playerUsername, e.getMessage(), e);
            throw new ServiceException("Error starting new game: " + e.getMessage());
        } finally {
            log.traceExit("Finished starting new game for player: {}", playerUsername);
        }
    }

//    @Override
//    public GuessResult makeGuess(Long gameId, int x, int y) throws ServiceException {
//        log.warn("Method makeGuess not yet implemented.");
//        throw new ServiceException("Method makeGuess not yet implemented.");
//    }
//
//    @Override
//    public Optional<Game> getFinishedGame(String playerId) throws ServiceException {
//        log.warn("Method getFinishedGame not yet implemented.");
//        return Optional.empty();
//    }
//
//    @Override
//    public boolean finishGame(Long gameId) throws ServiceException {
//        log.warn("Method finishGame not yet implemented.");
//        return false;
//    }
}