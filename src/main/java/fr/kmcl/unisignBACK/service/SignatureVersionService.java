package fr.kmcl.unisignBACK.service;

import fr.kmcl.unisignBACK.model.SignatureVersion;

import java.io.IOException;
import java.util.List;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
public interface SignatureVersionService {
    SignatureVersion findSignatureVersionById(Long id);
    SignatureVersion findSignatureVersionBySignatureVersionId(String signatureVersionId);
    List<SignatureVersion> getSignatureVersions();
    List<SignatureVersion> findSignatureVersionsByParentSignatureSignatureId(String parentSignatureId);
    void deleteSignatureVersion(String signatureVersionId) throws IOException;
}
