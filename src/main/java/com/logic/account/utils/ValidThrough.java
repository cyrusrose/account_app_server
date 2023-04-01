package com.logic.account.utils;

import static com.logic.account.utils.MyUtils.jsonify;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import org.springframework.data.annotation.Immutable;

import lombok.*;

@Embeddable
@Immutable
@Getter
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ValidThrough {
    @Column(nullable = false, columnDefinition = "smallint")
        private int month;
    @Column(nullable = false, columnDefinition = "smallint")
        private int year;
    
    @Override 
    public String toString() {
        return jsonify(this);
    }
}
