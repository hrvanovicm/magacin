package fyi.hrvanovicm.magacin.domain.report;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportExportService {
    public JasperPrint export(ReportDetailsResponse report) {
        try {
            InputStream jrxmlInputStream = getClass().getClassLoader().getResourceAsStream("pdf/receipt.jrxml");

            if (jrxmlInputStream == null) {
                System.out.println("Resource not found!");
                return null;
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlInputStream);
            List<ReportDetailsResponse> beans = new ArrayList<>();
            beans.add(report);

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(beans);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("title", "Your Custom Title");

            return  JasperFillManager.fillReport(jasperReport, parameters, dataSource);
         //   JasperViewer.viewReport(jasperPrint, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
