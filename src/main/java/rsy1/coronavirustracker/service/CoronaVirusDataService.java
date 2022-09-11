package rsy1.coronavirustracker.service;

import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import rsy1.coronavirustracker.model.LocationStats;

@Service
public class CoronaVirusDataService {
	
	private List<LocationStats> allStats =  new ArrayList<>();

	private static final String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
		
	public List<LocationStats> getAllStats() {
		return allStats;
	}

	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void fetchVirusData() {
		HttpResponse<String> httpResponse;
		
		List<LocationStats> newStats =  new ArrayList<>();
		
		HttpClient httpClient = HttpClient.newHttpClient();

		HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();

		try {
			httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
			
			StringReader csvBodyReader = new StringReader(httpResponse.body());
			
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
			
			for (CSVRecord record : records) {
			    LocationStats locationStats = new LocationStats();
			    locationStats.setState(record.get("Province/State"));
			    locationStats.setCountry(record.get("Country/Region"));	
			    
			    int latestCases = Integer.parseInt(record.get(record.size() -1));
			    int prevDayCases = Integer.parseInt(record.get(record.size() -2));
			    
			    locationStats.setLatestTotalCases(latestCases);
			    locationStats.setDiffFromPreviousDay(latestCases - prevDayCases);
			    
			    //System.out.println(locationStats);			    
			    newStats.add(locationStats);
			}			
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
		this.allStats = newStats;		
	}
}















