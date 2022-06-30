package fr.kmcl.unisignBACK.resource;

import fr.kmcl.unisignBACK.exception.model.SignatureLabelExistException;
import fr.kmcl.unisignBACK.exception.model.SignatureNotFoundException;
import fr.kmcl.unisignBACK.model.HttpResponse;
import fr.kmcl.unisignBACK.model.Signature;
import fr.kmcl.unisignBACK.model.SignatureVersion;
import fr.kmcl.unisignBACK.service.SignatureService;
import fr.kmcl.unisignBACK.service.SignatureVersionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static fr.kmcl.unisignBACK.constant.SignatureImplConstant.SIGNATURE_DELETED_SUCCESSFULLY;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
@RestController
@RequestMapping(path = {"/api/v1/signature-version"})
@AllArgsConstructor
public class SignatureVersionResource {

    private final SignatureVersionService signatureVersionService;

    @GetMapping(path = "/find/{signatureVersionId}")
    public ResponseEntity<SignatureVersion> getSignatureVersion(@PathVariable("signatureVersionId") String signatureVersionId) {
        SignatureVersion signature = signatureVersionService.findSignatureVersionBySignatureVersionId(signatureVersionId);
        return new ResponseEntity<>(signature, OK);
    }

    @GetMapping(path = "/find/signature/{parentSignatureId}")
    public ResponseEntity<List<SignatureVersion>> getSignatureVersionByParentSignatureId(@PathVariable("parentSignatureId") String parentSignatureId) {
        List<SignatureVersion> signatureVersions = signatureVersionService.findSignatureVersionsByParentSignatureSignatureId(parentSignatureId);
        return new ResponseEntity<>(signatureVersions, OK);
    }

    /**
     * Fetch all signatures
     * @return ResponseEntity<List<Signature>>: The list of all signatures and a httpStatus
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<List<SignatureVersion>> getAllSignatureVersions() {
        List<SignatureVersion> signatureVersions = signatureVersionService.getSignatureVersions();
        return new ResponseEntity<>(signatureVersions, OK);
    }

    /**
     * Delete a signature version if we have according authority
     * @param signatureVersionId String: signature version's id
     * @return ResponseEntity<HttpResponse>: httpStatus and message if found
     */
    @DeleteMapping("/delete/{signatureVersionId}")
    @PreAuthorize("hasAuthority('signature:delete')")
    public ResponseEntity<HttpResponse> deleteSignatureVersion(@PathVariable("signatureVersionId") String signatureVersionId) throws IOException {
        signatureVersionService.deleteSignatureVersion(signatureVersionId);
        return response(OK, SIGNATURE_DELETED_SUCCESSFULLY);
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
