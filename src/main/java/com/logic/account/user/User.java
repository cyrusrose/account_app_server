package com.logic.account.user;

import static com.logic.account.utils.MyUtils.jsonify;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.*;

@Entity
@Table(name = "app_user")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded =  true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class, 
    property = "id"
)
public class User {
    @Id @GeneratedValue @Column(updatable = false)
    @EqualsAndHashCode.Include
        private UUID id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
        @NonNull private Client client;
    @Column(nullable = false)
        @NonNull private String login;
    @Column(nullable = false)
        @NonNull private String password;

    public User(@NonNull Client client, @NonNull String login, @NonNull String password) {
        this.client = client;
        this.login = login;
        this.password = password;
    }

    @Override 
    public String toString() {
        return jsonify(this);
    }
}
