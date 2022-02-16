package fr.kmcl.unisignBACK.service;

import fr.kmcl.unisignBACK.model.Signature;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
public interface SignatureService {
    Signature findSignatureById(Long id);
}
