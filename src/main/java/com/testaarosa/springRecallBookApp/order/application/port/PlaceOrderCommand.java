package com.testaarosa.springRecallBookApp.order.application.port;

import com.testaarosa.springRecallBookApp.order.domain.OrderStatus;
import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import com.testaarosa.springRecallBookApp.recipient.domain.RecipientAddress;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Builder
@Value
public class PlaceOrderCommand {
    @Singular("item")
    List<PlaceOrderItem> itemList;
    OrderStatus orderStatus;
    PlaceOrderRecipient placeOrderRecipient;
}
