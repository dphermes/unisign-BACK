package fr.kmcl.unisignBACK.constant;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
public class SecurityConstant {
    public static final long EXPIRATION_TIME = 5 * 24 * 60 * 60 * 1000; // 5 days in milliseconds
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String KMCL_SAS = "Konica Minolta Centre Loire, SAS";
    public static final String KMCL_ADMINISTRATION = "Signature management portal";
    public static final String AUTHORITIES = "Authorities";
    public static final String FORBIDDEN_MESSAGE = "You Shall Not Pass! You need to log in to access it.";
    public static final String ACCESS_DENIED_MESSAGE = "You Shall Not Pass! You do not have permission.";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String[] PUBLIC_URLS = {
            "/api/v1/user/login",
            "/api/v1/user/register",
            "/api/v1/user/image/**",
            "/user/image/**",
            "/api/v1/token/refresh/**"
    };
}
