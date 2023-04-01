package com.logic.account.utils;

import static com.logic.account.utils.MyUtils.jsonify;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import org.springframework.data.annotation.Immutable;

import lombok.*;

@Entity
@Getter @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(staticName = "of")
@Immutable
public class Currency {
    @Id @Column(updatable = false, nullable = false, columnDefinition = "smallint")
    @EqualsAndHashCode.Include
        @NonNull private int code;
    @Column(nullable = false, unique = true)
        @NonNull private String letterCode;
    @Column(nullable = false)
        @NonNull private BigDecimal rate;

    @Override 
    public String toString() {
        return jsonify(this);
    }
}
