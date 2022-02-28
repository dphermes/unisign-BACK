package fr.kmcl.unisignBACK.resource;

import fr.kmcl.unisignBACK.model.AppUser;
import fr.kmcl.unisignBACK.model.Signature;
import fr.kmcl.unisignBACK.service.SignatureService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
