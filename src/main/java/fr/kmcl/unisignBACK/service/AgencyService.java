package fr.kmcl.unisignBACK.service;

import fr.kmcl.unisignBACK.exception.model.AgencyLabelExistException;
import fr.kmcl.unisignBACK.exception.model.AgencyNotFoundException;
import fr.kmcl.unisignBACK.exception.model.SignatureLabelExistException;
import fr.kmcl.unisignBACK.exception.model.SignatureNotFoundException;
import fr.kmcl.unisignBACK.model.Agency;
import fr.kmcl.unisignBACK.model.AppUser;
import fr.kmcl.unisignBACK.model.Signature;

import java.io.IOException;
import java.util.List;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
public interface AgencyService {
    Agency findAgencyById(Long id);
    Agency findAgencyByAgencyId(String agencyId);
    Agency findAgencyByLabel(String label);
    List<Agency> getAgencies();
    Agency addNewAgency(String label, String address, String addressComplement, String zipCode, String city, String createdByUser) throws AgencyLabelExistException, AgencyNotFoundException;
    Agency updateAgency(String currentLabel, String label, String address, String addressComplement, String zipCode, String city) throws AgencyLabelExistException, AgencyNotFoundException;
    void deleteAgency(String signatureId) throws IOException;
    List<Agency> findByAppUserId(String userId);
}
