package fr.kmcl.unisignBACK.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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
public class CustomEventMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;
    private String customEventMessageId;
    private String label;
    private String message;
    private Date creationDate;
    private Date lastModificationDate;
    private Date lastModificationDateDisplay;
    @ManyToOne
    @JoinColumn(name = "created_by_user_id")
    private AppUser createdByUser;
    @ManyToOne
    @JoinColumn(name = "last_modified_by_user_id")
    private AppUser lastModifiedByUser;
    @ManyToMany
    private List<Signature> signaturesTarget;
    @ManyToMany
    private List<Agency> agenciesTarget;
}
