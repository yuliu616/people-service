package com.yu.controller;

import com.yu.model.Permission;
import com.yu.model.config.RolePermissionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EnableConfigurationProperties(RolePermissionConfig.class)
@Component
public class PermissionController {

    @Autowired
    private RolePermissionConfig rolePermissionConfig;

    private static final Logger logger = LoggerFactory.getLogger(PermissionController.class);

    @PostConstruct
    public void init(){
        for (String role : rolePermissionConfig.getPermissionMap().keySet()) {
            for (Permission p : rolePermissionConfig.getPermissionMap().get(role)) {
                logger.info("- role=[{}] hasPermission:[{}]", role, p);
            }
        }
    }

    /**
     * for the provided role list, build a list of all permissions that for those roles.
     * in other words, if user A have those roles, what permissions should he have.
     */
    public List<Permission> getPermissionListForRole(String[] roles) {
        Set<Permission> concluded = new HashSet<>();
        for (String role : roles) {
            List<Permission> list = rolePermissionConfig.getPermissionMap().get(role);
            concluded.addAll(list);
        }
        return new ArrayList<>(concluded);
    }

}
