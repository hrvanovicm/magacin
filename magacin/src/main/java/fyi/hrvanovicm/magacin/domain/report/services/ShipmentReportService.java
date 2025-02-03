package fyi.hrvanovicm.magacin.domain.report.services;

import fyi.hrvanovicm.magacin.domain.report.repositories.ReportRepository;
import fyi.hrvanovicm.magacin.domain.report.repositories.ShipmentReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShipmentReportService {
    final ReportRepository reportRepository;
    final ShipmentReportRepository shipmentReportRepository;

    @Autowired
    public ShipmentReportService(ReportRepository reportRepository, ShipmentReportRepository shipmentReportRepository) {
        this.reportRepository = reportRepository;
        this.shipmentReportRepository = shipmentReportRepository;
    }
}
