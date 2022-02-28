package fr.kmcl.unisignBACK.service.impl;

import fr.kmcl.unisignBACK.model.Signature;
import fr.kmcl.unisignBACK.repo.SignatureRepo;
import fr.kmcl.unisignBACK.service.SignatureService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<Signature> getSignatures() {
        return signatureRepo.findAll();
    }
}
