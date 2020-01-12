package com.sm.dao.dao;

import com.sm.dao.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-07 23:08
 */
@Component
public class UserRoleMapDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Role> getRolesById(int userid){
        String sql = "select role_name from roles left join user_roles on roles.id = user_roles.roles_id where user_roles.user_id = ?";

        List<String> roleNames = jdbcTemplate.queryForList(sql, new Object[]{userid}, String.class);
        return roleNames.stream().map(n -> new Role(n)).collect(Collectors.toList());
    }

    public void createDefaultRole(int userId) {
        String sql = "insert into user_roles values(?, 1)";
        jdbcTemplate.update(sql, new Object[]{userId});
    }
}