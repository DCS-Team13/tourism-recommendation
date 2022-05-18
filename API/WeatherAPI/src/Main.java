import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Main {
	public static void main(String[] args) throws IOException, ParseException {
		String[] loc = getLoc("서울특별시", "송파구", "삼전동");
//		System.out.println("x : " + loc[0] + ", y : " + loc[1]);
		
		String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
		// 홈페이지에서 받은 키
		String serviceKey =  "eKie0nhicws1wZAZv19qeRVk7ecNfF3bvvpuck3t1SDVnXRJbAQLDN4EQPymUe6jKEQgK2ItAoENaDYjk6Ckyg%3D%3D";
		String pageNo = "1";
		String numOfRows = "1000"; // 한 페이지 결과 수
		String type = "json"; // 타입
		String baseDate = "20220517"; // 원하는 날짜. 보통 전날 11시로 하는 것 같습니다.
		String baseTime = "2300"; // API 제공 시간
		String nx = loc[0]; // 위도
		String ny = loc[1]; // 경도 
		
		
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
		
		System.out.println(url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json");
		System.out.println("Response code: " + conn.getResponseCode());
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
		System.out.println(result);
		
		///////////////////////////////////////////////
		
		JSONParser parser = new JSONParser(); 
		JSONObject jsonObject = (JSONObject) parser.parse(result); 
		JSONObject response = (JSONObject) jsonObject.get("response"); 
		JSONObject body = (JSONObject) response.get("body"); 
		JSONObject items = (JSONObject) body.get("items");
		
		JSONArray item = (JSONArray) items.get("item");
		
		String category;
		JSONObject obj;
		String day="";
		String time="";
		String tmn = "";
		String tmx = "";
		for(int i = 0 ; i < item.size(); i++) {
			obj = (JSONObject) item.get(i);
			Object fcstValue = obj.get("fcstValue");
			Object fcstDate = obj.get("fcstDate");
			Object fcstTime = obj.get("fcstTime");
			category = (String)obj.get("category");
			
			if(category.equals("TMN")) {
				tmn = fcstValue.toString();
			}
			if(category.equals("TMX")) {
				tmx = fcstValue.toString();
			}
			if(!day.equals(fcstDate.toString()) && !day.equals("")) {
				System.out.println("\n\t" + day + "의 최저기온은 " + tmn + ", 최고기온은 " + tmx + "\n");
			}
			
			if(!day.equals(fcstDate.toString())) {
				day=fcstDate.toString();
			}
			if(!time.equals(fcstTime.toString())) {
				time=fcstTime.toString();
				System.out.println(day+"  "+time);
			}

			System.out.print("\tcategory : "+ category);
			System.out.print(", fcst_Value : "+ fcstValue);
			System.out.print(", 날짜 : "+ fcstDate);
			System.out.println(", 시간 : "+ fcstTime);
		}
		
		getLoc("서울특별시","송파구");

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
	public static String[] getLoc(String top, String mid, String leaf) throws IOException, ParseException{
		BufferedReader bf;
		HttpURLConnection conn;
		
		JSONParser parser;
		JSONArray jsonArr;
		JSONObject jsonObj;
		
		String result;
		
		URL topUrl;
		String topVal = top.replace(" ", "");
		String topCode = "";
		
		URL midUrl;
		String midVal = mid.replace(" ", "");
		String midCode = "";
		
		URL leafUrl;
		String leafVal = leaf.replace(" ", "");

		String[] loc = new String[2];
		
		topUrl = new URL("https://www.kma.go.kr/DFSROOT/POINT/DATA/top.json.txt");
		conn = (HttpURLConnection) topUrl.openConnection();
		bf = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		result = bf.readLine().toString();
		bf.close();
		
		parser = new JSONParser(); 
		jsonArr = (JSONArray) parser.parse(result);
		for(int i = 0; i<jsonArr.size();i++) {
			jsonObj = (JSONObject) jsonArr.get(i);
			if(jsonObj.get("value").equals(topVal)) {
				topCode = jsonObj.get("code").toString();
				break;
			}
		}
		midUrl = new URL("https://www.kma.go.kr/DFSROOT/POINT/DATA/mdl." + topCode + ".json.txt");
		conn = (HttpURLConnection) midUrl.openConnection();
		bf = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		result = bf.readLine().toString();
		bf.close();
		
		parser = new JSONParser(); 
		jsonArr = (JSONArray) parser.parse(result);
		for(int i = 0; i<jsonArr.size();i++) {
			jsonObj = (JSONObject) jsonArr.get(i);
			if(jsonObj.get("value").equals(midVal)) {
				midCode = jsonObj.get("code").toString();
				break;
			}
		}
		
		leafUrl = new URL("http://www.kma.go.kr/DFSROOT/POINT/DATA/leaf." + midCode + ".json.txt");
		conn = (HttpURLConnection) leafUrl.openConnection();
		bf = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		result = bf.readLine().toString();
		bf.close();
		
		parser = new JSONParser(); 
		jsonArr = (JSONArray) parser.parse(result);
		for(int i = 0; i<jsonArr.size();i++) {
			jsonObj = (JSONObject) jsonArr.get(i);
			if(jsonObj.get("value").equals(leafVal)) {
				midCode = jsonObj.get("code").toString();
				loc[0] = jsonObj.get("x").toString();
				loc[1] = jsonObj.get("y").toString();
//				System.out.println("x : " + loc[0] + ", y : " + loc[1]);
				break;
			}
		}
		return loc;
	}
	
	public static String[] getLoc(String top, String mid) throws IOException, ParseException{
		BufferedReader bf;
		HttpURLConnection conn;
		
		JSONParser parser;
		JSONArray jsonArr;
		JSONObject jsonObj;
		
		String result;
		
		URL topUrl;
		String topVal = top.replace(" ", "");
		String topCode = "";
		
		URL midUrl;
		String midVal = mid.replace(" ", "");
		String midCode = "";
		
		URL leafUrl;

		String[] loc = new String[2];
		
		topUrl = new URL("https://www.kma.go.kr/DFSROOT/POINT/DATA/top.json.txt");
		conn = (HttpURLConnection) topUrl.openConnection();
		bf = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		result = bf.readLine().toString();
		bf.close();
		
		parser = new JSONParser(); 
		jsonArr = (JSONArray) parser.parse(result);
		for(int i = 0; i<jsonArr.size();i++) {
			jsonObj = (JSONObject) jsonArr.get(i);
			if(jsonObj.get("value").equals(topVal)) {
				topCode = jsonObj.get("code").toString();
				break;
			}
		}
		midUrl = new URL("https://www.kma.go.kr/DFSROOT/POINT/DATA/mdl." + topCode + ".json.txt");
		conn = (HttpURLConnection) midUrl.openConnection();
		bf = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		result = bf.readLine().toString();
		bf.close();
		
		parser = new JSONParser(); 
		jsonArr = (JSONArray) parser.parse(result);
		for(int i = 0; i<jsonArr.size();i++) {
			jsonObj = (JSONObject) jsonArr.get(i);
			if(jsonObj.get("value").equals(midVal)) {
				midCode = jsonObj.get("code").toString();
				break;
			}
		}
		
		leafUrl = new URL("http://www.kma.go.kr/DFSROOT/POINT/DATA/leaf." + midCode + ".json.txt");
		conn = (HttpURLConnection) leafUrl.openConnection();
		bf = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		result = bf.readLine().toString();
		bf.close();
		
		parser = new JSONParser(); 
		jsonArr = (JSONArray) parser.parse(result);
		jsonObj = (JSONObject) jsonArr.get(jsonArr.size()/2);
		loc[0] = jsonObj.get("x").toString();
		loc[1] = jsonObj.get("y").toString();
//		System.out.println("동 : " + jsonObj.get("value") + ", x : " + loc[0] + ", y : " + loc[1]);
		
		return loc;
	}
}