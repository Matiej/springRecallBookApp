package com.testaarosa.springRecallBookApp.order;

import com.testaarosa.springRecallBookApp.BaseTest;
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import com.testaarosa.springRecallBookApp.order.application.PlaceOrderCommand;
import com.testaarosa.springRecallBookApp.order.application.PlaceOrderItem;
import com.testaarosa.springRecallBookApp.order.application.PlaceOrderRecipient;

public abstract class OrderBaseTest extends BaseTest {

    protected PlaceOrderCommand getPlaceOrderCommand(Book book1, int quantity1, Book book2, int quantity2, PlaceOrderRecipient recipient) {
        return PlaceOrderCommand.builder()
                .placeOrderRecipient(recipient)
                .item(new PlaceOrderItem(book1.getId(), quantity1))
                .item(new PlaceOrderItem(book2.getId(), quantity2))
                .build();
    }

    protected PlaceOrderCommand getPlaceOrderCommand(Book book1, int quantity1, Book book2, int quantity2) {
        return getPlaceOrderCommand(book1, quantity1, book2, quantity2, preparePlaceOrderRecipient());
    }

    protected PlaceOrderCommand getPlaceOrderCommand(Book book, int quantity) {
        return PlaceOrderCommand.builder()
                .placeOrderRecipient(preparePlaceOrderRecipient())
                .item(new PlaceOrderItem(book.getId(), quantity))
                .build();
    }

    protected PlaceOrderRecipient preparePlaceOrderRecipient() {
        return preparePlaceOrderRecipient("jannowa@nowa.pl");
    }

    protected PlaceOrderRecipient preparePlaceOrderRecipient(String email) {
        return PlaceOrderRecipient.builder()
                .name("Jan")
                .lastName("Nowak")
                .email(email)
                .build();
    }
}
