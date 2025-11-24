package com.example.aura.TestContainer;

import com.example.aura.Entity.Service.Domain.Service;
import com.example.aura.Entity.Service.Domain.ServiceCategory;
import com.example.aura.Entity.Service.Repository.ServiceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ServiceRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ServiceRepository serviceRepository;

    @Test
    void shouldPersistMultipleServices_inPostgreSQL() {
        Service plumbing = new Service();
        plumbing.setName("Plumbing Service");
        plumbing.setDescription("Fix pipes");
        plumbing.setCategory(ServiceCategory.PLUMBING);

        Service electricity = new Service();
        electricity.setName("Electrical Service");
        electricity.setDescription("Fix wiring");
        electricity.setCategory(ServiceCategory.ELECTRICITY);

        entityManager.persistAndFlush(plumbing);
        entityManager.persistAndFlush(electricity);
        entityManager.clear();

        List<Service> all = serviceRepository.findAll();
        assertThat(all).hasSize(2);
    }

    @Test
    void shouldFindServicesByCategory_inPostgreSQL() {
        Service service1 = new Service();
        service1.setName("Service 1");
        service1.setCategory(ServiceCategory.PLUMBING);
        entityManager.persistAndFlush(service1);

        Service service2 = new Service();
        service2.setName("Service 2");
        service2.setCategory(ServiceCategory.PLUMBING);
        entityManager.persistAndFlush(service2);

        Service service3 = new Service();
        service3.setName("Service 3");
        service3.setCategory(ServiceCategory.ELECTRICITY);
        entityManager.persistAndFlush(service3);

        entityManager.clear();

        List<Service> plumbingServices = serviceRepository.findByCategory(ServiceCategory.PLUMBING);

        assertThat(plumbingServices).hasSize(2);
        assertThat(plumbingServices)
                .extracting(Service::getCategory)
                .containsOnly(ServiceCategory.PLUMBING);
    }

    @Test
    void shouldCountServicesByCategory_inPostgreSQL() {
        for (int i = 0; i < 3; i++) {
            Service service = new Service();
            service.setName("Plumbing " + i);
            service.setCategory(ServiceCategory.PLUMBING);
            entityManager.persistAndFlush(service);
        }
        entityManager.clear();

        Long count = serviceRepository.countByCategory(ServiceCategory.PLUMBING);

        assertThat(count).isEqualTo(3L);
    }
}