package recommend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class main {
    static List<List<String>> topTerms; // 클러스터별 Top terms 저장
    static List<List<String>> regions;  // 클러스터별 지역들 저장
    static List<String> recommendedRegions; // 사용자가 입력한 키워드에 대한 추천 관광지
    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        topTerms =  new ArrayList<>();
        regions = new ArrayList<>();
        recommendedRegions = new ArrayList<>();

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
}
