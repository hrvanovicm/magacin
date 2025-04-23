package fyi.hrvanovicm.magacin.application.report.queries;

import fyi.hrvanovicm.magacin.application.report.dto.ReportDTO;
import fyi.hrvanovicm.magacin.application.report.dto.ReportDetailsDTO;
import fyi.hrvanovicm.magacin.application.report.requests.ReportSearchParamsDTO;
import fyi.hrvanovicm.magacin.domain.report.ReportService;
import fyi.hrvanovicm.magacin.domain.report.ReportType;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ReportQueryService {
    private final ReportService reportService;

    @Autowired
    public ReportQueryService(ReportService reportService) {
        this.reportService = reportService;
    }

    @Transactional(readOnly = true)
    public List<ReportDTO> getAll(ReportSearchParamsDTO params) {
        return reportService.findAll(params.toSpecification())
                .stream()
                .map(ReportDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReportDetailsDTO getById(long reportId) {
        return this.reportService.findById(reportId)
                .map(ReportDetailsDTO::fromEntity)
                .orElseThrow(EntityNotFoundException::new);
    }

    public List<ReportType> getAllReportTypes() {
        return this.reportService.findAllReportTypes();
    }

    public List<String> getAllCompanyNames() {
        return this.reportService.findAllCompanyNames();
    }

    public List<String> getAllSignedUserNames() {
        return this.reportService.findAllSignedUserNames();
    }
}
