package com.sigma.oilstation.payload;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DebtGetDto {
    private UUID id;
    private UUID borrowerId;
    private double amount;
    private UUID lenderOrBorrowerId;
    private Long lenderId;
    private Timestamp givenTime;
    private Timestamp returnTime;
    private boolean isGiven;
}
