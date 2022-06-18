

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
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class main {
    static List<List<String>> topTerms; // 클러스터별 Top terms 저장
    static List<List<String>> regions;  // 클러스터별 지역들 저장
    static List<String> recommendedRegions; // 사용자가 입력한 키워드에 대한 추천 관광지
    public static void main(String[] args) throws IOException, ParseException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        topTerms =  new ArrayList<>();
        regions = new ArrayList<>();
        recommendedRegions = new ArrayList<>();

		Calendar c0 = new GregorianCalendar();
		
        for(int i=0 ; i<40 ; i++) {
            topTerms.add(new ArrayList<>());
            regions.add(new ArrayList<>());
        }
        saveCluster();
        while(true) {
            System.out.println("Enter the keyword that you like, or if you want to exit, enter the \"exit\": ");

            String answer = br.readLine();

            if(answer.equals("exit")) {
                System.out.println("Bye!");
                break;
            }

            for(int i=0 ; i<40 ; i++){
                for(String term : topTerms.get(i)) {
                    if(term.contains(answer)){
                        recommendedRegions.addAll(regions.get(i));
                        break;
                    }
                }
            }

            System.out.println("\"" + answer + "\"와(과) 연관된 추천 관광지입니다.\n");
            int len = recommendedRegions.size();
            for(int i=0 ; i<len ; i++){
                System.out.println((i+1) + ") " + recommendedRegions.get(i));
            }
            System.out.println("\n[   날짜를 선택해주세요  ]");
            System.out.println("[ 1)오늘 2)내일 3)모레 ]");
            
            int date = Integer.parseInt(br.readLine()) -1;
    		c0.add(Calendar.DATE, date);
            sortFile(new SimpleDateFormat("yyyyMMdd").format(c0.getTime()));
            recommendedRegions.clear();
        }
        br.close();
    }

    public static void saveCluster() throws IOException{
        String path = main.class.getResource("").getPath();
        BufferedReader br = new BufferedReader(new FileReader(path + "tour-clusters.txt"));
        boolean eof = false;

        String str = br.readLine();
        int idx = 0;
        while(idx < 40) {
            // 공백, 탭 제거
            str.replaceAll("\t", "");
            str.replaceAll(" ", "");

            if(str.startsWith(":VL-")) {
                str = br.readLine();
            }

            // Top Terms
            if(str.contains("Top Terms")){
                str = br.readLine();
                while(!str.contains("Weight")){
                    String term = str.split("=>")[0];
                    topTerms.get(idx).add(term);
                    str = br.readLine();
                }
            }

            // Region_crawl.txt
            if(str.contains("Weight")) {
                str = br.readLine();
                while(!str.contains(":VL-")) {
                    String region = (str.split("/")[1]).split("_crawl.txt")[0];
                    regions.get(idx).add(region);
                    str = br.readLine();
                    if(str == null) {
                        eof = true;
                        break;
                    }
                }

            }

            idx++;

            if(eof)
                break;
        }
    }
    
	public static void sortFile(String date) throws IOException, ParseException {
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
		Tourism tour;
		System.out.print("\nPlease wait");
		for(String region : recommendedRegions) {
			System.out.print(".");
			tour = new Tourism(region, getWeather(date, map.get(region.split(" ")[0])));
			list.add(tour);		
		}
		System.out.println('\n');
		System.out.println("[    Tomorrow's Weather    ]");
		System.out.println("[ 1:Sunny, 3:Cloudy, 4:Bad ]\n");
		Collections.sort(list, new Comparator<Tourism>() {

			@Override
			public int compare(Tourism t1, Tourism t2) {
				return (int)(t1.weather*100 - t2.weather*100);
			}
		});
		int i = 1;
		for(Tourism tourism : list) {
			if(tourism.weather == 0) {
				System.out.println(i + ")" +tourism.name + "\t" + "Rain");
			}
			else {
				System.out.println(i + ")" +tourism.name + "\t" + Math.round(tourism.weather*100)/100.0);
			}
			i++;
		}
		System.out.println();
	}
	
	public static double getWeather(String date, Location location) throws IOException, ParseException {
		Calendar c1 = new GregorianCalendar();

		c1.add(Calendar.DATE, -1);
		
		
		String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
		// 홈페이지에서 받은 키
		String serviceKey =  "eKie0nhicws1wZAZv19qeRVk7ecNfF3bvvpuck3t1SDVnXRJbAQLDN4EQPymUe6jKEQgK2ItAoENaDYjk6Ckyg%3D%3D";
		String pageNo = "1";
		String numOfRows = "1000"; // 한 페이지 결과 수
		String type = "json"; // 타입
		String baseDate = new SimpleDateFormat("yyyyMMdd").format(c1.getTime()); // 원하는 날짜. 보통 전날 11
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
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json");
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
		double sky = 0;
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
			}
			if(category.equals("PCP")) {
				if(!(fcstValue.toString().equals("강수없음")||fcstValue.toString().equals("1.0mm"))) {
					return 0;
				}
			}
			
			if(category.equals("SKY")) {
				sky += Integer.parseInt(fcstValue.toString());
			}
		}
		return sky/24;
	}
}
