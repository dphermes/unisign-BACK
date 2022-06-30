package fr.kmcl.unisignBACK.repo;

import fr.kmcl.unisignBACK.model.Signature;
import fr.kmcl.unisignBACK.model.SignatureVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
public interface SignatureVersionRepo extends JpaRepository<SignatureVersion, Long> {
    SignatureVersion findSignatureVersionById(Long id);
    SignatureVersion findSignatureVersionBySignatureVersionId(String signatureId);
    List<SignatureVersion> findSignatureVersionsByParentSignatureSignatureId(String parentSignatureId);
    List<SignatureVersion> findAll();
}
