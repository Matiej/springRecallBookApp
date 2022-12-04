package com.testaarosa.springRecallBookApp.order;

import com.testaarosa.springRecallBookApp.BaseTest;
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import com.testaarosa.springRecallBookApp.order.application.PlaceOrderCommand;
import com.testaarosa.springRecallBookApp.order.application.PlaceOrderItem;
import com.testaarosa.springRecallBookApp.order.application.PlaceOrderRecipient;
import com.testaarosa.springRecallBookApp.order.domain.Delivery;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public abstract class OrderBaseTest extends BaseTest {

    protected PlaceOrderCommand getPlaceOrderCommand(Book book1, int quantity1, Book book2, int quantity2, PlaceOrderRecipient recipient) {
        return PlaceOrderCommand.builder()
                .placeOrderRecipient(recipient)
                .item(new PlaceOrderItem(book1.getId(), quantity1))
                .item(new PlaceOrderItem(book2.getId(), quantity2))
                .delivery(Delivery.COURIER)
                .build();
    }

    protected PlaceOrderCommand getPlaceOrderCommand(Book book1, int quantity1, Book book2, int quantity2) {
        return getPlaceOrderCommand(book1, quantity1, book2, quantity2, preparePlaceOrderRecipient());
    }

    protected PlaceOrderCommand getPlaceOrderCommand(Book book, int quantity, Delivery delivery) {
        return PlaceOrderCommand.builder()
                .placeOrderRecipient(preparePlaceOrderRecipient())
                .item(new PlaceOrderItem(book.getId(), quantity))
                .delivery(delivery)
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

    protected User user(String email) {
        return new User(email,"test", List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    protected User admin() {
        return new User(getADMIN_USER(),"test", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }
}
