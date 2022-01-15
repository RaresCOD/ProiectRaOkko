package ro.ubbcluj.map.proiectraokko4.repository.db;


import ro.ubbcluj.map.proiectraokko4.Message.Message;
import ro.ubbcluj.map.proiectraokko4.domain.Utilizator;
import ro.ubbcluj.map.proiectraokko4.domain.validators.Validator;
import ro.ubbcluj.map.proiectraokko4.repository.Repository;
import ro.ubbcluj.map.proiectraokko4.repository.paging.Page;
import ro.ubbcluj.map.proiectraokko4.repository.paging.PageImplementation;
import ro.ubbcluj.map.proiectraokko4.repository.paging.Pageable;
import ro.ubbcluj.map.proiectraokko4.repository.paging.PagingRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.sql.Date;
import java.util.*;
import java.util.stream.StreamSupport;

public class MessageDbRepository implements Repository<Long, Message> {
    private String url;
    private String username;
    private String password;
    private Validator<Message> validator;

    /**
     *
     * @param url url-ul cu care se conecteaza la baza de date
     * @param username - usernameul
     * @param password - parola
     * @param validator - validator pentru prietenie
     */
    public MessageDbRepository(String url, String username, String password, Validator<Message> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Message findOne(Long aLong) {
        String sql = "select * from messages where id = " + aLong;

        try(Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long idCurent = resultSet.getLong("id");
                Long from = resultSet.getLong("from1");

                String sql1 = "select * from users where id = " + from;

                PreparedStatement ps1 = connection.prepareStatement(sql1);
                ResultSet resultSet1 = ps1.executeQuery();
                Utilizator utilizator = new Utilizator("a","a","a", "a");
                while (resultSet1.next()) {
                    String firstName = resultSet1.getString("first_name");
                    String lastName = resultSet1.getString("last_name");
                    String username = resultSet1.getString("username");
                    utilizator.setFirstName(firstName);
                    utilizator.setLastName(lastName);
                    utilizator.setUsername(username);
                    utilizator.setId(Long.valueOf(from));
                }

                String toString = resultSet.getString("to1");
                String toString1 = toString.strip();
                String[] listTo = toString1.split(" ");
                List<Utilizator> list = new ArrayList<>();
                for(String curent: listTo) {
                    String sql2 = "select * from users where id = " + curent;

                    PreparedStatement ps2 = connection.prepareStatement(sql2);
                    ResultSet resultSet2 = ps2.executeQuery();
                    while (resultSet2.next()) {
                        String firstName1 = resultSet2.getString("first_name");
                        String lastName1 = resultSet2.getString("last_name");
                        String username1 = resultSet2.getString("username");
                        String password1 = resultSet2.getString("password");
                        Utilizator utilizator1 = new Utilizator(username1, firstName1, lastName1, password1);
                        utilizator1.setId(Long.valueOf(curent));
                        list.add(utilizator1);
                    }
                }
                String msg = resultSet.getString("msg");
                LocalDateTime date = resultSet.getTimestamp("data").toLocalDateTime();
                Message message = new Message(utilizator,list,msg);
                message.setId(idCurent);
                message.setData(date);
                return message;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Message> findAll() {
        String sql = "select * from messages";

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet resultSet = ps.executeQuery();
            Set<Message> all = new HashSet<>();
            while (resultSet.next()) {
                Long idCurent = resultSet.getLong("id");
                Long from = resultSet.getLong("from1");

                String sql1 = "select * from users where id = " + from;

                PreparedStatement ps1 = connection.prepareStatement(sql1);
                ResultSet resultSet1 = ps1.executeQuery();
                Utilizator utilizator = new Utilizator("a","a","a", "a");
                while (resultSet1.next()) {
                    String firstName = resultSet1.getString("first_name");
                    String lastName = resultSet1.getString("last_name");
                    String username = resultSet1.getString("username");
                    utilizator.setFirstName(firstName);
                    utilizator.setLastName(lastName);
                    utilizator.setUsername(username);
                    utilizator.setId(Long.valueOf(from));
                }

                String toString = resultSet.getString("to1");
                String toString1 = toString.strip();
                String[] listTo = toString1.split(" ");
                List<Utilizator> list = new ArrayList<>();
                for(String curent: listTo) {
                    String sql2 = "select * from users where id = " + curent;

                    PreparedStatement ps2 = connection.prepareStatement(sql2);
                    ResultSet resultSet2 = ps2.executeQuery();
                    while (resultSet2.next()) {
                        String firstName1 = resultSet2.getString("first_name");
                        String lastName1 = resultSet2.getString("last_name");
                        String username1 = resultSet2.getString("username");
                        String password1 = resultSet2.getString("password");
                        Utilizator utilizator1 = new Utilizator(username1, firstName1, lastName1, password1);
                        utilizator1.setId(Long.valueOf(curent));
                        list.add(utilizator1);
                    }
                }
                String msg = resultSet.getString("msg");
                LocalDateTime date = resultSet.getTimestamp("data").toLocalDateTime();
                Message message = new Message(utilizator,list,msg);
                message.setId(idCurent);
                message.setData(date);
                all.add(message);
            }
            return all.stream().toList();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Message save(Message entity) {
        validator.validate(entity);
        String sql = "insert into messages (from1, to1, msg, rplymsg, data) values (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            String to1 = String.valueOf(entity.getTo()
                    .stream()
                    .map(x -> x.getId().toString())
                    .reduce("",(u,v) -> u.concat(" " + v)));

            ps.setInt(1, Math.toIntExact(entity.getFrom().getId()));
            ps.setString(2, to1);
            ps.setString(3, entity.getMsg());
            if(entity.getReplyMsg() == null) {
                ps.setInt(4, 0);
            } else {
                ps.setInt(4, Math.toIntExact(entity.getReplyMsg().getId()));
            }
            ps.setTimestamp(5, Timestamp.valueOf(entity.getData()));

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Message delete(Long aLong) {
        return null;
    }

    @Override
    public Message update(Message entity) {
        return null;
    }
}
