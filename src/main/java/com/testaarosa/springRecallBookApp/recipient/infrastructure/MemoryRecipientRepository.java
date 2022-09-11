package com.testaarosa.springRecallBookApp.recipient.infrastructure;

import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import com.testaarosa.springRecallBookApp.recipient.domain.RecipientRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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

    @Override
    public List<Recipient> getAllRecipientsByEmail(String email) {
        return tmpOrderStorage.values()
                .stream()
                .filter(p-> p.getEmail().equals(email))
                .collect(Collectors.toList());
    }

    @Override
    public List<Recipient> getAll() {
        return tmpOrderStorage.values().stream().toList();
    }

    @Override
    public void delete(Long id) {
        tmpOrderStorage.remove(id);
    }

}
