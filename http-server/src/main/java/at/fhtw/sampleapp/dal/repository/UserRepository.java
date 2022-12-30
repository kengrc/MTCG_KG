package at.fhtw.sampleapp.dal.repository;

import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.sampleapp.dal.DataAccessException;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.model.Card;
import at.fhtw.sampleapp.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class UserRepository {
//    private UnitOfWork unitOfWork;

//    public UserRepository(UnitOfWork unitOfWork)
//    {
//        this.unitOfWork = unitOfWork;
//    }

    public UserRepository() {

    }

    public HttpStatus postUser(User user, UnitOfWork unitOfWork) {
        if (userExists(user.getPlayer_username(), unitOfWork)) {
            return HttpStatus.CONFLICT;
        }
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    INSERT INTO player VALUES (DEFAULT, ?,?,?,?,?,?,?) RETURNING player_id
                """))
        {
            preparedStatement.setString(1, user.getPlayer_username());
            preparedStatement.setString(2, user.getPlayer_password());
            preparedStatement.setInt(3, user.getPlayer_coins());
            preparedStatement.setString(4, user.getPlayer_token());
            preparedStatement.setString(5, user.getPlayer_bio());
            preparedStatement.setString(6, user.getPlayer_image());
            preparedStatement.setString(7, user.getPlayer_name());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
            {
                user.setPlayer_id(resultSet.getInt(1));
            }

            return HttpStatus.CREATED;

        } catch (SQLException e) {
            System.err.println("postUser() doesn't work");
            throw new DataAccessException("INSERT NICHT ERFOLGREICH", e);
        }
    }

    public boolean userExists (String username, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    SELECT player_id FROM player WHERE player_username LIKE ?
                """)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next() ) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println("postUser() doesn't work");
            throw new DataAccessException("INSERT NICHT ERFOLGREICH", e);
        }
        return false;
    }

    public int getPlayerId(String currentToken, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    SELECT player_id FROM player WHERE player_token = ?
                """)) {
            preparedStatement.setString(1,currentToken);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
            {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("buyerId() doesn't work");
            throw new DataAccessException("INSERT NICHT ERFOLGREICH", e);
        }
        return -1;
    }

    public String getPlayerUserName(String currentToken, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    SELECT player_username FROM player WHERE player_token = ?
                """)) {
            preparedStatement.setString(1,currentToken);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
            {
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            System.err.println("buyerId() doesn't work");
            throw new DataAccessException("INSERT NICHT ERFOLGREICH", e);
        }
        return null;
    }

    public Collection getUserData(String username, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    SELECT player_username, player_coins, player_bio, player_image, player_name FROM player WHERE player_username = ? 
                """)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            Collection<User> userData = new ArrayList<>();
            if(resultSet.next())
            {
                User user = new User(
                        resultSet.getString(1),
                        resultSet.getInt(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5));
                userData.add(user);
                return userData;
            }
            return null;
        } catch (SQLException e) {
            System.err.println("getDeck() doesn't work");
            throw new DataAccessException("SELECT NICHT ERFOLGREICH", e);
        }
    }

    public void updateUserData(User user, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    UPDATE player SET player_name = ?,
                                      player_bio = ?,
                                      player_image = ? WHERE player_username =?; 
                """))
        {
            preparedStatement.setString(1, user.getPlayer_name());
            preparedStatement.setString(2, user.getPlayer_bio());
            preparedStatement.setString(3, user.getPlayer_image());
            preparedStatement.setString(4, user.getPlayer_username());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("updateUserData() doesn't work");
            throw new DataAccessException("UPDATE NICHT ERFOLGREICH", e);
        }
    }
}
