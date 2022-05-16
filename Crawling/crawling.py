import requests
from bs4 import BeautifulSoup

url = 'https://search.naver.com/search.naver?where=influencer&sm=tab_jum&query='

query = input('keyword : ')

url = url + query.replace(' ', '+') + "+여행"

req = requests.get(url, format(query))
#print(req.text)

soup = BeautifulSoup(req.text, 'html.parser')
#print(soup)

print("URL : ", url.format(query))
print("* '{} 여행' search result".format(query))
print("---------------------------")

titleList = soup.find_all('a', {'class' : 'name_link'})
dscList = soup.find_all('a', {'class' : 'dsc_link'})

f = open(query + "_crawl.txt", 'w')

for number in range(len(titleList)) :
	print("title : ", titleList[number].text)
	f.write(titleList[number].text.replace(query, '').replace('여행', '')+'\n')
	print("dsc : ", dscList[number].text)
	f.write(dscList[number].text.replace(query, '').replace('여행', '')+'\n')
	print("href : ", titleList[number].get('href'))
	print("---------------------------")

f.close()
