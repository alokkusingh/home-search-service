package com.alok.home.search.service;

import com.alok.home.commons.repository.TransactionRepository;
import com.alok.home.search.dto.GetTransactionsResponse;
import jakarta.annotation.PostConstruct;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.util.*;

@Service
public class LuceneTransactionSearchService {
    private final Directory memoryIndex = new RAMDirectory();
    private final Analyzer analyzer = new StandardAnalyzer();

    private final TransactionRepository transactionRepository;

    public LuceneTransactionSearchService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @PostConstruct
    public void indexTransactions() {

        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        try {
            var docs = transactionRepository.findAll()
                    .stream().filter(transaction -> transaction.getDescription() != null)
                    .map(transactionEntity -> GetTransactionsResponse.Transaction.builder()
                            .description(transactionEntity.getDescription())
                            .id(transactionEntity.getId())
                            .head(transactionEntity.getHead())
                            .subHead(transactionEntity.getSubHead())
                            .date(transactionEntity.getDate())
                            .bank(transactionEntity.getBank())
                            .debit(transactionEntity.getDebit())
                            .credit(transactionEntity.getCredit())
                            .build())
                    .map(transactionDto -> {
                        var doc = new Document();
                        doc.add(new TextField("description", transactionDto.getDescription(), Field.Store.YES));
                        byte[] data = SerializationUtils.serialize(transactionDto);
                        doc.add(new StoredField("transaction",  new BytesRef(data)));

                        return doc;
                    })
                    .toList();

            IndexWriter writer = new IndexWriter(memoryIndex, indexWriterConfig);
            docs.forEach(
                    doc -> {
                        try {
                            writer.addDocument(doc);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<GetTransactionsResponse.Transaction> searchIndex(String inField, String queryString) throws IOException, ParseException {
        var queryParser = new QueryParser(inField, analyzer);
        queryParser.setAllowLeadingWildcard(true);
        Query query = queryParser.parse("*" + queryString + "*");

        IndexReader indexReader = DirectoryReader.open(memoryIndex);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TopDocs topDocs = searcher.search(query, 5000);
        return Arrays.stream(topDocs.scoreDocs)
                .map(scoreDoc -> {
                    Document doc = null;
                    try {
                        doc = searcher.doc(scoreDoc.doc);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return  (GetTransactionsResponse.Transaction) SerializationUtils.deserialize(doc.getFields().getLast().binaryValue().bytes);
                })
                .sorted(Comparator.comparing(GetTransactionsResponse.Transaction::getDate).reversed())
                .toList();
    }

    public void deleteDocument(Term term) {
        try {
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
            IndexWriter writter = new IndexWriter(memoryIndex, indexWriterConfig);
            writter.deleteDocuments(term);
            writter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Document> searchIndex(Query query) {
        try {
            IndexReader indexReader = DirectoryReader.open(memoryIndex);
            IndexSearcher searcher = new IndexSearcher(indexReader);
            TopDocs topDocs = searcher.search(query, 10);
            List<Document> documents = new ArrayList<>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                documents.add(searcher.doc(scoreDoc.doc));
            }

            return documents;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public List<Document> searchIndex(Query query, Sort sort) {
        try {
            IndexReader indexReader = DirectoryReader.open(memoryIndex);
            IndexSearcher searcher = new IndexSearcher(indexReader);
            TopDocs topDocs = searcher.search(query, 10, sort);
            List<Document> documents = new ArrayList<>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                documents.add(searcher.doc(scoreDoc.doc));
            }

            return documents;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}