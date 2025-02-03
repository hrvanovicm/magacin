package fyi.hrvanovicm.magacin.application.http.v1;

import fyi.hrvanovicm.magacin.application.requests.CompanySearchCriteriaDTO;
import fyi.hrvanovicm.magacin.domain.company.dto.requests.CompanyCreateRequestDTO;
import fyi.hrvanovicm.magacin.domain.company.dto.requests.CompanyUpdateRequest;
import fyi.hrvanovicm.magacin.domain.company.dto.responses.CompanyDetailsResponse;
import fyi.hrvanovicm.magacin.domain.company.dto.responses.CompanyResponse;
import fyi.hrvanovicm.magacin.domain.company.services.CompanyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
public class CompanyRestController {
    private final CompanyService companyService;

    @Autowired
    public CompanyRestController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("all")
    public ResponseEntity<List<CompanyResponse>> getAllCompanies(
            CompanySearchCriteriaDTO criteria
    ) {
        return ResponseEntity.ok(companyService.getAll(criteria.toSpecification()));
    }

    @GetMapping("")
    public ResponseEntity<Page<CompanyResponse>> getAllCompaniesPaginated(
            CompanySearchCriteriaDTO criteria,
            Pageable pageable
    ) {
        return ResponseEntity.ok(companyService.getAll(criteria.toSpecification(), pageable));
    }


    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyDetailsResponse> getCompanyById(
            @PathVariable(name = "companyId") Long companyId
    ) {
        CompanyDetailsResponse companyResponse = companyService.getById(companyId).orElseThrow();
        return ResponseEntity.status(HttpStatus.OK).body(companyResponse);
    }

    @PostMapping("")
    public ResponseEntity<CompanyResponse> createCompany(
            @RequestBody @Valid CompanyCreateRequestDTO request
    ) {
        CompanyResponse createdCompany = companyService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCompany);
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyResponse> updateCompanyById(
            @PathVariable Long companyId,
            @RequestBody @Valid CompanyUpdateRequest request
    ) {
        CompanyResponse updatedCompany = companyService.update(companyId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedCompany);
    }
}
