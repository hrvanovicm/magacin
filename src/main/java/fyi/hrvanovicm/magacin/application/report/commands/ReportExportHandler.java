package fyi.hrvanovicm.magacin.application.report.commands;

import fyi.hrvanovicm.magacin.application.report.dto.ReportDTO;
import fyi.hrvanovicm.magacin.application.report.dto.ReportDetailsDTO;
import fyi.hrvanovicm.magacin.application.report.queries.ReportQueryService;
import fyi.hrvanovicm.magacin.application.report.requests.ReportEditRequest;
import fyi.hrvanovicm.magacin.application.report.requests.ReportProductRequest;
import fyi.hrvanovicm.magacin.domain.products.ProductService;
import fyi.hrvanovicm.magacin.domain.report.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

@Component
public class ReportExportHandler {
    @Value("classpath:pdf/receipt.jrxml")
    Resource resourceFile;

    private final ReportQueryService reportQueryService;

    @Autowired
    public ReportExportHandler(ReportQueryService reportQueryService) {
        this.reportQueryService = reportQueryService;
    }

    public JasperPrint handle(long reportId) {
        ReportDetailsDTO report = this.reportQueryService.getById(reportId);

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
            List<ReportDetailsDTO> beans = new ArrayList<>();
            beans.add(report);

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
