package fr.kmcl.unisignBACK.service.impl;

import fr.kmcl.unisignBACK.model.AppUser;
import fr.kmcl.unisignBACK.model.Signature;
import fr.kmcl.unisignBACK.repo.SignatureRepo;
import fr.kmcl.unisignBACK.service.SignatureService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
@Service
@Transactional
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SignatureServiceImpl implements SignatureService {
    private final SignatureRepo signatureRepo;

    @Override
    public Signature findSignatureById(Long id) {
        return signatureRepo.findSignatureById(id);
    }

    @Override
    public Signature findSignatureByLabel(String label) {
        return signatureRepo.findSignatureByLabel(label);
    }

    @Override
    public Signature findSignatureByCreatedByUser(Long id) {
        return signatureRepo.findSignatureByCreatedByUser(id);
    }

    @Override
    public List<Signature> getSignatures() {
        return signatureRepo.findAll();
    }

    @Override
    public Signature addNewSignature(String label, AppUser createdByUser, boolean isActive) {
        Signature signature = new Signature();
        signature.setSignatureId(generateSignatureId());
        signature.setLabel(label);
        signature.setCreationDate(new Date());
        signature.setCreatedByUser(createdByUser);
        signature.setActive(isActive);
        signatureRepo.save(signature);
        return signature;
    }

    @Override
    public Signature updateSignatureSettings(String currentLabel, String newLabel, boolean isActive) {
        Signature currentSignature = findSignatureByLabel(currentLabel);
        currentSignature.setLabel(newLabel);
        currentSignature.setActive(isActive);
        currentSignature.setLastModificationDate(new Date());
        currentSignature.setLastModificationDateDisplay(new Date());
        signatureRepo.save(currentSignature);
        return currentSignature;
    }

    /**
     * Generates a random signature id
     * @return String: generated signature id
     */
    private String generateSignatureId() {
        return RandomStringUtils.randomNumeric(5);
    }
}
