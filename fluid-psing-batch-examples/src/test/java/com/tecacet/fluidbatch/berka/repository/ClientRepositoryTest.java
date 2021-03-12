package com.tecacet.fluidbatch.berka.repository;

import com.tecacet.fluidbatch.berka.entity.ClientEntity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void test() {
        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setId(100L);
        clientEntity.setBirthDate(LocalDate.of(1999, 1, 1));
        clientEntity.setGender(ClientEntity.Gender.FEMALE);
        clientEntity.setDistrict("9");
        clientRepository.save(clientEntity);

        List<ClientEntity> clientEntityList = clientRepository.findAll();
    }
}
