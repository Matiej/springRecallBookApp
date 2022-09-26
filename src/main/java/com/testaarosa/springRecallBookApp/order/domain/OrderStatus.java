package com.testaarosa.springRecallBookApp.order.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public enum OrderStatus {
    NEW {
        @Override
        public UpdateOrderStatusResult updateOrderStatus(OrderStatus status) {
            return switch (status) {
                case PAID -> UpdateOrderStatusResult.ok(status);
                case CANCELED, ABANDONED -> UpdateOrderStatusResult.revoked(status);
                default -> super.updateOrderStatus(status);
            };
        }
    },
    PAID {
        @Override
        public UpdateOrderStatusResult updateOrderStatus(OrderStatus status) {
            if(status == SHIPPED) {
                return UpdateOrderStatusResult.ok(status);
            }
            return super.updateOrderStatus(status);
        }
    },
    CANCELED,
    ABANDONED,
    SHIPPED;

    public UpdateOrderStatusResult updateOrderStatus(OrderStatus status) {
        throw new IllegalArgumentException("Unable to change current order status: " + this.name()
                + "  to '" + status.name() + "' status.");
    }

    public static Optional<OrderStatus> parseOrderString(String value) {
        return Arrays.stream(values()).
                filter(status -> StringUtils.equalsIgnoreCase(status.name(), value))
                .findAny();
    }
}
