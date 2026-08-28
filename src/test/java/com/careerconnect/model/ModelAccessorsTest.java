package com.careerconnect.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ModelAccessorsTest {
    @SuppressWarnings("unused")
    static Stream<Class<?>> modelTypes() {
        return Stream.of(
                Application.class, Category.class, Company.class, Education.class,
                Experience.class, Job.class, Notification.class, Recruiter.class,
                Resume.class, SavedJob.class, Skill.class, User.class);
    }

    @ParameterizedTest
    @MethodSource("modelTypes")
    void defaultConstructorCreatesModel(Class<?> modelType) throws Exception {
        Object model = modelType.getConstructor().newInstance();

        assertNotNull(model);
    }

    @ParameterizedTest
    @MethodSource("modelTypes")
    void publicAccessorsRoundTripValues(Class<?> modelType) throws Exception {
        Object model = modelType.getConstructor().newInstance();

        for (Method setter : modelType.getMethods()) {
            if (!setter.getName().startsWith("set") || setter.getParameterCount() != 1) {
                continue;
            }

            String property = setter.getName().substring(3);
            Method getter;
            try {
                getter = modelType.getMethod("get" + property);
            } catch (NoSuchMethodException exception) {
                getter = modelType.getMethod("is" + property);
            }
            Object value = valueFor(setter.getParameterTypes()[0]);

            setter.invoke(model, value);

            assertEquals(value, getter.invoke(model), modelType.getSimpleName() + "." + property);
        }
    }

    @Test
    void valueConstructorsPopulateCategoryAndUser() throws Exception {
        Category category = new Category(7, "Engineering", "code", "Build software");
        assertEquals(7, category.getId());
        assertEquals("Engineering", category.getName());
        assertEquals("code", category.getIcon());
        assertEquals("Build software", category.getDescription());

        Timestamp createdAt = Timestamp.valueOf("2026-01-02 03:04:05");
        Constructor<User> constructor = User.class.getConstructor(
                int.class, String.class, String.class, String.class, String.class,
                String.class, String.class, String.class, Timestamp.class);
        User user = constructor.newInstance(3, "Alex", "alex@example.com", "secret",
                "555-0100", "JOB_SEEKER", "Profile", "avatar.png", createdAt);

        assertEquals(3, user.getId());
        assertEquals("Alex", user.getName());
        assertEquals("alex@example.com", user.getEmail());
        assertEquals(createdAt, user.getCreatedAt());
    }

    private static Object valueFor(Class<?> type) {
        if (type == int.class) {
            return 42;
        }
        if (type == long.class) {
            return 42L;
        }
        if (type == boolean.class) {
            return true;
        }
        if (type == double.class) {
            return 42.0d;
        }
        if (type == Job.class) {
            return new Job();
        }
        if (type == Timestamp.class) {
            return Timestamp.valueOf("2026-01-02 03:04:05");
        }
        return "test-value";
    }
}