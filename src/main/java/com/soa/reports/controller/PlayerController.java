package com.soa.reports.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soa.reports.model.Player;
import com.soa.reports.model.Team;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Slf4j
@RestController
public class PlayerController {

	@GetMapping("/players/report")
	public ResponseEntity<Void> generateReport() {

		try {
			
			Player p1 = new Player(1, "Victor Oshimen", "Forward", 150d);
			Player p2 = new Player(2, "Diego Demme", "Midfielder", 10d);
			Player p3 = new Player(3, "Rafael Leao", "Forward", 300d);
			List<Player> players = Arrays.asList(p1,p2,p3);
			
			var team = Team.builder().name("SempreSoloForzaNapoli").playersData(players).build();

			JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(List.of(team));
			//new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource($P{playersData})

			JasperPrint empReport =
					JasperFillManager.fillReport
				   (
							JasperCompileManager.compileReport(
							ResourceUtils.getFile("classpath:players-details.jrxml")
									.getAbsolutePath()) // path of the jasper report
							, new HashMap<>() // dynamic parameters
							, beanColDataSource
					);

			HttpHeaders headers = new HttpHeaders();
			//set the PDF format
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDispositionFormData("filename", "rosa-fantacalcio.pdf");
			//create the report in PDF format
			return new ResponseEntity
					(JasperExportManager.exportReportToPdf(empReport), headers, HttpStatus.OK);

		} catch(Exception e) {
			log.error("Impossible to generate report. Error: {}", e.getMessage(),e);
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}