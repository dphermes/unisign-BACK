package fr.kmcl.unisignBACK.service.impl;

import fr.kmcl.unisignBACK.model.SignatureVersion;
import fr.kmcl.unisignBACK.repo.SignatureVersionRepo;
import fr.kmcl.unisignBACK.service.SignatureVersionService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
@Service
@Transactional
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SignatureVersionServiceImpl implements SignatureVersionService {
    private final SignatureVersionRepo signatureVersionRepo;

    @Override
    public SignatureVersion findSignatureVersionById(Long id) {
        return signatureVersionRepo.findSignatureVersionById(id);
    }

    @Override
    public SignatureVersion findSignatureVersionBySignatureVersionId(String signatureVersionId) {
        return signatureVersionRepo.findSignatureVersionBySignatureVersionId(signatureVersionId);
    }

    @Override
    public List<SignatureVersion> getSignatureVersions() {
        return signatureVersionRepo.findAll();
    }

    @Override
    public List<SignatureVersion> findSignatureVersionsByParentSignatureSignatureId(String parentSignatureId) {
        return signatureVersionRepo.findSignatureVersionsByParentSignatureSignatureId(parentSignatureId);
    }

    @Override
    public void deleteSignatureVersion(String signatureVersionId) throws IOException {
        SignatureVersion signatureVersion = signatureVersionRepo.findSignatureVersionBySignatureVersionId(signatureVersionId);
        signatureVersionRepo.deleteById(signatureVersion.getId());
    }
}
