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

query = input('area : ')
url = 'https://search.naver.com/search.naver?where=blog&query=' + quote(query.replace(' ', '+'))

res = requests.get(url)
res.raise_for_status() # 문제시 프로그램 종료
soup = BeautifulSoup(res.text, "lxml") 
#print(soup)
posts = soup.find_all("div", attrs={"class":"total_area"})
#print(posts)
f = open(query + "_crawl.txt", 'w')

for i in range(len(posts)) :
    post_title = posts[i].find("a", attrs={"class":"api_txt_lines total_tit"}).get_text()
    print("제목 :",post_title)
    post_link = posts[i].find("a", attrs={"class":"api_txt_lines total_tit"})['href']
    print("link :", post_link)

    blog_p = re.compile("blog.naver.com")
    blog_m = blog_p.search(post_link)
    
    if blog_m:
        blog_text = text_scraping(delete_iframe(post_link))
        if blog_text:
            f.write(blog_text + '\n')
            print(str(i+1)+"/"+str(len(posts))+" complete...")
        else:
            print(str(i+1)+"/"+str(len(posts))+" ...fail")
        print("-"*50)

f.close()
