package com.soa.reports.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soa.reports.model.Player;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@RestController
public class PlayerController {

	@GetMapping("/players/report")
	public ResponseEntity<Void> generateReport() {

		try {
			Player p1 = new Player(1, "Victor Oshimen", "Forward", 150d);
			Player p2 = new Player(2, "Diego Demme", "Midfielder", 10d);
			Player p3 = new Player(2, "Rafael Leao", "Midfielder", 300d);


			List<Player> players = new ArrayList<>();
			players.add(p1);
			players.add(p2);
			players.add(p3);

			//dynamic parameters required for report
			Map<String, Object> params = new HashMap<>();
			params.put("teamName", "SempreSoloForzaNapoli");
			params.put("playersData", new JRBeanCollectionDataSource(players));

			JasperPrint empReport =
					JasperFillManager.fillReport
				   (
							JasperCompileManager.compileReport(
							ResourceUtils.getFile("classpath:players-details.jrxml")
									.getAbsolutePath()) // path of the jasper report
							, params // dynamic parameters
							, new JREmptyDataSource()
					);

			HttpHeaders headers = new HttpHeaders();
			//set the PDF format
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDispositionFormData("filename", "employees-details.pdf");
			//create the report in PDF format
			return new ResponseEntity
					(JasperExportManager.exportReportToPdf(empReport), headers, HttpStatus.OK);

		} catch(Exception e) {
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}