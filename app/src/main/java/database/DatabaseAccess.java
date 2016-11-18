package database;

import android.content.Context;

import java.sql.Connection;;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import exception.NonUniqueEmailException;
import exception.NonUniqueUserNameException;
import model.User;

/**
 * Created by hungdo on 11/15/16.
 */

public class DatabaseAccess {
    private static DatabaseAccess databaseAccess;
    private static Connection connection;
    private Context context;
    private User user;

    /**
     * Call only once for setup Facade class
     */
    public static void Initialize() {
        databaseAccess = new DatabaseAccess();
    }

    /**
     * Setup the context for database
     * @param context the stage which user are at
     */
    public void setContext(Context context) {
        this.context = context;
    }

    public void setConnection(Connection c) {
        connection = c;
    }

    public Context getContext() {
        return context;
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * get database
     * @return DatabaseAccess
     */
    public static DatabaseAccess getDatabaseAccess() {
        return databaseAccess;
    }

    /**
     * Get user
     * @return User who are loggin
     */
    public User getUser() {
        return user;
    }

    /**
     * Set user
     * @param user user who will loggin
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * returns the user with the given username in the system if one exists
     * Null otherwise
     * @param username the username of the User that will be returned
     * @return User the user that matches the given username
     */
    public User getUserByUserName(String username) {
        try {

            String query = "SELECT U.username, U.password, " +
                    "S.GTechEmail, S.majorName, S.year, U.type " +
                    "FROM USERS AS U " +
                    "LEFT JOIN STUDENTS AS S " +
                    "ON U.Username=S.Username " +
                    "WHERE U.username='" + username + "';";
            Statement statement = connection.createStatement();

            ResultSet statementResults = statement.executeQuery(query);

            if (statementResults.next()) {
                User user = new User(
                        statementResults.getString(1),
                        statementResults.getString(2),
                        statementResults.getString(3),
                        statementResults.getString(4),
                        statementResults.getInt(5),
                        statementResults.getInt(6)
                );
                return user;
            }

            return null;

        } catch (SQLException e) {

            System.out.println("Could not connect to the database: " + e.getMessage());
        }

        return null;
    }


    /**
     * Insert new user into database
     * @param user new user
     */
    public void createUser(User user) throws NonUniqueUserNameException, NonUniqueEmailException {
        String usename = user.getUsername();
        String password = user.getPassword();
        String email = user.getEmail();
        String major = user.getMajor();
        int year = user.getYear();
        User u = getUserByUserName(user.getUsername());
        if (u != null) {
            throw new NonUniqueUserNameException();
        } else {
            try {
                String query = "INSERT INTO USERS (" +
                        "Username, " +
                        "Password, " +
                        "Type) " +
                        "VALUES ('" + usename +
                        "', '" + password + "', '0'" +
                        ");";
                Statement statement = connection.createStatement();
                statement.execute(query);
                query = "INSERT INTO STUDENTS (" +
                        "GTechEmail, " +
                        "Username, " +
                        "MajorName, " +
                        "Year) " +
                        "VALUES (" +
                        "'" + email + "', " +
                        "'" + usename + "', " +
                        "'" + major + "', " +
                        "'" + year + "'" +
                        ");";
                statement = connection.createStatement();
                statement.execute(query);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                try {
                    String query = "DELETE FROM USERS " +
                            "WHERE Username = '" + usename + "';";
                    Statement statement = connection.createStatement();
                    statement.execute(query);
                } catch (SQLException s) {
                    System.out.println(s.getMessage());
                }
                throw new NonUniqueEmailException();

            }
        }
    }

}
