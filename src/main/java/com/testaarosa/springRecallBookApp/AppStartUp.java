package com.testaarosa.springRecallBookApp;

import com.testaarosa.springRecallBookApp.catalog.application.port.CatalogUseCase;
import com.testaarosa.springRecallBookApp.catalog.application.port.CreateBookCommand;
import com.testaarosa.springRecallBookApp.author.dataBase.AuthorJpaRepository;
import com.testaarosa.springRecallBookApp.author.domain.Author;
import com.testaarosa.springRecallBookApp.order.application.port.OrderUseCase;
import com.testaarosa.springRecallBookApp.order.application.port.QueryOrderUseCase;
import com.testaarosa.springRecallBookApp.recipient.application.port.RecipientUseCase;
import com.testaarosa.springRecallBookApp.recipient.application.port.SaveRecipientCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;

@Component
public class AppStartUp implements CommandLineRunner {
    private final CatalogUseCase catalogUseCase;
    private final OrderUseCase orderUseCase;
    private final QueryOrderUseCase queryOrderUseCase;
    private final String catalogQuery;
    private final Long limit;
    private final RecipientUseCase recipientUseCase;
    private final AuthorJpaRepository authorJpaRepository;

    public AppStartUp(CatalogUseCase catalogUseCase, OrderUseCase orderUseCase, QueryOrderUseCase queryOrderUseCase,
                      @Value("${recallBookApp.schoolCatalog.query}") String catalogQuery,
                      @Value("${recallBookApp.schoolCatalog.limit:5}") Long limit, RecipientUseCase recipientUseCase, AuthorJpaRepository authorJpaRepository) {
        this.catalogUseCase = catalogUseCase;
        this.orderUseCase = orderUseCase;
        this.queryOrderUseCase = queryOrderUseCase;
        this.catalogQuery = catalogQuery;
        this.limit = limit;
        this.recipientUseCase = recipientUseCase;
        this.authorJpaRepository = authorJpaRepository;
    }


    @Override
    public void run(String... args) throws Exception {
        initData();
    }

    private void initData() {
        SaveRecipientCommand recipient = SaveRecipientCommand.builder()
                .name("Ksawery Nowak")
                .phone("661555777")
                .street("Starej Drogi 11")
                .city("Warszawa")
                .zipCode("01-001")
                .email("ksawer@gmail.com")
                .build();
        recipientUseCase.addRecipient(recipient);


        Author james1 = new Author("James","Nowak",1979);
        Author james2 = new Author("James","Nowak",1979);
        Author james3 = new Author("James","Nowak",1979);
        authorJpaRepository.save(james1);
        authorJpaRepository.save(james2);
        authorJpaRepository.save(james3);
        Author anna = new Author("Anna","Babo",1990);
        authorJpaRepository.save(anna);
        Author annaJackowska = new Author("Anna","Jackowska",1980);
        authorJpaRepository.save(anna);

        catalogUseCase.addBook(CreateBookCommand.builder().title("Harry Potter").authors(new HashSet<>(Arrays.asList(james3.getId(), anna.getId()))).year(2022).price(new BigDecimal("112.00")).build());
        catalogUseCase.addBook(CreateBookCommand.builder().title("Black Out").authors(new HashSet<>(Arrays.asList(anna.getId()))).year(2010).price(new BigDecimal("240.10")).build());
        catalogUseCase.addBook(CreateBookCommand.builder().title("Total Snow").authors(new HashSet<>(Arrays.asList(james1.getId()))).year(2001).price(new BigDecimal("40.10")).build());

//        catalogUseCase.addBook(CreateBookCommand.builder().title("Sezon Burz").author("Stefan Burczymucha").year(2005).price(new BigDecimal("281.01")).build());
//        catalogUseCase.addBook(CreateBookCommand.builder().title("Black Knight").author("Jowi Kielosi").year(2014).price(new BigDecimal("90.89")).build());
    }
}
