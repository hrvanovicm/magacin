package fyi.hrvanovicm.magacin.application.report.commands;

import fyi.hrvanovicm.magacin.domain.report.ReportService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteReportHandler {
    private final ReportService reportService;

    @Autowired
    public DeleteReportHandler(ReportService reportService) {
        this.reportService = reportService;
    }

    public void handle(long reportId) {
        var isExists = this.reportService.exists(reportId);

        if(!isExists) {
            throw new EntityNotFoundException();
        }

        this.reportService.delete(reportId);
    }
}
