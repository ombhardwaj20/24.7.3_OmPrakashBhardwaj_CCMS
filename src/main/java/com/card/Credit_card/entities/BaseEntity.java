package com.card.Credit_card.entities;

//import jakarta.persistence.Entity;
//import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.OffsetDateTime;

@MappedSuperclass
@Getter
@Setter

public class BaseEntity {
    /**
     * The creation time.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private OffsetDateTime createdOn;

    /**
     * The last updated time.
     */
    @UpdateTimestamp
    private OffsetDateTime lastUpdatedOn;
}
