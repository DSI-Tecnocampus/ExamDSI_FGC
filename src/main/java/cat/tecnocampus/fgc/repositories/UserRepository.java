package cat.tecnocampus.fgc.repositories;

import cat.tecnocampus.fgc.domain.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int[] saveUsers(List<User> users) {
        return jdbcTemplate.batchUpdate("INSERT INTO user(username, name, second_name, email) values(?, ?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        User user = users.get(i);
                        preparedStatement.setString(1, user.getUsername());
                        preparedStatement.setString(2, user.getName());
                        preparedStatement.setString(3, user.getSecondName());
                        preparedStatement.setString(4, user.getEmail());
                    }

                    @Override
                    public int getBatchSize() {
                        return users.size();
                    }
                });
    }

    public User findOneUser(String username) {
        return jdbcTemplate.queryForObject("select * from user where username = ?", new UserMapper(), username);
    }

    public boolean existsUser(String username) {
        try {
            Integer cnt = jdbcTemplate.queryForObject("SELECT count(*) FROM user WHERE username = ?", Integer.class, username);
            return cnt > 0;
        }
        catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public List<User> getUsers() {
        return jdbcTemplate.query("SELECT * FROM USER", new UserMapper());
    }

    private final class UserMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet resultSet, int i) throws SQLException {
            User user = new User();

            user.setUsername(resultSet.getString("username"));
            user.setName(resultSet.getString("name"));
            user.setSecondName(resultSet.getString("second_name"));
            user.setEmail(resultSet.getString("email"));

            return user;
        }
    }
}
