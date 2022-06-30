package fr.kmcl.unisignBACK.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * @author KMCL (https://www.kmcl.fr)
 * @version 1.0
 * @since 01/10/2022
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignatureVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;
    private String signatureVersionId;
    private Date creationDate;
    @ManyToOne
    @JoinColumn(name = "signature_version_created_by_user_id")
    private AppUser createdByUser;
    @JsonIgnore
    @ManyToOne
    private Signature parentSignature;
    private String htmlSignature;
    private boolean isValidatedByManager;
}
