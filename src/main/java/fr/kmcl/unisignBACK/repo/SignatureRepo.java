package fr.kmcl.unisignBACK.repo;

import fr.kmcl.unisignBACK.model.Signature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
public interface SignatureRepo extends JpaRepository<Signature, Long> {
    Signature findSignatureById(Long id);
    Signature findSignatureBySignatureId(String signatureId);
    Signature findSignatureByLabel(String label);
    Signature findSignatureByCreatedByUser(Long id);
    List<Signature> findAll();
}
