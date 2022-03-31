package com.ver17.tryit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Iterator;
//import java.util.*;
import java.util.stream.*;
import java.util.function.*;

import org.json.simple.JSONArray;

//import javax.json.JsonObject;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Hello {

	public static Double getMinValue(ArrayList<Double> listDouble) {
		//Collections.min(listDouble);
		return Collections.min(listDouble);
	}

	public static Double getCurrentCloseNAV(ArrayList<Double> listDouble) {
		return listDouble.get(listDouble.size() - 1);
	}

	public static String getRequiredString(String response) {
		int start = (response.indexOf("\"low\":[")) + 7;
		int end = response.indexOf("],", start);

		return response.substring(start, (end - 2));

	}

	public static List<Float> convertStringToNav(String requiredStr) {
		String[] label = requiredStr.split(",");
		List<String> wordList = Arrays.asList(label);
		// wordList.forEach((n)-> System.out.println(n));

		return wordList.stream().map(Float::parseFloat).collect(Collectors.toList());
	}

	public static String calculatePercentage(Double minValue, Double lastDayLowClose, Double percent) {

		if ((minValue + (minValue * (percent / 100))) >= lastDayLowClose) {
			return "Look";
		} else {
			return "More than 10%";
		}

	}

	public static HttpResponse<String> webServe(String companyName, String period) {
		HttpResponse<String> response = null;
		HttpRequest request = null;

		try {
			StringBuffer uriGet = new StringBuffer();
			uriGet.append("https://yh-finance.p.rapidapi.com/market/get-charts?symbol=" + companyName
					+ "&interval=1d&range=" + period + "&region=IN");
			/*20211118-> uriGet.append("https://apidojo-yahoo-finance-v1.p.rapidapi.com/market/get-charts?symbol=" + companyName
					+ "&interval=1d&range=" + period + "&region=IN");
					
			 * .header("x-rapidapi-key", "dc3cfb578dmsh48906544256364ap1a4599jsn7ffb2dc0ff5e")
					.header("x-rapidapi-host", "apidojo-yahoo-finance-v1.p.rapidapi.com")
			 * */

			// TODO Auto-generated method stub
			request = HttpRequest.newBuilder().uri(URI.create(uriGet.toString()))
					.header("x-rapidapi-key", "2374d6edecmsh8dde891a649f954p1c2a21jsnc1072f512182")
					.header("x-rapidapi-host", "yh-finance.p.rapidapi.com")
					.method("GET", HttpRequest.BodyPublishers.noBody()).build();

			response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;

	}

	private static JSONObject createJSONObject(String jsonString) {
		JSONObject jsonObject = new JSONObject();
		JSONParser jsonParser = new JSONParser();
		if ((jsonString != null) && !(jsonString.isEmpty())) {
			try {
				jsonObject = (JSONObject) jsonParser.parse(jsonString);
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
			}
		}
		return jsonObject;
	}

	public static void main(String[] args) {
		HttpResponse<String> response;
		List<String> sixMonthslow = new ArrayList<String>();
		// HttpRequest request;
		try {

			File file = new File("C:\\Users\\z003s8mc\\eclipse-workspace\\TryIt\\ReadStocks.txt");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			StringBuffer sb = new StringBuffer(); // constructs a string buffer with no characters

			String line;
			while ((line = br.readLine()) != null) {
				response = webServe(line, "6mo");
				JSONObject jsonObject1 = createJSONObject(response.body());
				JSONArray low = new JSONArray();

				if (!jsonObject1.isEmpty()) {

					try {

						JSONObject charts = (JSONObject) jsonObject1.get("chart");

						JSONArray results = (JSONArray) charts.get("result");

						JSONObject data = (JSONObject) results.get(0);

						JSONObject indicators = (JSONObject) data.get("indicators");

						JSONArray quote = (JSONArray) indicators.get("quote");

						// System.out.println(quote.size());

						JSONObject data1 = (JSONObject) quote.get(0);
						// System.out.println(data1.size());

						low = (JSONArray) data1.get("low");
						
						//System.out.println(low.size());
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}

					ListIterator<Double> namesIterator = low.listIterator();

					if (!low.isEmpty()) {

						ArrayList<Double> listDouble = new ArrayList<Double>();

						while (namesIterator.hasNext()) {
							listDouble.add(namesIterator.next());
						}

						// String requiredStr = getRequiredString(response.body());

						// List<Float> listFloat = convertStringToNav(requiredStr);
						
						listDouble.removeAll(Collections.singletonList(null));
						

						Double minValue = getMinValue(listDouble);

						Double lastDayLowClose = getCurrentCloseNAV(listDouble);

						Double percent = (double) 10f;
						String buyOrNot = calculatePercentage(minValue, lastDayLowClose, percent);

						if (buyOrNot.equals("Look")) {
							System.out.println("-------------------------------------------->" + line
									+ " - Watch - minValue " + minValue + " Last day low traded " + lastDayLowClose);
							sixMonthslow
									.add(line + "-minValue-" + minValue + "-Last day low traded-" + lastDayLowClose);
						}

						// sb.append(line); //appends line to string buffer
						// sb.append("\n"); //line feed
						System.out.println(line + " - processed");

					} else {
						System.out
								.println(line + " Dont have data ++++++++++++++++++++++++++++++++++++++++++++++++++ ");
					}
				}
			}
			fr.close();
			System.out.println(
					"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			Iterator<String> iter = sixMonthslow.iterator();
			while (iter.hasNext()) {
				System.out.println(iter.next());
			}

			System.out.println(
					"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(
					"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			Iterator<String> iter = sixMonthslow.iterator();
			while (iter.hasNext()) {
				System.out.println(iter.next());
			}

			System.out.println(
					"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		}

	}

}
