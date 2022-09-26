package com.testaarosa.springRecallBookApp.order.application;


import com.testaarosa.springRecallBookApp.order.application.port.OrderUseCase;
import com.testaarosa.springRecallBookApp.order.dataBase.OrderJpaRepository;
import com.testaarosa.springRecallBookApp.order.domain.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static com.testaarosa.springRecallBookApp.order.domain.OrderStatus.ABANDONED;
import static com.testaarosa.springRecallBookApp.order.domain.OrderStatus.NEW;

@Slf4j
@Component
@RequiredArgsConstructor
public class AbandonedOrdersScheduledJobs {
    @Value(value = "${app.days.to.abandon.orders:1}")
    private int DAYS_TO_ABANDON_ORDERS;
    private final OrderJpaRepository repository;
    private final OrderUseCase orderUseCase;

    @Transactional
    @Scheduled(cron = "${app.orders.abandoned.cron}")
    public void run() {
        log.info("Start job: " + this.getClass().getSimpleName() + " to search orders to change status as ABANDONED");
        LocalDateTime timeStamp = LocalDateTime.now().minusHours(DAYS_TO_ABANDON_ORDERS);// test verson
//        LocalDateTime.now().minusDays(DAYS_TO_ABANDON_ORDERS); // test ver
        List<Order> ordersToAbandon = repository.findAllByOrderStatusAndAndCreatedAtLessThanEqual(NEW, timeStamp);
        log.info("Found " + ordersToAbandon.size() + " orders to change status to ABANDONED");
        ordersToAbandon
                .forEach(order -> {
                    orderUseCase.updateOrderStatus(order.getId(), ABANDONED.name());
                    log.info("Order with ID: " + order.getId() + " was updated. Status has changed to: " + ABANDONED);
                });
        log.info("End of job: " + this.getClass().getSimpleName());
    }

}
