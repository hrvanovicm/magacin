package fyi.hrvanovicm.magacin.application.http.v1;

import fyi.hrvanovicm.magacin.domain.unit_measure.services.UnitMeasureService;
import fyi.hrvanovicm.magacin.domain.unit_measure.dto.request.UnitMeasureCreateRequestDTO;
import fyi.hrvanovicm.magacin.domain.unit_measure.dto.response.UnitMeasureResponse;
import fyi.hrvanovicm.magacin.domain.unit_measure.dto.request.UnitMeasureUpdateRequestDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/unit-measures")
public class UnitMeasureRestController {
    private final UnitMeasureService unitMeasureService;

    public UnitMeasureRestController(UnitMeasureService unitMeasureService) {
        this.unitMeasureService = unitMeasureService;
    }

    @GetMapping
    public List<UnitMeasureResponse> getAllUnitMeasurements() {
        return this.unitMeasureService.getAll();
    }

    @GetMapping("{id}")
    public UnitMeasureResponse getUnitMeasureById(
            @PathVariable(required = true) long id
    ) {
        var unitMeasure = this.unitMeasureService.getById(id);
        return unitMeasure.orElseThrow();
    }

    @PostMapping
    public void createUnitMeasure(
            @RequestBody @Valid UnitMeasureCreateRequestDTO request
    ) {
        this.unitMeasureService.create(request);
    }

    @PutMapping("{id}")
    public void updateUnitMeasure(
            @PathVariable Long id,
            @RequestBody UnitMeasureUpdateRequestDTO request
    ) {
        this.unitMeasureService.update(id, request);
    }
}
