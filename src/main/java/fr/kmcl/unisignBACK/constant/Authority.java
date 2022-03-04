package fr.kmcl.unisignBACK.constant;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
public class Authority {
    public static final String[] USER_AUTHORITIES = { "user:read", "signature:read", "signature:create", "signature:update" };
    public static final String[] MANAGER_AUTHORITIES = { "user:read", "user:update", "signature:read", "signature:create", "signature:update" };
    public static final String[] ADMIN_AUTHORITIES = { "user:read", "user:update", "user:create", "signature:read", "signature:create", "signature:update" };
    public static final String[] SUPER_ADMIN_AUTHORITIES = { "user:read", "user:update", "user:create", "user:delete", "signature:read", "signature:create", "signature:update", "signature:delete" };
}
