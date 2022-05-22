import requests
import re
from bs4 import BeautifulSoup
from urllib.parse import quote

#iframe 제거 후 blog.naver.com 붙이기
def delete_iframe(url):
    res = requests.get(url)
    res.raise_for_status() # 문제시 프로그램 종료
    soup = BeautifulSoup(res.text, "lxml") 

    src_url = "https://blog.naver.com/" + soup.iframe["src"]
    
    return src_url

# 본문 스크래핑
def text_scraping(url):
    res = requests.get(url)
    res.raise_for_status() # 문제시 프로그램 종료
    soup = BeautifulSoup(res.text, "lxml") 

    if soup.find("div", attrs={"class":"se-main-container"}):
        text = soup.find("div", attrs={"class":"se-main-container"}).get_text()
        text = text.replace("\n","") #공백 제거
        return text
    elif soup.find("div", attrs={"id":"postViewArea"}):
        text = soup.find("div", attrs={"id":"postViewArea"}).get_text()
        text = text.replace("\n","") 
        return text
    else:
        return 0

area = []
query = []
add_word = ["여행", "볼거리", "유명", "추천"]

repet = int(input('크롤링 진행할 키워드 수 : '))

for n in range(repet) :
    area.append(input('area : '))
    query.append(input('query : '))

for n in range(repet) :
    url = 'https://search.naver.com/search.naver?where=blog&query=' + quote(query[n].replace(' ', '+'))

    f = open("loc.txt", 'a', encoding='UTF-8')
    f.write("./" + area[n] + " " + query[n] + "_crawl.txt\n")
    f.close()

    f = open(area[n] + " " + query[n] + "_crawl.txt", 'w', encoding='UTF-8')

    all_post_link = []

    for i in range(len(add_word)) :
        add_url = url + "+" + add_word[i]

        res = requests.get(add_url)
        res.raise_for_status() # 문제시 프로그램 종료
        soup = BeautifulSoup(res.text, "lxml") 
        #print(soup)
        posts = soup.find_all("div", attrs={"class":"total_area"})
        #print(posts)

        for j in range(len(posts)) :
            post_title = posts[j].find("a", attrs={"class":"api_txt_lines total_tit"}).get_text()
            print("제목 :",post_title)
            post_link = posts[j].find("a", attrs={"class":"api_txt_lines total_tit"})['href']
            print("link :", post_link)

            if(all_post_link.count(post_link) == 0) :
                all_post_link.append(post_link)
                blog_p = re.compile("blog.naver.com")
                blog_m = blog_p.search(post_link)

                if blog_m:
                    blog_text = text_scraping(delete_iframe(post_link))
                    if blog_text:
                        f.write(blog_text.replace(add_word[i], "").replace(area[n], "").replace(query[n], "") + '\n')
                        print(str(n*len(add_word)*len(posts)+i*len(posts)+j+1)+"/"+str(repet*len(add_word)*len(posts))+" complete...")
                    else:
                        print(str(n*len(add_word)*len(posts)+i*len(posts)+j+1)+"/"+str(repet*len(add_word)*len(posts))+" .....fail")

            else:
                print(str(n*len(add_word)*len(posts)+i*len(posts)+j+1)+"/"+str(repet*len(add_word)*len(posts))+" ..overlap..")
            print("-"*50)

    f.close()
