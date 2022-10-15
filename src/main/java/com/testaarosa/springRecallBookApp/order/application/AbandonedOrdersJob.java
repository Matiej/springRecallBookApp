package com.testaarosa.springRecallBookApp.order.application;


import com.testaarosa.springRecallBookApp.clock.Clock;
import com.testaarosa.springRecallBookApp.order.application.port.OrderUseCase;
import com.testaarosa.springRecallBookApp.order.dataBase.OrderJpaRepository;
import com.testaarosa.springRecallBookApp.order.domain.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static com.testaarosa.springRecallBookApp.order.domain.OrderStatus.ABANDONED;
import static com.testaarosa.springRecallBookApp.order.domain.OrderStatus.NEW;

@Slf4j
@Component
@RequiredArgsConstructor
public class AbandonedOrdersJob {
    @Value(value = "${app.duration.to.abandon.orders:1}")
    private Duration DURATION_TO_ABANDON_ORDERS;
    private final User systemUser;
    private final OrderJpaRepository repository;
    private final OrderUseCase orderUseCase;
    private final Clock clock;

    @Transactional
    @Scheduled(cron = "${app.orders.abandoned.cron}")
    public void run() {
        log.info("Start job: " + this.getClass().getSimpleName() + " to search orders to change status as ABANDONED");
        LocalDateTime timeStamp = clock.now().minus(DURATION_TO_ABANDON_ORDERS);//
        List<Order> ordersToAbandon = repository.findAllByOrderStatusAndAndCreatedAtLessThanEqual(NEW, timeStamp);
        log.info("Found " + ordersToAbandon.size() + " orders to change status to ABANDONED");

        ordersToAbandon
                .forEach(order -> {
                    UpdateOrderStatusCommand command = UpdateOrderStatusCommand.builder()
                            .orderId(order.getId())
                            .orderStatus(ABANDONED)
                            .user(systemUser)
                            .build();
                    orderUseCase.updateOrderStatus(command);
                    log.info("Order with ID: " + order.getId() + " was updated. Status has changed to: " + ABANDONED);
                });
        log.info("End of job: " + this.getClass().getSimpleName());
    }

}
