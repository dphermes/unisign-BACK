package fr.kmcl.unisignBACK.service.impl;

import fr.kmcl.unisignBACK.exception.model.*;
import fr.kmcl.unisignBACK.model.Agency;
import fr.kmcl.unisignBACK.model.AppUser;
import fr.kmcl.unisignBACK.model.Signature;
import fr.kmcl.unisignBACK.model.SignatureVersion;
import fr.kmcl.unisignBACK.repo.AgencyRepo;
import fr.kmcl.unisignBACK.repo.SignatureRepo;
import fr.kmcl.unisignBACK.repo.SignatureVersionRepo;
import fr.kmcl.unisignBACK.repo.UserRepo;
import fr.kmcl.unisignBACK.service.SignatureService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

import static fr.kmcl.unisignBACK.constant.SignatureImplConstant.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

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
    private final AgencyRepo agencyRepo;
    private final UserRepo userRepo;
    private final SignatureVersionRepo signatureVersionRepo;

    @Override
    public Signature findSignatureById(Long id) {
        return signatureRepo.findSignatureById(id);
    }

    @Override
    public Signature findSignatureBySignatureId(String signatureId) {
        return signatureRepo.findSignatureBySignatureId(signatureId);
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
    public Signature addNewSignature(String label, String createdByUser, boolean isActive, String applyToAgenciesId) throws SignatureLabelExistException, SignatureNotFoundException {
        validateSignatureLabel(EMPTY, label);
        Signature signature = new Signature();
        signature.setSignatureId(generateSignatureId());
        signature.setLabel(label);
        signature.setCreationDate(new Date());
        AppUser user = userRepo.findAppUserByUsername(createdByUser);
        signature.setCreatedByUser(user);
        signature.setActive(isActive);
        signature.setStatus("inactive");
        // @TODO : Check if this method saves correctly the agencies in DB
        List<String> agenciesToFind = new ArrayList<String>(Arrays.asList(applyToAgenciesId.split(",")));
        List<Agency> foundAgencies = new ArrayList<>();
        for (String agency : agenciesToFind) {
            Agency foundAgency = agencyRepo.findAgencyByLabel(agency);
            foundAgencies.add(foundAgency);
        }
        Set<Agency> convertedAgencies = new HashSet<>(foundAgencies);
        signature.setApplyToAgencies(convertedAgencies);
        signatureRepo.save(signature);
        SignatureVersion createVersion = new SignatureVersion();
        createVersion.setSignatureVersionId(generateSignatureId());
        createVersion.setCreationDate(new Date());
        createVersion.setCreatedByUser(user);
        createVersion.setParentSignature(signature);
        createVersion.setHtmlSignature(DEFAULT_HTML_SIGNATURE);
        if (user.getRole() == "ROLE_MANAGER" || user.getRole() == "ROLE_ADMIN" || user.getRole() == "ROLE_SUPER_ADMIN") {
            createVersion.setValidatedByManager(true);
        } else {
            createVersion.setValidatedByManager(false);
        }
        signatureVersionRepo.save(createVersion);
        signature.addSignatureVersion(createVersion);
        signatureRepo.save(signature);
        return signature;
    }

    @Override
    public Signature updateSignatureSettings(String currentLabel, String newLabel, String updatedByUser, boolean isActive, String htmlSignature, List<Agency> agencies) throws SignatureLabelExistException, SignatureNotFoundException {
        Signature currentSignature = validateSignatureLabel(currentLabel, newLabel);
        assert currentSignature != null;
        currentSignature.setLabel(newLabel);
        currentSignature.setActive(isActive);
        AppUser user = userRepo.findAppUserByUsername(updatedByUser);
        currentSignature.setLastModificationDate(new Date());
        currentSignature.setLastModificationDateDisplay(new Date());
        // @TODO : Check if this method saves correctly the agencies in DB
        for (Agency agency : agencies) {
            Agency foundAgency = agencyRepo.findAgencyByLabel(agency.getLabel());
            currentSignature.setApplyToAgency(foundAgency);
        }
        signatureRepo.save(currentSignature);
        return currentSignature;
    }

    @Override
    public void deleteSignature(String signatureId) throws IOException {
        Signature signature = signatureRepo.findSignatureBySignatureId(signatureId);
        signatureRepo.deleteById(signature.getId());
    }

    private Signature validateSignatureLabel(String currentLabel, String newLabel) throws SignatureNotFoundException, SignatureLabelExistException {
        Signature signatureByNewLabel = findSignatureByLabel(newLabel);
        if(StringUtils.isNotBlank(currentLabel)) {
            Signature currentSignature = findSignatureByLabel(currentLabel);
            if(currentSignature == null) {
                throw new SignatureNotFoundException(NO_SIGNATURE_FOUND_WITH_LABEL + currentLabel);
            }
            if(signatureByNewLabel != null && !currentSignature.getId().equals(signatureByNewLabel.getId())) {
                throw new SignatureLabelExistException(SIGNATURE_LABEL_ALREADY_EXISTS);
            }
            return currentSignature;
        } else {
            if(signatureByNewLabel != null) {
                throw new SignatureLabelExistException(SIGNATURE_LABEL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    /**
     * Generates a random signature id
     * @return String: generated signature id
     */
    private String generateSignatureId() {
        return RandomStringUtils.randomNumeric(5);
    }
}
