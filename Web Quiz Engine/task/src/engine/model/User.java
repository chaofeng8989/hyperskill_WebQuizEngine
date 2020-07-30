package engine.model;

import com.sun.source.tree.Tree;
import engine.securit.Authority;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.querydsl.QuerydslUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

@Data
@Entity
@ToString
@Embeddable
@Table (name = "user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;


    private String username;


    @NotEmpty @NotNull @Email
    @Pattern(regexp=".+@.*\\..*", message="Please provide a valid email address")

    @Column (unique = true, nullable = false)
    private String email;

    @NotEmpty @NotNull @Size(min = 5)
    private String password;

    @OneToMany
    @JoinColumn
    List<Question> questionList = new ArrayList<>();


    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"))
    private List<Authority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    // standard getters and setters

}