package fr.kmcl.unisignBACK.service.impl;

import fr.kmcl.unisignBACK.exception.model.AgencyLabelExistException;
import fr.kmcl.unisignBACK.exception.model.AgencyNotFoundException;
import fr.kmcl.unisignBACK.model.Agency;
import fr.kmcl.unisignBACK.model.AppUser;
import fr.kmcl.unisignBACK.repo.AgencyRepo;
import fr.kmcl.unisignBACK.repo.UserRepo;
import fr.kmcl.unisignBACK.service.AgencyService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static fr.kmcl.unisignBACK.constant.AgencyImplConstant.AGENCY_LABEL_ALREADY_EXISTS;
import static fr.kmcl.unisignBACK.constant.AgencyImplConstant.NO_AGENCY_FOUND_WITH_LABEL;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
@Service
@Transactional
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class AgencyServiceImpl implements AgencyService {
    private final AgencyRepo agencyRepo;
    private final UserRepo userRepo;

    @Override
    public Agency findAgencyById(Long id) {
        return agencyRepo.findAgencyById(id);
    }

    @Override
    public Agency findAgencyByAgencyId(String signatureId) {
        return agencyRepo.findAgencyByAgencyId(signatureId);
    }

    @Override
    public Agency findAgencyByLabel(String label) {
        return agencyRepo.findAgencyByLabel(label);
    }


    @Override
    public List<Agency> getAgencies() {
        return agencyRepo.findAll();
    }

    @Override
    public Agency addNewAgency(String label, String address, String addressComplement, String zipCode, String city, String createdByUser) throws AgencyLabelExistException, AgencyNotFoundException {
        validateAgencyLabel(EMPTY, label);
        Agency agency = new Agency();
        agency.setAgencyId(generateAgencyId());
        agency.setLabel(label);
        agency.setAddress(address);
        agency.setAddressComplement(addressComplement);
        agency.setZipCode(zipCode);
        agency.setCity(city);
        agency.setCreationDate(new Date());
        AppUser user = userRepo.findAppUserByUsername(createdByUser);
        agencyRepo.save(agency);
        return agency;
    }

    @Override
    public Agency updateAgency(String currentLabel, String newLabel, String address, String addressComplement, String zipCode, String city) throws AgencyLabelExistException, AgencyNotFoundException {
        Agency currentAgency = validateAgencyLabel(currentLabel, newLabel);
        assert currentAgency != null;
        currentAgency.setLabel(newLabel);
        currentAgency.setAddress(address);
        if (addressComplement != null) {
            currentAgency.setAddressComplement(addressComplement);
        } else {
            currentAgency.setAddressComplement(null);
        }
        currentAgency.setZipCode(zipCode);
        currentAgency.setCity(city);
        agencyRepo.save(currentAgency);
        return currentAgency;
    }

    @Override
    public void deleteAgency(String agencyId) throws IOException {
        Agency agency = agencyRepo.findAgencyByAgencyId(agencyId);
        agencyRepo.deleteById(agency.getId());
    }

    @Override
    public List<Agency> findByAppUserId(String userId) {
        List<Agency> agencies = agencyRepo.findAgenciesByEmployeesUserId(userId);
        return agencies;
    }

    private Agency validateAgencyLabel(String currentLabel, String newLabel) throws AgencyLabelExistException, AgencyNotFoundException {
        Agency agencyByNewLabel = findAgencyByLabel(newLabel);
        if(StringUtils.isNotBlank(currentLabel)) {
            Agency currentAgency = findAgencyByLabel(currentLabel);
            if(currentAgency == null) {
                throw new AgencyNotFoundException(NO_AGENCY_FOUND_WITH_LABEL + currentLabel);
            }
            if(agencyByNewLabel != null && !currentAgency.getId().equals(agencyByNewLabel.getId())) {
                throw new AgencyLabelExistException(AGENCY_LABEL_ALREADY_EXISTS);
            }
            return currentAgency;
        } else {
            if(agencyByNewLabel != null) {
                throw new AgencyLabelExistException(AGENCY_LABEL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    /**
     * Generates a random signature id
     * @return String: generated signature id
     */
    private String generateAgencyId() {
        return RandomStringUtils.randomNumeric(5);
    }
}
