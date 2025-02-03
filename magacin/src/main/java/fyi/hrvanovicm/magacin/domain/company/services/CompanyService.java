package fyi.hrvanovicm.magacin.domain.company.services;

import fyi.hrvanovicm.magacin.domain.company.Company;
import fyi.hrvanovicm.magacin.domain.company.dto.requests.CompanyCreateRequestDTO;
import fyi.hrvanovicm.magacin.domain.company.dto.requests.CompanyUpdateRequest;
import fyi.hrvanovicm.magacin.domain.company.dto.responses.CompanyDetailsResponse;
import fyi.hrvanovicm.magacin.domain.company.dto.responses.CompanyResponse;
import fyi.hrvanovicm.magacin.domain.company.repositories.CompanyRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class CompanyService {
    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public List<CompanyResponse> getAll(Specification<Company> spec) {
        return this.companyRepository
                .findAll(spec)
                .stream()
                .map(CompanyResponse::fromEntity)
                .toList();
    }

    public Page<CompanyResponse> getAll(Specification<Company> spec, Pageable pageable) {
        return this.companyRepository.findAll(spec, pageable).map(CompanyResponse::fromEntity);
    }

    @Transactional
    public CompanyResponse create(@Valid CompanyCreateRequestDTO request) {
        var createdEntity = this.companyRepository.save(request.toEntity());
        return CompanyResponse.fromEntity(createdEntity);
    }

    @Transactional
    public CompanyResponse update(@NotNull Long id, @Valid CompanyUpdateRequest request) {
        boolean entityExists = this.companyRepository.existsById(id);

        if (!entityExists) {
            throw new EntityNotFoundException(String.format("Company with id %d not found", id));
        }

        var updatedEntity = this.companyRepository.save(request.toEntity(id));
        return CompanyResponse.fromEntity(updatedEntity);
    }

    public Optional<CompanyDetailsResponse> getById(@NotNull Long id) {
        return this.companyRepository.findById(id).map(CompanyDetailsResponse::fromEntity);
    }

    @Transactional
    public void delete(@NotNull Long id) {
        var entity = this.companyRepository.findById(id).orElseThrow();
        this.companyRepository.deleteById(entity.getId());
    }

    @Transactional
    public void forceDelete(@NotNull Long id) {
        var entity = this.companyRepository.findById(id).orElseThrow();
        this.companyRepository.forceDeleteById(entity.getId());
    }
}
