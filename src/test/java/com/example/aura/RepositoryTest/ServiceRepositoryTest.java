package com.example.aura.RepositoryTest;

import com.example.aura.Entity.Service.Domain.Service;
import com.example.aura.Entity.Service.Domain.ServiceCategory;
import com.example.aura.Entity.Service.Repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ServiceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ServiceRepository serviceRepository;

    private Service plumbingService;
    private Service electricityService;

    @BeforeEach
    void setUp() {
        plumbingService = new Service();
        plumbingService.setName("Plumbing Repair");
        plumbingService.setDescription("Professional plumbing services");
        plumbingService.setCategory(ServiceCategory.PLUMBING);

        electricityService = new Service();
        electricityService.setName("Electrical Installation");
        electricityService.setDescription("Expert electrical work");
        electricityService.setCategory(ServiceCategory.ELECTRICITY);
    }

    @Test
    void shouldSaveService_always() {

        Service saved = serviceRepository.save(plumbingService);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Plumbing Repair");
        assertThat(saved.getCategory()).isEqualTo(ServiceCategory.PLUMBING);
    }

    @Test
    void shouldFindServicesByCategory_whenExist() {
        entityManager.persistAndFlush(plumbingService);
        entityManager.persistAndFlush(electricityService);

        List<Service> plumbingServices = serviceRepository.findByCategory(ServiceCategory.PLUMBING);

        assertThat(plumbingServices).hasSize(1);
        assertThat(plumbingServices.get(0).getName()).isEqualTo("Plumbing Repair");
    }

    @Test
    void shouldCountServicesByCategory_correctly() {

        entityManager.persistAndFlush(plumbingService);
        entityManager.persistAndFlush(electricityService);

        Long count = serviceRepository.countByCategory(ServiceCategory.PLUMBING);

        assertThat(count).isEqualTo(1L);
    }

    @Test
    void shouldFindServiceByName_whenExists() {
        entityManager.persistAndFlush(plumbingService);

        Optional<Service> found = serviceRepository.findByName("Plumbing Repair");

        assertThat(found).isPresent();
        assertThat(found.get().getCategory()).isEqualTo(ServiceCategory.PLUMBING);
    }

    @Test
    void shouldReturnTrue_whenServiceNameExists() {
        entityManager.persistAndFlush(plumbingService);

        boolean exists = serviceRepository.existsByName("Plumbing Repair");

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalse_whenServiceNameDoesNotExist() {
        boolean exists = serviceRepository.existsByName("Nonexistent Service");

        assertThat(exists).isFalse();
    }
}