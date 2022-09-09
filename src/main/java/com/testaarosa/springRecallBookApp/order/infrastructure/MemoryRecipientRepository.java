package com.testaarosa.springRecallBookApp.order.infrastructure;

import com.testaarosa.springRecallBookApp.order.domain.Recipient;
import com.testaarosa.springRecallBookApp.order.domain.RecipientRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
class MemoryRecipientRepository implements RecipientRepository {
    private final Map<Long, Recipient> tmpOrderStorage = new ConcurrentHashMap<>();
    private final AtomicLong ID_NEXT_VALUE = new AtomicLong(0);

    @Override
    public Recipient save(Recipient recipient) {
        if (recipient.getId() == null) {
            recipient.setId(ID_NEXT_VALUE.incrementAndGet());
        }
        tmpOrderStorage.put(recipient.getId(), recipient);
        return recipient;
    }

    @Override
    public Optional<Recipient> findById(Long id) {
        return Optional.ofNullable(tmpOrderStorage.get(id));
    }
}
