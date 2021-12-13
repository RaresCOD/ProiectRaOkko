package ro.ubbcluj.map.proiectraokko4.repository.db;


import ro.ubbcluj.map.proiectraokko4.domain.Utilizator;
import ro.ubbcluj.map.proiectraokko4.domain.validators.Validator;
import ro.ubbcluj.map.proiectraokko4.repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Repo care salveaza si aduce datele din baza de date "users"
 */
public class UtilizatorDbRepository implements Repository<Long, Utilizator> {
    private String url;
    private String _username;
    private String password;
    private Validator<Utilizator> validator;

    /**
     *
     * @param url url-ul cu care se conecteaza la baza de date
     * @param username - usernameul
     * @param password - parola
     * @param validator - validator pentru prietenie
     */
    public UtilizatorDbRepository(String url, String username, String password, Validator<Utilizator> validator) {
        this.url = url;
        this._username = username;
        this.password = password;
        this.validator = validator;
    }
    @Override
    public Utilizator findOne(Long id) {
        String sql = "select * from users where id = " + id;

        try (Connection connection = DriverManager.getConnection(url, _username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
           ResultSet result = ps.executeQuery();
           while (result.next()) {
               String firstName = result.getString("first_name");
               String lastName = result.getString("last_name");
               String username = result.getString("username");
               Utilizator utilizator = new Utilizator(username, firstName, lastName);
               utilizator.setId(id);
               String sql1 = "select friendship.id2, friendship.id1\n" +
                       "from users\n" +
                       "inner join friendship\n" +
                       "on users.id = friendship.id1 or users.id = friendship.id2 \n" +
                       "where users.id = " + id;
               try(Connection connection1 = DriverManager.getConnection(url, _username, password);
               PreparedStatement ps1 = connection1.prepareStatement(sql1)) {
                   ResultSet resultSet = ps1.executeQuery();
                   while(resultSet.next()) {
                       Long id1 = Long.valueOf(resultSet.getInt("id1"));
                       Long id2 = Long.valueOf(resultSet.getInt("id2"));
                       Long idBun;
                       if(id1 != id) {
                           idBun = id1;
                       } else {
                           idBun = id2;
                       }

                       String sql2 = "select * from users where id = " + idBun;

                       try(Connection connection2 = DriverManager.getConnection(url, _username, password);
                           PreparedStatement ps2 = connection.prepareStatement(sql2)) {
                           ResultSet resultSet1 = ps2.executeQuery();
                           while (resultSet1.next()) {
                               String firstName2 = resultSet1.getString("first_name");
                               String lastName2 = resultSet1.getString("last_name");
                               String username2 = resultSet1.getString("username");
                               Utilizator utilizator1 = new Utilizator(username2, firstName2, lastName2);
                               utilizator1.setId(idBun);
                               utilizator.addFriend(utilizator1);
                               }
                       }
                   }
               }

               return utilizator;
           }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Utilizator> findAll() {
        Set<Utilizator> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, _username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username = resultSet.getString("username");

                Utilizator utilizator = new Utilizator(username, firstName, lastName);
                utilizator.setId(id);
                users.add(utilizator);
            }
            return users.stream().toList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users.stream().toList();
    }

    @Override
    public Utilizator save(Utilizator entity) {

        String sql = "insert into users (username, first_name, last_name) values (?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, _username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getUsername());
            ps.setString(2, entity.getFirstName());
            ps.setString(3, entity.getLastName());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Utilizator delete(Long id) {

        String sql = "delete from users where id = " + id;

        try (Connection connection = DriverManager.getConnection(url, _username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Utilizator update(Utilizator entity) {

        String sql = "update users set first_name = ?, last_name = ?  where id = ? ";
        try (Connection connection = DriverManager.getConnection(url, _username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setInt(3, Math.toIntExact(entity.getId()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

