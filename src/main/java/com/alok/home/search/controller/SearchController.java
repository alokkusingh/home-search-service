package com.alok.home.search.controller;

import com.alok.home.search.dto.GetTransactionsResponse;
import com.alok.home.search.service.LuceneTransactionSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping
public class SearchController {


    private final LuceneTransactionSearchService luceneTransactionSearchService;

    public SearchController(LuceneTransactionSearchService luceneTransactionSearchService) {
        this.luceneTransactionSearchService = luceneTransactionSearchService;
    }

    @GetMapping("/transactions")
    public ResponseEntity<GetTransactionsResponse> transaction(
            @RequestParam(value = "description", required = true) String description
    ) throws IOException, ParseException {

        var transactions = luceneTransactionSearchService.searchIndex("description", description);

        return ResponseEntity.accepted()
                .body(GetTransactionsResponse.builder()
                        .count(transactions.size())
                        //.lastTransactionDate(Optional.ofNullable(transactions).orElse(Collections.emptyList()).getLast() == null? null: transactions.getLast().getDate())
                        .transactions(transactions)
                        .build());
    }
}