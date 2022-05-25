import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Main {
	public static void main(String[] args) throws IOException, ParseException{
		sortFile("output");

//			항목값	항목명		단위			압축bit수
		
//			POP		강수확률		%			8
//			PTY		강수형태		코드값		4
//			PCP		1시간 강수량	범주 (1 mm)	8
//			REH		습도			%			8
//			SNO		1시간 신적설	범주(1 cm)	8
//			SKY		하늘상태		코드값		4
//			TMP		1시간 기온	℃				10
//			TMN		일 최저기온	℃				10
//			TMX		일 최고기온	℃				10
//			UUU		풍속(동서성분)	m/s			12
//			VVV		풍속(남북성분)	m/s			12
//			WAV		파고			M			8
//			VEC		풍향			deg			10
//			WSD		풍속			m/s			10
		
	}
	public static class Location {
		String x;
		String y;
		Location(String x, String y){
			this.x = x;
			this.y = y;
		}
	}
	
	public static class Tourism{
		String name;
		int weather;
		Tourism(String name, int weather) {
			this.name = name;
			this.weather = weather;
		}
	}
	public static void sortFile(String fileName) throws IOException, ParseException {
		File loc = new File("./loc.txt");
		Map<String, Location> map = new HashMap<String, Location>();
		ArrayList<Tourism> list = new ArrayList<>();
		BufferedReader bf = new BufferedReader(new FileReader(loc));
		String str;
		StringTokenizer st;
		while((str = bf.readLine())!= null) {
			st = new StringTokenizer(str,"\t");
			String name = st.nextToken();
			String y= st.nextToken();
			String x = st.nextToken();
			map.put(name ,new Location(x, y));
		}
		File output = new File("./output.txt");
		Tourism tour;
		bf = new BufferedReader(new FileReader(output));
		while((str = bf.readLine())!= null) {
			tour = new Tourism(str, getWeather("20220527", map.get(str)));
			list.add(tour);
		}
		Collections.sort(list, new Comparator<Tourism>() {

			@Override
			public int compare(Tourism t1, Tourism t2) {
				// TODO Auto-generated method stub
				return t1.weather - t2.weather;
			}
		});
		for(Tourism tourism : list) {
			System.out.println(tourism.name + "\t" + tourism.weather);
		}
	}
	
	public static int getWeather(String date, Location location) throws IOException, ParseException {
		Calendar c1 = new GregorianCalendar();

		c1.add(Calendar.DATE, -1);
		
//		System.out.println("x : " + loc[0] + ", y : " + loc[1]);
		
		String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
		// 홈페이지에서 받은 키
		String serviceKey =  "eKie0nhicws1wZAZv19qeRVk7ecNfF3bvvpuck3t1SDVnXRJbAQLDN4EQPymUe6jKEQgK2ItAoENaDYjk6Ckyg%3D%3D";
		String pageNo = "1";
		String numOfRows = "1000"; // 한 페이지 결과 수
		String type = "json"; // 타입
		String baseDate = new SimpleDateFormat("yyyyMMdd").format(c1.getTime()); // 원하는 날짜. 보통 전날 11시로 하는 것 같습니다.
		String baseTime = "2300"; // API 제공 시간
		String nx = location.x; // 위도
		String ny = location.y; // 경도 
		
		
		StringBuilder urlBuilder = new StringBuilder(apiUrl);
		urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
		urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(pageNo, "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "="+ URLEncoder.encode(numOfRows, "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "="+ URLEncoder.encode(baseDate, "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "="+ URLEncoder.encode(baseTime, "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")+"&");

		URL url = new URL(urlBuilder.toString());
		
//		System.out.println(url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json");
//		System.out.println("Response code: " + conn.getResponseCode());
		BufferedReader rd;
		if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
			rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();
		conn.disconnect();
		String result= sb.toString();
//		System.out.println(result);
		
		///////////////////////////////////////////////
		
		JSONParser parser = new JSONParser(); 
		JSONObject jsonObject = (JSONObject) parser.parse(result); 
		JSONObject response = (JSONObject) jsonObject.get("response"); 
		JSONObject body = (JSONObject) response.get("body"); 
		JSONObject items = (JSONObject) body.get("items");
		
		JSONArray item = (JSONArray) items.get("item");
		
		String category;
		JSONObject obj;
		String day=date;
		String time="";
		int sky = 0;
		for(int i = 0 ; i < item.size(); i++) {
			obj = (JSONObject) item.get(i);
			Object fcstValue = obj.get("fcstValue");
			Object fcstDate = obj.get("fcstDate");
			Object fcstTime = obj.get("fcstTime");
			category = (String)obj.get("category");
			
			if(!day.equals(fcstDate.toString())) {
				continue;
			}
			
			if(!time.equals(fcstTime.toString())) {
				time=fcstTime.toString();
//				System.out.println(day+"  "+time);
			}
			if(category.equals("SKY")) {
				sky += Integer.parseInt(fcstValue.toString());
			}

//			System.out.print("\tcategory : "+ category);
//			System.out.print(", fcst_Value : "+ fcstValue);
//			System.out.print(", 날짜 : "+ fcstDate);
//			System.out.println(", 시간 : "+ fcstTime);
		}
//		System.out.println(sky);
		return sky;
	}
}