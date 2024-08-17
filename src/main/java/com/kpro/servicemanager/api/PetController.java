package com.kpro.servicemanager.api;

import com.kpro.servicemanager.dto.Pet;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tests")
public class PetController implements PetsApi {
    @Override
    public Optional<NativeWebRequest> getRequest() {
        return PetsApi.super.getRequest();
    }

    @Override
    public ResponseEntity<Void> createPets() {
        return PetsApi.super.createPets();
    }

    @Override
    public ResponseEntity<List<Pet>> listPets(Integer limit) {
        return PetsApi.super.listPets(limit);
    }

    @Override
    public ResponseEntity<Pet> showPetById(String petId) {
        return PetsApi.super.showPetById(petId);
    }
}
