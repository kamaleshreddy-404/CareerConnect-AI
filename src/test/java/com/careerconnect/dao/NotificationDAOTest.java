package com.careerconnect.dao;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.careerconnect.model.Notification;

class NotificationDAOTest {
    @Test
    void createsAndFindsNotificationForUser() {
        NotificationDAO notificationDAO = new NotificationDAO();
        int userId = 999999;

        assertTrue(notificationDAO.createNotification(userId, "Test title", "Test message"));

        List<Notification> notifications = notificationDAO.getNotificationsByUser(userId);
        assertEquals(1, notifications.size());
        assertEquals("Test title", notifications.get(0).getTitle());
        assertFalse(notifications.get(0).isRead());
    }

    @Test
    void marksExistingNotificationAsRead() {
        NotificationDAO notificationDAO = new NotificationDAO();
        int userId = 999998;
        notificationDAO.createNotification(userId, "Read title", "Read message");

        Notification notification = notificationDAO.getNotificationsByUser(userId).stream()
                .findFirst()
                .orElse(null);
        assertNotNull(notification);
        assertTrue(notificationDAO.markAsRead(notification.getId()));
        assertTrue(notificationDAO.getNotificationsByUser(userId).get(0).isRead());
    }

    @Test
    void returnsFalseWhenMarkingUnknownNotification() {
        assertFalse(new NotificationDAO().markAsRead(Integer.MAX_VALUE));
    }
}
