package rsy1.coronavirustracker.controlers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import rsy1.coronavirustracker.model.LocationStats;
import rsy1.coronavirustracker.service.CoronaVirusDataService;

@Controller
public class HomeController {
	
	@Autowired
	CoronaVirusDataService dataService;

	@GetMapping("/")
	public String bhome(Model model) {
		List<LocationStats> allStats = dataService.getAllStats();
		int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
		int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPreviousDay()).sum();
		
		model.addAttribute("locationStats", dataService.getAllStats());
		model.addAttribute("totalReportedCases", totalReportedCases);
		model.addAttribute("totalNewCases", totalNewCases);
		
		return "home";
	}
}
