package com.alok.home.search.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class GetTransactionsResponse {

    private Integer count;
    private Date lastTransactionDate;
    private List<Transaction> transactions;

    @Data
    @Builder
    public static class Transaction implements Serializable {
        private Integer id;
        private Date date;
        private Integer debit;
        private Integer credit;
        private String head;
        private String subHead;
        private String bank;
        private String description;
    }
}
