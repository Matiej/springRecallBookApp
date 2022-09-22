package com.testaarosa.springRecallBookApp.order.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public enum OrderStatus {
    NEW {
        @Override
        public OrderStatus updateOrderStatus(OrderStatus status) {
            return switch (status) {
                case PAID -> PAID;
                case CANCELED -> CANCELED;
                case ABANDONED -> ABANDONED;
                default -> super.updateOrderStatus(status);
            };
        }
    },
    PAID {
        @Override
        public OrderStatus updateOrderStatus(OrderStatus status) {
            if(status == SHIPPED) {
                return SHIPPED;
            }
            return super.updateOrderStatus(status);
        }
    },
    CANCELED,
    ABANDONED,
    SHIPPED;

    public OrderStatus updateOrderStatus(OrderStatus status) {
        throw new IllegalArgumentException("Unable to change current order status: " + this.name()
                + "  to '" + status.name() + "' status.");
    }

    public static Optional<OrderStatus> parseOrderString(String value) {
        return Arrays.stream(values()).
                filter(status -> StringUtils.equalsIgnoreCase(status.name(), value))
                .findAny();
    }
}
