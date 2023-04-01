package com.logic.account.transactions;

import static com.logic.account.utils.MyUtils.jsonify;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.UUIDGenerator;
import org.springframework.data.annotation.Immutable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.logic.account.account.personal.PersonalAccount;
import com.logic.account.user.Client;
import com.logic.account.utils.Currency;

import lombok.*;

@Entity
@Immutable
@Getter @EqualsAndHashCode(onlyExplicitlyIncluded =  true)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // for Jackson & Hibernate
public class UserTransaction {
    @Id @Column(updatable = false)
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "time-uuid")
	@GenericGenerator( 
		name = "time-uuid",
		strategy = "org.hibernate.id.UUIDGenerator",
		parameters = {
			@Parameter(
				name = UUIDGenerator.UUID_GEN_STRATEGY_CLASS,
				value = "org.hibernate.id.uuid.CustomVersionOneStrategy"
			)
		}
	)
        private UUID id;
    @Column(nullable = false)
        @NonNull private BigDecimal money;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @NonNull private Client sender;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @NonNull private Client receiver;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(
        foreignKeyDefinition = "FOREIGN KEY (sender_account_id) REFERENCES personal_account ON DELETE SET NULL",
        value = ConstraintMode.CONSTRAINT), nullable = true)
        private PersonalAccount senderAccount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(
        foreignKeyDefinition = "FOREIGN KEY (receiver_account_id) REFERENCES personal_account ON DELETE SET NULL",
        value = ConstraintMode.CONSTRAINT), nullable = true)
        private PersonalAccount receiverAccount;
    @Column(nullable = false)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        @NonNull private LocalDateTime time; 
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @NonNull private Currency currency;
    @NonNull private String via;

    protected UserTransaction(
        @NonNull BigDecimal money, @NonNull Client sender, @NonNull Client receiver,
        @NonNull PersonalAccount senderAccount, @NonNull PersonalAccount receiverAccount,
        @NonNull String via
    ) {
        this.money = money;
        this.sender = sender;
        this.receiver = receiver;
        this.senderAccount = senderAccount;
        this.receiverAccount = receiverAccount;
        this.currency = senderAccount.getAccount().getCurrency();
        this.via = via;
        this.time = LocalDateTime.now();
    }

    /**
     * Must be created within a transaction
     * @param money negative values are used for withdrawal
     * @param senderAccount where money is withdrawn from
     * @param receiver who receives money
     * @return immutable transaction
     */
    public static UserTransaction of(
        @NonNull BigDecimal money,
        @NonNull PersonalAccount senderAccount, @NonNull PersonalAccount receiverAccount,
        @NonNull String via
    ) {
        senderAccount.subtractMoney(money);
        receiverAccount.addMoney(money);

        return new UserTransaction(
            money, senderAccount.getClient(), receiverAccount.getClient(), 
            senderAccount, receiverAccount, via
        );
    }

    @Override 
    public String toString() {
        return jsonify(this);
    }
}
