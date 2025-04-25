package fyi.hrvanovicm.magacin.domain.unit_measure;

import fyi.hrvanovicm.magacin.infrastructure.persistance.UnitMeasureRepository;
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

    public List<UnitMeasureEntity> findAll() {
        return this.unitMeasureRepository.findAll();
    }

    public Optional<UnitMeasureEntity> findById(long id) {
        return this.unitMeasureRepository.findById(id);
    }

    public boolean exists(long id) {
        return unitMeasureRepository.existsById(id);
    }

    @Transactional
    public void save(UnitMeasureEntity unitMeasure) {
        this.unitMeasureRepository.save(unitMeasure);
    }

    @Transactional
    public void delete(long id) {
        this.unitMeasureRepository.deleteById(id);
    }
}
