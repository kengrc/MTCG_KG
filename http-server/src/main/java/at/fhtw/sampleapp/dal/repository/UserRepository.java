package at.fhtw.sampleapp.dal.repository;

import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.sampleapp.dal.DataAccessException;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.model.Battle;
import at.fhtw.sampleapp.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class UserRepository {
    public UserRepository() {

    }

    public HttpStatus postUser(User user, UnitOfWork unitOfWork) {
        if (userExists(user.getPlayer_token(), unitOfWork)) {
            return HttpStatus.CONFLICT;
        }
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    INSERT INTO player(player_username, player_password, player_token) VALUES (?,?,?)
                """)) {
            preparedStatement.setString(1, user.getPlayer_username());
            preparedStatement.setString(2, user.getPlayer_password());
            preparedStatement.setString(3, user.getPlayer_token());

            preparedStatement.executeUpdate();
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next())
//            {
//                user.setPlayer_id(resultSet.getInt(1));
//            }

            return HttpStatus.CREATED;

        } catch (SQLException e) {
            System.err.println("postUser() doesn't work");
            throw new DataAccessException("INSERT NICHT ERFOLGREICH", e);
        }
    }

    public boolean userExists (String player_token, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    SELECT player_id FROM player WHERE player_token LIKE ?
                """)) {
            preparedStatement.setString(1, player_token);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next() ) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println("userExists() doesn't work");
            throw new DataAccessException("SELECT NICHT ERFOLGREICH", e);
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
            System.err.println("getPlayerId() doesn't work");
            throw new DataAccessException("INSERT NICHT ERFOLGREICH", e);
        }
        return -1;
    }

    public String getPlayerUserName(int playerId, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    SELECT player_username FROM player WHERE player_id = ?
                """)) {
            preparedStatement.setInt(1,playerId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
            {
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            System.err.println("getPlayerUserName() doesn't work");
            throw new DataAccessException("INSERT NICHT ERFOLGREICH", e);
        }
        return null;
    }

    public Collection<User> getUserData(String username, UnitOfWork unitOfWork) {
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
            System.err.println("getUserData() doesn't work");
            throw new DataAccessException("SELECT NICHT ERFOLGREICH", e);
        }
    }

    public void updateUserData(User user, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    UPDATE player SET player_name = ?,
                                      player_bio = ?,
                                      player_image = ? WHERE player_username =?;
                """)) {
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

    public Collection<User> getUserStats(String currentToken, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    SELECT player_username, player_elo, player_total_battles, player_wins, player_losses FROM player WHERE player_token = ?
                """)) {
            preparedStatement.setString(1, currentToken);
            ResultSet resultSet = preparedStatement.executeQuery();
            Collection<User> userStats = new ArrayList<>();
            if(resultSet.next())
            {
                User user = new User(
                        resultSet.getString(1),
                        resultSet.getInt(2),
                        resultSet.getInt(3),
                        resultSet.getInt(4),
                        resultSet.getInt(5));
                userStats.add(user);
                return userStats;
            }
            return null;
        } catch (SQLException e) {
            System.err.println("getUserStats() doesn't work");
            throw new DataAccessException("SELECT NICHT ERFOLGREICH", e);
        }
    }

    public Collection<User> getUserScore(UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    SELECT player_username, player_elo FROM player ORDER BY player_elo DESC
                """)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            Collection<User> userScore = new ArrayList<>();
            while(resultSet.next())
            {
                User user = new User(
                        resultSet.getString(1),
                        resultSet.getInt(2));
                userScore.add(user);
            }
            return userScore;
        } catch (SQLException e) {
            System.err.println("getUserScore() doesn't work");
            throw new DataAccessException("SELECT NICHT ERFOLGREICH", e);
        }
    }

    public void updateUserStats(Battle battle, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    UPDATE player SET player_total_battles = player_total_battles + ?,
                                      player_wins = player_wins + ?,
                                      player_losses = player_losses + ?,
                                      player_elo = player_elo + ? WHERE player_id = ?;
                """)) {
            // winner and loser
            preparedStatement.setInt(1, 1);
            if ((battle.getBattle_player_a_id() == battle.getBattle_winner_id()) ||
                (battle.getBattle_player_b_id() == battle.getBattle_winner_id())) {
                // winner
                preparedStatement.setInt(2, 1);
                preparedStatement.setInt(3, 0);
                preparedStatement.setInt(4, 17);
                preparedStatement.setInt(5, battle.getBattle_winner_id());
                preparedStatement.executeUpdate();

                // loser
                preparedStatement.setInt(2, 0);
                preparedStatement.setInt(3, 1);
                preparedStatement.setInt(4, -14);
                if (battle.getBattle_player_a_id() == battle.getBattle_winner_id()) {
                    preparedStatement.setInt(5, battle.getBattle_player_b_id());
                } else if (battle.getBattle_player_b_id() == battle.getBattle_winner_id()) {
                    preparedStatement.setInt(5, battle.getBattle_player_a_id());
                }
            }
            // draw
            else {
                preparedStatement.setInt(2, 0);
                preparedStatement.setInt(3, 0);
                preparedStatement.setInt(4, 0);
                preparedStatement.setInt(5, battle.getBattle_player_a_id());
                preparedStatement.executeUpdate();
                preparedStatement.setInt(5, battle.getBattle_player_b_id());
            }
            // for loser update and draw player B update
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("updateUserStats() doesn't work");
            throw new DataAccessException("UPDATE NICHT ERFOLGREICH", e);
        }
    }
}
