package fr.kmcl.unisignBACK.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.io.Serializable;
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
@Table(name = "agency")
public class Agency implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;
    private String agencyId;
    private String label;
    private String address;
    private String addressComplement;
    private String zipCode;
    private String city;
    private Date creationDate;
    @ManyToMany(fetch = FetchType.LAZY,
                cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "signature_agency",
            joinColumns = { @JoinColumn(name = "signature_id") },
            inverseJoinColumns = { @JoinColumn(name = "agency_id") })
    @JsonIgnore
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Set<Signature> signatures = new HashSet<>();
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "agency_created_by_user_id",referencedColumnName="id")
    private AppUser createdByUser;
    @OneToMany(mappedBy = "agency", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<AppUser> employees = new HashSet<>();
}
