package com.tobeto.rentacar.business.concretes;

import com.tobeto.rentacar.business.abstracts.FuelService;
import com.tobeto.rentacar.business.dtos.requests.fuel.CreateFuelRequest;
import com.tobeto.rentacar.business.dtos.requests.fuel.UpdateFuelRequest;
import com.tobeto.rentacar.business.dtos.responses.fuel.CreateFuelResponse;
import com.tobeto.rentacar.business.dtos.responses.fuel.GetAllFuelResponse;
import com.tobeto.rentacar.business.dtos.responses.fuel.GetFuelByIdResponse;
import com.tobeto.rentacar.business.dtos.responses.fuel.UpdateFuelResponse;
import com.tobeto.rentacar.business.rules.FuelBusinessRules;
import com.tobeto.rentacar.core.utilities.mapping.ModelMapperService;
import com.tobeto.rentacar.dataAccess.abstracts.FuelRepository;
import com.tobeto.rentacar.entities.concretes.Fuel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FuelManager implements FuelService {

    private FuelRepository fuelRepository;
    private ModelMapperService modelMapperService;
    private FuelBusinessRules fuelBusinessRules;

    @Override
    public CreateFuelResponse addFuel(CreateFuelRequest createFuelRequest) {

        fuelBusinessRules.fuelNameCanNotBeDuplicated(createFuelRequest.getName());

        Fuel fuel = this.modelMapperService.forRequest().map(createFuelRequest, Fuel.class);
        fuel.setCreatedDate(LocalDateTime.now());
        Fuel createdFuel = this.fuelRepository.save(fuel);

        CreateFuelResponse createdFuelResponse = this.modelMapperService.forResponse().map(createdFuel, CreateFuelResponse.class);
        return createdFuelResponse;
    }

    @Override
    public List<GetAllFuelResponse> getAllFuels() {

        var result = fuelRepository.findAll();
        List<GetAllFuelResponse> response = result.stream()
                .map(fuel -> modelMapperService.forResponse()
                        .map(fuel, GetAllFuelResponse.class)).collect(Collectors.toList());

        return response;
    }

    @Override
    public void deleteFuelById(int id) {
        Fuel fuel = fuelRepository.findById(id).orElseThrow(() -> new RuntimeException("There is no fuel with this id!"));
        fuel.setDeletedDate(LocalDateTime.now());
        fuelRepository.delete(fuel);
    }

    @Override
    public UpdateFuelResponse updateFuelById(int id, UpdateFuelRequest updateFuelRequest) {
        Fuel fuel = fuelRepository.findById(id).orElseThrow(() -> new RuntimeException("There is no fuel with this id!"));
        Fuel updatedFuel = modelMapperService.forResponse().map(updateFuelRequest, Fuel.class);
        fuel.setUpdatedDate(LocalDateTime.now());
        fuel.setName(updatedFuel.getName() != null ? updatedFuel.getName() : fuel.getName());
        fuelRepository.save(fuel);
        UpdateFuelResponse response = modelMapperService.forResponse().map(fuel, UpdateFuelResponse.class);
        return response;
    }

    @Override
    public GetFuelByIdResponse getFuelById(int id) {
        Fuel fuel = fuelRepository.findById(id).orElseThrow(() -> new RuntimeException("There is no fuel with this id!"));
        GetFuelByIdResponse response = modelMapperService.forResponse().map(fuel, GetFuelByIdResponse.class);
        return response;
    }
}
