package fr.kmcl.unisignBACK.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class Signature {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;
    private String signatureId;
    private String label;
    private Date lastModificationDate;
    private Date lastModificationDateDisplay;
    private Date creationDate;
    @ManyToOne(optional = false)
    @JoinColumn(name="signature_created_by_user_id")
    private AppUser createdByUser;
    @ManyToOne(optional = true)
    @JoinColumn(name="signature_modified_by_user_id")
    private AppUser modifiedByUser;
    private boolean isActive;
    private String status;
    @ManyToMany(targetEntity = Agency.class,
            fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "signature_agency",
            joinColumns = { @JoinColumn(name = "signature_id") },
            inverseJoinColumns = { @JoinColumn(name = "agency_id") })
    private Set<Agency> applyToAgencies = new HashSet<>();
    @OneToMany(targetEntity = SignatureVersion.class, cascade = CascadeType.ALL)
    @JoinTable(
            name="signature_signature_versions",
            joinColumns = @JoinColumn(name = "signature_id"),
            inverseJoinColumns = @JoinColumn(name="signature_versions_id")
    )
    private Set<SignatureVersion> signatureVersions;
    @OneToOne
    private SignatureVersion activeSignatureVersion;

    public void addSignatureVersion(SignatureVersion signatureVersion) {
        this.signatureVersions = signatureVersions;
    }

    public void setApplyToAgency(Agency foundAgency) {
    }
}
