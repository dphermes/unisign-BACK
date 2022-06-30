package fr.kmcl.unisignBACK.exception.model;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
public class SignatureLabelExistException extends Exception {
    public SignatureLabelExistException(String message) {
        super(message);
    }
}
