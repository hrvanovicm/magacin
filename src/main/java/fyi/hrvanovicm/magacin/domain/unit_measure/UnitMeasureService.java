package fyi.hrvanovicm.magacin.domain.unit_measure;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class UnitMeasureService {
    private final UnitMeasureRepository unitMeasureRepository;

    @Autowired
    public UnitMeasureService(UnitMeasureRepository unitMeasureRepository) {
        this.unitMeasureRepository = unitMeasureRepository;
    }

    public List<UnitMeasureDTO> getAll() {
        return this.unitMeasureRepository.findAll().stream().map(UnitMeasureDTO::fromEntity).toList();
    }

    public Optional<UnitMeasureDTO> getById(Long id) {
        return this.unitMeasureRepository.findById(id).map(UnitMeasureDTO::fromEntity);
    }

    @Transactional
    public void save(@Valid UnitMeasureRequest request) {
        var unitMeasure = Optional.ofNullable(request.getId()).flatMap(unitMeasureRepository::findById).orElse(new UnitMeasureEntity());

        unitMeasure.setName(request.getName());
        unitMeasure.setShortName(request.getShortName());
        unitMeasure.setIsInteger(request.getIsInteger());

        this.unitMeasureRepository.save(unitMeasure);
    }

    @Transactional
    public void delete(Long id) {
        var unitMeasure = Optional.ofNullable(id).flatMap(unitMeasureRepository::findById).orElseThrow();

        this.unitMeasureRepository.deleteById(unitMeasure.getId());
    }
}
