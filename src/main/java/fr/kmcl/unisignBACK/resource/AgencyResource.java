package fr.kmcl.unisignBACK.resource;

import fr.kmcl.unisignBACK.exception.model.AgencyLabelExistException;
import fr.kmcl.unisignBACK.exception.model.AgencyNotFoundException;
import fr.kmcl.unisignBACK.model.Agency;
import fr.kmcl.unisignBACK.model.HttpResponse;
import fr.kmcl.unisignBACK.service.AgencyService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static fr.kmcl.unisignBACK.constant.AgencyImplConstant.AGENCY_DELETED_SUCCESSFULLY;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
@RestController
@RequestMapping(path = {"/api/v1/agency"})
@AllArgsConstructor
public class AgencyResource {

    private final AgencyService agencyService;

    @GetMapping(path = "/find/{agencyId}")
    public ResponseEntity<Agency> getAgency(@PathVariable("agencyId") String agencyId) {
        Agency agency = agencyService.findAgencyByAgencyId(agencyId);
        return new ResponseEntity<>(agency, OK);
    }

    @GetMapping(path = "/find/user/{userId}")
    public ResponseEntity<List<Agency>> getAgencyByUserId(@PathVariable("userId") String userId) {
        List<Agency> agencies = agencyService.findByAppUserId(userId);
        return new ResponseEntity<>(agencies, OK);
    }

    /**
     * Fetch all agencies
     * @return ResponseEntity<List<Agency>>: The list of all KMCL agencies and a httpStatus
     *
    **/
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<List<Agency>> getAllAgencies() {
        List<Agency> agencies = agencyService.getAgencies();
        return new ResponseEntity<>(agencies, OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Agency> addNewAgency(@RequestParam("label") String label,
                                               @RequestParam("address") String address,
                                               @RequestParam("addressComplement") String addressComplement,
                                               @RequestParam("zipCode") String zipCode,
                                               @RequestParam("city") String city,
                                               @RequestParam("userName") String userName) throws AgencyNotFoundException, AgencyLabelExistException {
        Agency newAgency = agencyService.addNewAgency(label, address, addressComplement, zipCode, city, userName);
        return new ResponseEntity<>(newAgency, OK);
    }

    /**
     * Update a signature in the database
     * @param currentLabel String: current signature label
     * @param label String: new signature label
     * @param label String: address
     * @return ResponseEntity<Signature>: a response entity with the new signature and the OK http status
     * @throws AgencyLabelExistException : AgencyLabelExistException exception can be thrown
     * @throws AgencyNotFoundException : AgencyNotFoundException exception can be thrown
     */
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('signature:update')")
    public ResponseEntity<Agency> updateSignatureSettings(@RequestParam("currentLabel") String currentLabel,
                                                         @RequestParam("label") String label,
                                                         @RequestParam("address") String address,
                                                         @RequestParam("addressComplement") String addressComplement,
                                                         @RequestParam("zipCode") String zipCode,
                                                         @RequestParam("city") String city)
            throws AgencyLabelExistException, AgencyNotFoundException {
        Agency updatedAgency = agencyService.updateAgency(currentLabel, label, address, addressComplement, zipCode, city);
        return new ResponseEntity<>(updatedAgency, OK);
    }

    /**
     * Delete an agency if we have according authority
     * @param agencyId String: agency's username
     * @return ResponseEntity<HttpResponse>: httpStatus and message if found
     */
    @DeleteMapping("/delete/{agencyId}")
    @PreAuthorize("hasAuthority('agency:delete')")
    public ResponseEntity<HttpResponse> deleteSignature(@PathVariable("agencyId") String agencyId) throws IOException {
        agencyService.deleteAgency(agencyId);
        return response(OK, AGENCY_DELETED_SUCCESSFULLY);
    }

    /**
     * Custom method to return a response entity if another method doesn't return anything initially
     * @param httpStatus HttpStatus: httpStatus
     * @param message String: message to build a response entity
     * @return ResponseEntity<HttpResponse>: custom response entity
     */
    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase(), message), httpStatus);
    }
}
