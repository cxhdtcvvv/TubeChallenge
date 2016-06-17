package tubeChallenge;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CSVconverter {
	 public static void main(String[] args) {

//		 	CSVconverter obj = new CSVconverter();
//			obj.run();

		  }

		  public void run() {

			String csvFile = "/Users/chenxiaohan/Downloads/GeoIPCountryWhois.csv";
			BufferedReader br = null;
		
			String line = "";
			String cvsSplitBy = ",";

			try {

				br = new BufferedReader(new FileReader(csvFile));
				while ((line = br.readLine()) != null) {

				        // use comma as separator
					String[] country = line.split(cvsSplitBy);	//put each value into string array

					System.out.println("Country [code= " + country[4] 
		                                 + " , name=" + country[5] + "]");

				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			System.out.println("Done");
		  }
	
	
	
	
	
}
