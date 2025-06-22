package persistance;
import model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistance.utils.JdbcUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class SQLRepositoryUser implements IUserRepository {
    private final JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger(SQLRepositoryUser.class);

    public SQLRepositoryUser(Properties props) {
        logger.info("Initializing SQLRepositoryUser with properties: {}", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public Optional<User> findOne(Integer id) {
        logger.traceEntry("Finding user with id {}", id);
        try {
            if (id == null) {
                logger.error("Attempted to find user with null id");
                throw new RuntimeException("Attempted to find user with null id");
            }
            try (Connection connection = dbUtils.getConnection()) {
                if (connection == null) {
                    logger.error("Couldn't connect to the database");
                    throw new RuntimeException("Couldn't connect to the database.");
                }
                try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Users WHERE id = ?")) {
                    stmt.setInt(1, id);
                    try (ResultSet result = stmt.executeQuery()) {
                        if (result.next()) {
                            User x = new User(result.getInt(1), result.getString(2), result.getString(3));
                            logger.traceExit("Found user: {}", x);
                            return Optional.of(x);
                        } else {
                            logger.traceExit("No user found with id {}", id);
                            return Optional.empty();
                        }
                    }
                }
            } catch (SQLException e) {
                logger.error("Database error while finding user: {}", e.getMessage(), e);
                throw new RuntimeException("Database error: " + e.getMessage(), e);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while finding user by id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public Optional<User> findByAlias(String username) {
        logger.traceEntry("Finding user with username {}", username);
        try {
            if (username == null) {
                logger.error("Attempted to find user with null username");
                throw new RuntimeException("Attempted to find user with null username");
            }
            try (Connection connection = dbUtils.getConnection()) {
                if (connection == null) {
                    logger.error("Couldn't connect to the database");
                    throw new RuntimeException("Couldn't connect to the database.");
                }
                try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Users WHERE username = ?")) {
                    stmt.setString(1, username);
                    try (ResultSet result = stmt.executeQuery()) {
                        if (result.next()) {
                            User x = new User(result.getInt(1), result.getString(2), result.getString(3));
                            logger.traceExit("Found user: {}", x);
                            return Optional.of(x);
                        } else {
                            logger.traceExit("No user found with username {}", username);
                            return Optional.empty();
                        }
                    }
                }
            } catch (SQLException e) {
                logger.error("Database error while finding user: {}", e.getMessage(), e);
                throw new RuntimeException("Database error: " + e.getMessage(), e);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while finding user by username {}: {}", username, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<User> findAll() {
        logger.traceEntry("Finding all users");

        try (Connection connection = dbUtils.getConnection()) {
            if (connection == null) {
                logger.error("Couldn't connect to the database");
                throw new RuntimeException("Couldn't connect to the database.");
            }

            List<User> xs = new ArrayList<>();
            try (PreparedStatement preStmt = connection.prepareStatement("SELECT * FROM Users");
                 ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    User x = new User(result.getInt(1), result.getString(2), result.getString(3));
                    xs.add(x);
                }
            }
            logger.traceExit("Found {} users", xs.size());
            return xs;

        } catch (SQLException e) {
            logger.error("Database error while finding all users: {}", e.getMessage(), e);
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while finding all users: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> add(User entity) {
        logger.traceEntry("Saving user {}", entity);
        if (entity == null) {
            logger.error("Attempted to add null user");
            throw new RuntimeException("Attempted to add null user");
        }
        try {
            try (Connection connection = dbUtils.getConnection()) {
                if (connection == null) {
                    logger.error("Couldn't connect to the database.");
                    throw new RuntimeException("Couldn't connect to the database.");
                }
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO Users (username, password) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, entity.getUsername());
                    stmt.setString(2, entity.getPassword());

                    int rowsAffected = stmt.executeUpdate();
                    ResultSet keys = stmt.getGeneratedKeys();
                    if (keys.next()) {
                        entity.setId(keys.getInt(1));
                    }
                    if (rowsAffected > 0) {
                        logger.traceExit("Successfully saved user with id {}", entity.getId());
                        return Optional.of(entity);
                    } else {
                        logger.error("Failed to insert user, no rows affected.");
                        return Optional.empty();
                    }
                }
            } catch (SQLException e) {
                logger.error("Database error while adding user: {}", e.getMessage(), e);
                throw new RuntimeException("Database error: " + e.getMessage(), e);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Validation error while adding user: {}", e.getMessage(), e);
            throw new RuntimeException("Validation error: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> delete(Integer id) {
        logger.traceEntry("Deleting user with id {}", id);
        if (id == null) {
            logger.error("Attempted to delete user with null id");
            throw new RuntimeException("Attempted to delete user with null id");
        }
        try (Connection connection = dbUtils.getConnection()) {
            if (connection == null) {
                logger.error("Couldn't connect to the database.");
                throw new RuntimeException("Couldn't connect to the database.");
            }
            try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM Users WHERE id = ?")) {
                stmt.setInt(1, id);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    logger.traceExit("Successfully deleted user with id {}", id);
                    return Optional.of(new User(id));
                } else {
                    logger.warn("No user found with id {} for deletion.", id);
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            logger.error("Database error while deleting user with id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while deleting user with id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> update(User entity, User newEntity) {
        logger.traceEntry("Updating user from {} to {}", entity, newEntity);
        try {
            if (entity == null || newEntity == null) {
                logger.error("Attempted to update with null entity/newEntity");
                throw new RuntimeException("Attempted to update with null entity/newEntity");
            }

            if (entity.getId() == null) {
                logger.error("Attempted to update entity with null id");
                throw new RuntimeException("Cannot update user with null id");
            }

            try (Connection connection = dbUtils.getConnection()) {
                if (connection == null) {
                    logger.error("Couldn't connect to the database.");
                    throw new RuntimeException("Couldn't connect to the database.");
                }
                try (PreparedStatement stmt = connection.prepareStatement(
                        "UPDATE Users SET username = ?, password = ? WHERE id = ?")) {
                    stmt.setString(1, newEntity.getUsername());
                    stmt.setString(2, newEntity.getPassword());
                    stmt.setInt(3, entity.getId());

                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        logger.traceExit("Successfully updated user with id {}", entity.getId());
                        return Optional.of(newEntity);
                    } else {
                        logger.error("Failed to update user with id {}, no rows affected.", entity.getId());
                        return Optional.empty();
                    }
                }
            } catch (SQLException e) {
                logger.error("Database error while updating user: {}", e.getMessage(), e);
                throw new RuntimeException("Database error: " + e.getMessage(), e);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Validation error while updating user: {}", e.getMessage(), e);
            throw new RuntimeException("Validation error: " + e.getMessage(), e);
        }
    }
}