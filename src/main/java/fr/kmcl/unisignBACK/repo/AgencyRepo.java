package fr.kmcl.unisignBACK.repo;

import fr.kmcl.unisignBACK.model.Agency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
public interface AgencyRepo extends JpaRepository<Agency, Long> {
    Agency findAgencyById(Long id);
    Agency findAgencyByAgencyId(String agencyId);
    Agency findAgencyByLabel(String label);
    List<Agency> findAgenciesByEmployeesUserId(String userId);
    List<Agency> findAgenciesBySignaturesId(String userId);
    List<Agency> findAll();
}
