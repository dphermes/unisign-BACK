package fr.kmcl.unisignBACK.security;

public enum AppUserPermission {
    USER_READ("user:read"),
    USER_WRITE("user:write"),
    SIGNATURE_READ("manager:read"),
    SIGNATURE_WRITE("manager:write");

    private final String permission;

    AppUserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
