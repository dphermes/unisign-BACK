package fr.kmcl.unisignBACK.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
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
public class AppUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;
    private String userId;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String profileImageUrl;
    @JsonBackReference
    @ManyToOne(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="agency_id")
    private Agency agency;
    @JsonIgnore
    @OneToMany(mappedBy = "recipient")
    private Set<Notification> notifications;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private Date joinDate;
    private String role;
    private String[] authorities;
    private boolean isActive;
    private boolean isNotLocked;

    public void enrollEmployee(Agency agency) {
        this.agency = agency;
    }
}

