package com.logic.account.transactions;

import static com.logic.account.utils.MyUtils.jsonify;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.logic.account.utils.Currency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Data
@Entity
@Getter @EqualsAndHashCode(onlyExplicitlyIncluded =  true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserTransaction {
    @Id @Column(updatable = false)
    @EqualsAndHashCode.Include
    @GeneratedValue
        private UUID id;
    private BigDecimal money;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
        private Currency currency;
    private String state;
    private String via;
    
    private String title;
    private String titleRu;
    
    private UUID senderId;
    private UUID receiverId;

    private UUID senderAccountId;
    private UUID receiverAccountId;
    
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    public UserTransaction(BigDecimal money, Currency currency, String state, String via, String title, String titleRu,
            UUID senderId, UUID receiverId, UUID senderAccountId, UUID receiverAccountId) {
        this.money = money;
        this.currency = currency;
        this.state = state;
        this.via = via;
        this.title = title;
        this.titleRu = titleRu;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;

        this.time = LocalDateTime.now();
    }

    @Override 
    public String toString() {
        return jsonify(this);
    }
}
