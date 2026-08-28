package com.careerconnect.dao;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.careerconnect.model.Education;
import com.careerconnect.model.Experience;
import com.careerconnect.model.User;

class UserDAOTest {
    private final UserDAO userDAO = new UserDAO();

    @Test
    void authenticatesSeededUserCaseInsensitively() {
        User user = userDAO.authenticateUser("ALEX.STUDENT@COLLEGE.EDU", "password123");

        assertNotNull(user);
        assertEquals("Alex Johnson", user.getName());
        assertEquals("JOB_SEEKER", user.getRole());
    }

    @Test
    void rejectsUnknownCredentials() {
        assertNull(userDAO.authenticateUser("unknown@example.com", "wrong-password"));
    }

    @Test
    void returnsSeededUserById() {
        User user = userDAO.getUserById(4);

        assertNotNull(user);
        assertEquals("alex.student@college.edu", user.getEmail());
    }

    @Test
    void returnsNullForUnknownUserId() {
        assertNull(userDAO.getUserById(Integer.MAX_VALUE));
    }

    @Test
    void returnsEducationForUser() {
        List<Education> education = userDAO.getUserEducation(4);

        assertEquals(1, education.size());
        assertEquals(4, education.get(0).getUserId());
        assertTrue(education.get(0).getDegree().contains("Computer Science"));
    }

    @Test
    void returnsExperienceForUser() {
        List<Experience> experience = userDAO.getUserExperience(4);

        assertEquals(1, experience.size());
        assertEquals(4, experience.get(0).getUserId());
        assertEquals("Software Engineer Intern", experience.get(0).getTitle());
    }

    @Test
    void resetsPasswordForKnownEmail() {
        String email = "sarah.m@college.edu";

        assertTrue(userDAO.resetPassword(email, "temporary-test-password"));
        assertNotNull(userDAO.authenticateUser(email, "temporary-test-password"));
    }
}
