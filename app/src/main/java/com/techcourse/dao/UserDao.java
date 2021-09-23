package com.techcourse.dao;

import com.techcourse.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDao.class);

    private final DataSource dataSource;

    public UserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insert(User user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource) {
            @Override
            String createQuery() {
                return "insert into users (account, password, email) values (?, ?, ?)";
            }

            @Override
            void setValues(PreparedStatement pstmt, Object... values) throws SQLException {
                int cnt = 1;
                for (Object value : values) {
                    pstmt.setObject(cnt++, value);
                }
            }
        };
        jdbcTemplate.execute(user.getAccount(), user.getPassword(), user.getEmail());
    }

    public void update(User user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource) {
            @Override
            String createQuery() {
                return "update users set account = ?, password = ?, email = ? where id = ?";
            }

            @Override
            void setValues(PreparedStatement pstmt, Object... values) throws SQLException {
                int cnt = 1;
                for (Object value : values) {
                    pstmt.setObject(cnt++, value);
                }
            }
        };
        jdbcTemplate.execute(user.getAccount(), user.getPassword(), user.getEmail(), user.getId());
    }

    public List<User> findAll() {
        SelectJdbcTemplate selectJdbcTemplate = new SelectJdbcTemplate(dataSource) {
            @Override
            void setParams(PreparedStatement pstmt) {
            }

            @Override
            Object mapFromRow(ResultSet rs) throws SQLException {
                List<User> users = new ArrayList<>();
                while (rs.next()) {
                    users.add(new User(
                            rs.getLong(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4)));
                }
                return users;
            }
        };
        final String sql = "select * from users";
        return (List<User>) selectJdbcTemplate.query(sql);
    }

    public User findById(Long id) {
        SelectJdbcTemplate selectJdbcTemplate = new SelectJdbcTemplate(dataSource) {
            @Override
            void setParams(PreparedStatement pstmt) throws SQLException {
                pstmt.setLong(1, id);
            }

            @Override
            Object mapFromRow(ResultSet rs) throws SQLException {
                return new User(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4));
            }
        };
        final String sql = "select * from users where id = ?";
        return (User) selectJdbcTemplate.query(sql);
    }

    public User findByAccount(String account) {
        SelectJdbcTemplate selectJdbcTemplate = new SelectJdbcTemplate(dataSource) {
            @Override
            void setParams(PreparedStatement pstmt) throws SQLException {
                pstmt.setString(1, account);
            }

            @Override
            Object mapFromRow(ResultSet rs) throws SQLException {
                return new User(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4));
            }
        };
        final String sql = "select * from users where account = ?";
        return (User) selectJdbcTemplate.query(sql);
    }

    public void clear() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource) {
            @Override
            String createQuery() {
                return "drop table users";
            }

            @Override
            void setValues(PreparedStatement pstmt, Object... values) {
            }
        };
        jdbcTemplate.execute();
    }
}
