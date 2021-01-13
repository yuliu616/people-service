package com.yu.model.config;

import com.yu.model.Permission;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties("people-service.role-permission-config")
public class RolePermissionConfig {

    private Map<String, List<Permission>> permissionMap;

    public Map<String, List<Permission>> getPermissionMap() {
        return permissionMap;
    }

    public void setPermissionMap(Map<String, List<Permission>> permissionMap) {
        this.permissionMap = permissionMap;
    }

}
