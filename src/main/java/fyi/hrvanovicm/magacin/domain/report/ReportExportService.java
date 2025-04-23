package fyi.hrvanovicm.magacin.domain.report;

import jakarta.persistence.EntityNotFoundException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

@Component
public class ReportExportService {
    private final ReportRepository reportRepository;

    @Autowired
    public ReportExportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public JasperPrint export(Long reportId) {
        ReportEntity report = Optional.ofNullable(reportId)
                .flatMap(reportRepository::findById)
                .orElseThrow(EntityNotFoundException::new);

        try {
            InputStream jrxmlInputStream = getClass()
                    .getClassLoader()
                    .getResourceAsStream(
                            report.getType() == ReportType.RECEIPT
                            ? "pdf/receipt.jrxml" : "pdf/shipment.jrxml"
                    );

            if (jrxmlInputStream == null) {
                System.out.println("XML not found!");
                return null;
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlInputStream);
            List<ReportDetails> beans = new ArrayList<>();
            beans.add(ReportDetails.fromEntity(report));

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(beans);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("title", report.toString());

            return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
