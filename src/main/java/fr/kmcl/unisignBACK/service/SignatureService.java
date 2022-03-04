package fr.kmcl.unisignBACK.service;

import fr.kmcl.unisignBACK.exception.model.SignatureLabelExistException;
import fr.kmcl.unisignBACK.exception.model.SignatureNotFoundException;
import fr.kmcl.unisignBACK.model.AppUser;
import fr.kmcl.unisignBACK.model.Signature;

import java.io.IOException;
import java.util.List;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
public interface SignatureService {
    Signature findSignatureById(Long id);
    Signature findSignatureBySignatureId(String signatureId);
    Signature findSignatureByLabel(String label);
    Signature findSignatureByCreatedByUser(Long id);
    List<Signature> getSignatures();
    Signature addNewSignature(String label, String createdByUser, boolean isActive) throws SignatureLabelExistException, SignatureNotFoundException;
    Signature updateSignatureSettings(String currentLabel, String label, String updatedByUser,boolean isActive, String htmlSignature) throws SignatureLabelExistException, SignatureNotFoundException;
    void deleteSignature(String signatureId) throws IOException;
}
