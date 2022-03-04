package fr.kmcl.unisignBACK.resource;

import fr.kmcl.unisignBACK.exception.model.*;
import fr.kmcl.unisignBACK.model.AppUser;
import fr.kmcl.unisignBACK.model.Signature;
import fr.kmcl.unisignBACK.service.SignatureService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
@RestController
@RequestMapping(path = {"/api/v1/signature"})
@AllArgsConstructor
public class SignatureResource {

    private final SignatureService signatureService;

    @GetMapping(path = "/find/{signatureId}")
    public ResponseEntity<Signature> getSignature(@PathVariable("signatureId") String signatureId) {
        Signature signature = signatureService.findSignatureBySignatureId(signatureId);
        return new ResponseEntity<>(signature, OK);
    }

    /**
     * Fetch all signatures
     * @return ResponseEntity<List<Signature>>: The list of all signatures and a httpStatus
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<List<Signature>> getAllSignatures() {
        List<Signature> signatures = signatureService.getSignatures();
        return new ResponseEntity<>(signatures, OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Signature> addNewSignature(@RequestParam("label") String label,
                                                     @RequestParam("userName") String userName,
                                                      @RequestParam("isActive") String isActive) throws SignatureLabelExistException, SignatureNotFoundException {
        Signature newSignature = signatureService.addNewSignature(label, userName, Boolean.parseBoolean(isActive));
        return new ResponseEntity<>(newSignature, OK);
    }

    /**
     * Update a signature in the database
     * @param currentLabel String: current signature label
     * @param label String: new signature label
     * @param isActive boolean: new signature active status
     * @return ResponseEntity<Signature>: a response entity with the new signature and the OK http status
     * @throws SignatureLabelExistException : SignatureLabelExistException exception can be thrown
     * @throws SignatureNotFoundException : SignatureNotFoundException exception can be thrown
     */
    @PostMapping("/update")
    public ResponseEntity<Signature> updateSignatureSettings(@RequestParam("currentLabel") String currentLabel,
                                                             @RequestParam("label") String label,
                                                             @RequestParam("userName") String userName,
                                                             @RequestParam("isActive") String isActive,
                                                             @RequestParam("htmlSignature") String htmlSignature)
            throws SignatureLabelExistException, SignatureNotFoundException {
        Signature updatedSignature = signatureService.updateSignatureSettings(currentLabel, label, userName, Boolean.parseBoolean(isActive), htmlSignature);
        return new ResponseEntity<>(updatedSignature, OK);
    }
}
