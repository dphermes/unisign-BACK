package fr.kmcl.unisignBACK.resource;

import fr.kmcl.unisignBACK.exception.model.EmailExistException;
import fr.kmcl.unisignBACK.exception.model.NotImageFileException;
import fr.kmcl.unisignBACK.exception.model.UserNotFoundException;
import fr.kmcl.unisignBACK.exception.model.UsernameExistException;
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
    public ResponseEntity<Signature> getSignature(@PathVariable("signatureId") Long id) {
        Signature signature = signatureService.findSignatureById(id);
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

    /**
     * Update a signature in the database
     * @param currentLabel String: new signature label
     * @param isActive boolean: new signature active status
     * @return ResponseEntity<Signature>: a response entity with the new signature and the OK http status
     * @throws UserNotFoundException : UserNotFoundException exception can be thrown
     * @throws EmailExistException : EmailExistException exception can be thrown
     * @throws IOException : IOException exception can be thrown exception can be thrown
     * @throws UsernameExistException : UsernameExistException exception can be thrown
     */
    @PostMapping("/update")
    public ResponseEntity<Signature> updateSignatureSettings(@RequestParam("currentLabel") String currentLabel,
                                                             @RequestParam("label") String label,
                                                             @RequestParam("isActive") String isActive)
            throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, NotImageFileException {
        Signature updatedSignature = signatureService.updateSignatureSettings(currentLabel, label, Boolean.parseBoolean(isActive));
        return new ResponseEntity<>(updatedSignature, OK);
    }
}
