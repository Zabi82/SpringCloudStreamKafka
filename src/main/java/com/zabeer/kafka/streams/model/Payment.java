package com.zabeer.kafka.streams.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @JsonProperty
    private String txnId;
    @JsonProperty
    private String custAccountNumber;
    @JsonProperty
    private Date txnDate;
    @JsonProperty
    private DebitCredit txnType;
    @JsonProperty
    private BigDecimal txnAmount;



}
