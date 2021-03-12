package com.tecacet.fluidbatch.berka.etl;

import com.tecacet.fluidbatch.berka.dto.BerkaClient;
import com.tecacet.fluidbatch.berka.entity.ClientEntity;

import org.springframework.batch.item.ItemProcessor;

public class ClientProcessor implements ItemProcessor<BerkaClient, ClientEntity> {


    @Override
    public ClientEntity process(BerkaClient berkaClient) {
        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setId(berkaClient.getId());
        clientEntity.setBirthDate(berkaClient.getBirthDate());
        clientEntity.setGender(berkaClient.getGender().equals("M") ? ClientEntity.Gender.MALE : ClientEntity.Gender.FEMALE);
        clientEntity.setDistrict(berkaClient.getDistrict());
        return clientEntity;
    }
}
