package com.tobeto.rentacar.business.concretes;

import com.tobeto.rentacar.business.abstracts.ModelService;
import com.tobeto.rentacar.business.dtos.requests.model.CreateModelRequest;
import com.tobeto.rentacar.business.dtos.responses.model.CreatedModelResponse;
import com.tobeto.rentacar.business.dtos.responses.model.GetAllModelResponse;
import com.tobeto.rentacar.business.rules.BrandBusinessRules;
import com.tobeto.rentacar.business.rules.FuelBusinessRules;
import com.tobeto.rentacar.business.rules.ModelBusinessRules;
import com.tobeto.rentacar.business.rules.TransmissionBusinessRules;
import com.tobeto.rentacar.core.utilities.mapping.ModelMapperService;
import com.tobeto.rentacar.dataAccess.abstracts.ModelRepository;
import com.tobeto.rentacar.entities.concretes.Model;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ModelManager implements ModelService {

    private ModelRepository modelRepository;
    private ModelMapperService modelMapperService;
    private ModelBusinessRules modelBusinessRules;
    private FuelBusinessRules fuelBusinessRules;
    private TransmissionBusinessRules transmissionBusinessRules;
    private BrandBusinessRules brandBusinessRules;

    @Override
    public CreatedModelResponse add(CreateModelRequest createModelRequest) {

        modelBusinessRules.modelNameCanNotBeDuplicated(createModelRequest.getName());

        brandBusinessRules.isBrandExists(createModelRequest.getBrandId());
        fuelBusinessRules.isFuelExists(createModelRequest.getFuelId());
        transmissionBusinessRules.isTransmissionExists(createModelRequest.getTransmissionId());

        Model model = this.modelMapperService.forRequest()
                .map(createModelRequest, Model.class);
        model.setCreatedDate(LocalDateTime.now());
        model.setId(0);
        Model createdModel = this.modelRepository.save(model);

        CreatedModelResponse createdModelResponse = this.modelMapperService.forResponse()
                .map(createdModel, CreatedModelResponse.class);
        return createdModelResponse;
    }

    @Override
    public List<GetAllModelResponse> getAll() {

        var result = modelRepository.findAll();
        List<GetAllModelResponse> response = result.stream()
                .map(model -> modelMapperService.forResponse()
                        .map(model, GetAllModelResponse.class)).collect(Collectors.toList());

        return response;
    }
}
