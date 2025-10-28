package com.siamak.shop.client.user;

import com.siamak.shop.client.user.events.UserPasswordChangedEvent;
import com.siamak.shop.model.User;
import com.siamak.shop.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final OutboxService outbox;

    @Transactional
    public User updatePassword(User p, String newPassword) {
        p.setPassword(newPassword);

        var evt = new UserPasswordChangedEvent(
                UUID.randomUUID().toString(),
                "user.password-changed",
                OffsetDateTime.now(ZoneOffset.UTC).toString(),
                p.getId(),
                p.getName(),
                p.getPassword()
        );

        outbox.add(
                "catalog.product",
                String.valueOf(p.getId()),
                evt.eventType(),
                evt
        );
        return p;
    }
}
