package ro.ubbcluj.map.proiectraokko4.repository.db;


import ro.ubbcluj.map.proiectraokko4.domain.Event;
import ro.ubbcluj.map.proiectraokko4.domain.validators.ValidationException;
import ro.ubbcluj.map.proiectraokko4.domain.validators.Validator;
import ro.ubbcluj.map.proiectraokko4.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repo care salveaza si aduce datele din baza de date "friendship"
 */
public class EventDbRepository implements Repository<Long, Event> {
    private String url;
    private String username;
    private String password;
    private Validator<Event> validator;

    /**
     * @param url       url-ul cu care se conecteaza la baza de date
     * @param username  - usernameul
     * @param password  - parola
     * @param validator - validator pentru prietenie
     */
    public EventDbRepository(String url, String username, String password, Validator<Event> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Event findOne(Long id) {
        String sql = "select * from events where id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, Math.toIntExact(id));
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                Long idR = result.getLong("id");
                String name = result.getString("name");
                String description = result.getString("description");
                String location = result.getString("location");
                LocalDateTime date = result.getTimestamp("date").toLocalDateTime();
                String participants = result.getString("participants");
                if(participants.isBlank() || participants.isEmpty()) participants = null;
                Event event = new Event(name, description, location, date, participants);
                event.setId(idR);
                return event;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public List<Event> findAll() {
        List<Event> events = new ArrayList<>();
        String sql = "select * from events";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                Long idR = result.getLong("id");
                String name = result.getString("name");
                String description = result.getString("description");
                String location = result.getString("location");
                LocalDateTime date = result.getTimestamp("date").toLocalDateTime();
                String participants = result.getString("participants");
                if(participants.isBlank() || participants.isEmpty()) participants = null;
                Event event = new Event(name, description, location, date, participants);
                event.setId(idR);
                events.add(event);
            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Event save(Event entity) throws ValidationException {
        validator.validate(entity);

        String sql = "insert into events (name, description, location, date, participants) values (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getName());
            ps.setString(2, entity.getDescription());
            ps.setString(3, entity.getLocation());
            ps.setTimestamp(4, Timestamp.valueOf(entity.getDate()));
            ps.setString(5, entity.getParticipants());

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Event delete(Long id) {
        String sql = "delete from events where id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, Math.toIntExact(id));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Event update(Event entity) {
        String sql = "update events set name = ?, description = ?, location = ?, date = ?, participants = ?  where id = ? ";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getName());
            ps.setString(2, entity.getDescription());
            ps.setString(3, entity.getLocation());
            ps.setTimestamp(4, Timestamp.valueOf(entity.getDate()));
            ps.setString(5, entity.getParticipants());
            ps.setInt(6, Math.toIntExact(entity.getId()));
            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

