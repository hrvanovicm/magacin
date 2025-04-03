package fyi.hrvanovicm.magacin.domain.unit_measure;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UnitMeasureService {

    private final UnitMeasureRepository unitMeasureRepository;

    @Autowired
    public UnitMeasureService(UnitMeasureRepository unitMeasureRepository) {
        this.unitMeasureRepository = unitMeasureRepository;
    }

    public List<UnitMeasureResponse> getAll() {
        return this.unitMeasureRepository.findAll()
            .stream()
            .map(UnitMeasureResponse::fromEntity)
            .toList();
    }

    public Optional<UnitMeasureResponse> getById(@NotNull Long id) {
        return this.unitMeasureRepository.findById(id).map(UnitMeasureResponse::fromEntity);
    }

    @Transactional
    public UnitMeasureResponse create(@Valid UnitMeasureCreateRequestDTO createRequest) {
        var createdEntity = this.unitMeasureRepository.save(createRequest.toEntity());
        return UnitMeasureResponse.fromEntity(createdEntity);
    }

    @Transactional
    public UnitMeasureResponse update(@NotNull Long id, @Valid UnitMeasureUpdateRequestDTO request) {
        boolean entityExists = this.unitMeasureRepository.existsById(id);

        if (!entityExists) {
            throw new EntityNotFoundException(String.format("Unit measure with id %d not found", id));
        }

        var updatedEntity = this.unitMeasureRepository.save(request.toEntity(id));
        return UnitMeasureResponse.fromEntity(updatedEntity);
    }

    @Transactional
    public void delete(@NotNull Long id) {
        var entity = this.unitMeasureRepository.findById(id).orElseThrow();
        this.unitMeasureRepository.deleteById(entity.getId());
    }

    @Transactional
    public void forceDelete(@NotNull Long id) {
        var entity = this.unitMeasureRepository.findById(id).orElseThrow();
        this.unitMeasureRepository.forceDeleteById(entity.getId());
    }
}
