package fr.kmcl.unisignBACK.exception.model;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
public class UsernameExistException extends Exception {
    public UsernameExistException(String message) {
        super(message);
    }
}
