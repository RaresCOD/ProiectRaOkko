package ro.ubbcluj.map.proiectraokko4.repository.db;


import ro.ubbcluj.map.proiectraokko4.domain.Prietenie;
import ro.ubbcluj.map.proiectraokko4.domain.Tuple;
import ro.ubbcluj.map.proiectraokko4.domain.Utilizator;
import ro.ubbcluj.map.proiectraokko4.domain.validators.ValidationException;
import ro.ubbcluj.map.proiectraokko4.domain.validators.Validator;
import ro.ubbcluj.map.proiectraokko4.repository.Repository;
import ro.ubbcluj.map.proiectraokko4.repository.paging.Page;
import ro.ubbcluj.map.proiectraokko4.repository.paging.Pageable;
import ro.ubbcluj.map.proiectraokko4.repository.paging.Paginator;
import ro.ubbcluj.map.proiectraokko4.repository.paging.PagingRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Repo care salveaza si aduce datele din baza de date "friendship"
 */
public class FriendshipDbRepository implements PagingRepository<Tuple<Long, Long>, Prietenie> {
    private String url;
    private String username;
    private String password;
    private Validator<Prietenie> validator;

    /**
     * @param url       url-ul cu care se conecteaza la baza de date
     * @param username  - usernameul
     * @param password  - parola
     * @param validator - validator pentru prietenie
     */
    public FriendshipDbRepository(String url, String username, String password, Validator<Prietenie> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Prietenie findOne(Tuple<Long, Long> longLongTuple) {
        String sql = "select * from friendship where (id1 = ? and id2 = ?) or (id1 = ? and id2 = ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, longLongTuple.getLeft().intValue());
            ps.setInt(2, longLongTuple.getRight().intValue());
            ps.setInt(3, longLongTuple.getRight().intValue());
            ps.setInt(4, longLongTuple.getLeft().intValue());
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                Long id1 = result.getLong("id1");
                Long id2 = result.getLong("id2");
                Date date = result.getDate("dateofaccept");
                int status = result.getInt("status");
                Prietenie prietenie = new Prietenie();
                Tuple<Long, Long> tuple = new Tuple(id1, id2);
                prietenie.setId(tuple);
                prietenie.setDate(date);
                prietenie.setStatus(status);
                return prietenie;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public List<Prietenie> findAll() {
        Set<Prietenie> friends = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendship");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                Date date = resultSet.getDate("dateofaccept");
                int status = resultSet.getInt("status");
                Prietenie prietenie = new Prietenie();
                Tuple<Long, Long> tuple = new Tuple(id1, id2);
                prietenie.setId(tuple);
                prietenie.setDate(date);
                prietenie.setStatus(status);
                friends.add(prietenie);
            }
            return friends.stream().toList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Prietenie save(Prietenie entity) throws ValidationException {
        validator.validate(entity);

        String sql = "insert into friendship (id1, id2, dateofaccept, status) values (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, Math.toIntExact(entity.getId().getLeft()));
            ps.setInt(2, Math.toIntExact(entity.getId().getRight()));
            ps.setDate(3, entity.getDate());
            ps.setInt(4, entity.getStatus());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Prietenie delete(Tuple<Long, Long> id) {

        String sql = "delete from friendship where (id1 = ? and id2 = ?) or (id1 = ? and id2 = ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id.getLeft().intValue());
            ps.setInt(2, id.getRight().intValue());
            ps.setInt(3, id.getRight().intValue());
            ps.setInt(4, id.getLeft().intValue());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Prietenie update(Prietenie entity) {
        String sql = "update friendship set status = ?, dateofaccept = ? where (id1 = ? and id2 = ?) or (id1 = ? and id2 = ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, entity.getStatus());
            ps.setDate(2, entity.getDate());
            ps.setInt(3, entity.getId().getLeft().intValue());
            ps.setInt(4, entity.getId().getRight().intValue());
            ps.setInt(5, entity.getId().getRight().intValue());
            ps.setInt(6, entity.getId().getLeft().intValue());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Page<Prietenie> findAll(Pageable pageable) {

        String sql = "SELECT * FROM (SELECT *, ROW_NUMBER() over (ORDER BY id ASC) AS NoOfRows FROM users) AS Unused WHERE NoOfRows >= ? AND NoOfRows < ?";
        Set<Prietenie> friends = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("select * from friendship")) {

            ps.setInt(1, pageable.getPageNumber() * pageable.getPageSize() + 1);
            ps.setInt(2, (pageable.getPageNumber() + 1) * pageable.getPageSize() + 1);

            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                Date date = resultSet.getDate("dateofaccept");
                int status = resultSet.getInt("status");
                Prietenie prietenie = new Prietenie();
                Tuple<Long, Long> tuple = new Tuple(id1, id2);
                prietenie.setId(tuple);
                prietenie.setDate(date);
                prietenie.setStatus(status);
                friends.add(prietenie);
            }
            Paginator<Prietenie> paginator = new Paginator<>(pageable, friends.stream().toList());
            return paginator.paginate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

