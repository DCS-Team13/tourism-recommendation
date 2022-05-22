from konlpy.tag import Mecab
from collections import Counter
import matplotlib.pyplot as plt
from matplotlib import font_manager, rc
import numpy as np
from sklearn.feature_extraction.text import TfidfVectorizer
import pandas as pd
from math import log

m = Mecab("C:\\Mecab\\mecab-ko-dic")

# 각 지역별 키워드 저장할 딕셔너리
region_keywords = dict()
# 품사 태그
tags = ['NNG', 'NNP']
# 지역
regions = ["강원", "경기", "충청", "전라", "경상"]


# 단어 형태소 분석 - 빈도수 체크
def morpho_analysis(filePath, vocas):

    region_name = filePath.split(" ")[0].split("/")[2]

    with open(filePath, 'r', encoding="UTF-8") as f:
        text = f.read().replace('\n', '')
    words = m.pos(text)
    keywords = []

    # 일반명사, 고유명사만 추출
    for word in words:
        if word[1] in tags:
            keywords.append(word)

    # 지역명 제거
    for key in keywords:
        if (key[0] in regions):
            keywords.remove(key)

    # 의미없는 단어 거르기 - 추후 결정
    del_words = ["여행", "곳", "길", "호텔", "사람", "생각", "사진", "집", "짐", "보관", "나머지", "구분", "시간", "때", "이용", "텍스트", "층", "공간", "원", "뤼", "다음", "안"]
    for key in keywords:
        if key[0] in del_words:
            keywords.remove(key)

    # 빈도수 체크
    count_items = Counter(keywords)
    # print(count_items)
    # print(len(count_items))

    count = 1
    for item in count_items.keys():
        vocas.add(item[0])
        count += 1
        if count == 20:
            break

    # 빈도수 그래프 - 확인용, 필요없을 시 주석처리 가능
'''
    font_path = "C:/Windows/Fonts/NGULIM.TTF"
    font = font_manager.FontProperties(fname=font_path).get_name()
    rc('font', family=font)

    x = np.arange(20)
    temp = list(count_items.items())
    temp.sort(key=lambda x: x[1], reverse=True)
    print(temp)

    # 상위 20개 시각화
    keys = []
    values = []
    count = 0
    for data in temp:
        keys.append(data[0][0])
        values.append(data[1])
        count += 1
        if count == 20:
            break

    plt.bar(x, values)
    plt.xticks(x, keys)

    plt.show()'''


def tf(t, d):
    return d.count(t)

def df(t):
    df = 0
    for doc in docs:
        df += t in doc
    return df

def idf(t):
    return log(n/(df(t)+5))

def tf_idf(t, d):
    return tf(t, d) * idf(t)

docs = list()
# loc.txt에 적힌 크롤링데이터 갯수
count = 0
with open("../Crawling/loc.txt", 'r', encoding="UTF-8") as f:
    locs = f.readlines()
    for loc in locs:
        with open(loc.replace('\n', ''), 'r', encoding="UTF-8") as f:
            textList = f.readlines()
            text = ""
            for str in textList:
                text += str.replace('\n', '').replace('\u200b', '')
            docs.append(text)
            count += 1
            print(loc, ", ", count)
'''
vocas = set()
for loc in locs:
    morpho_analysis(loc.replace('\n',''), vocas)
vocas = list(vocas)
vocas.sort()
print("voca 정렬 완료 : ", len(vocas))
# voca = list(set(w for doc in docs for w in doc.split()))
# voca.sort()
# print(voca)

n = len(docs)  # 문서 개수
vocaLen = len(vocas)

# TF
tf_list = [[tf(vocas[j], docs[i]) for j in range(vocaLen)] for i in range(n)]
tf_res = pd.DataFrame(tf_list, columns=vocas)
tf_res.plot()

# IDF
idf_list = [idf(vocas[j]) for j in range(vocaLen)]
idf_res = pd.DataFrame(idf_list, index=vocas, columns=["IDF"])

# TF-IDF
tf_idf_list = [[tf_idf(vocas[j], docs[i]) for j in range(vocaLen)] for i in range(n)]
tfidf_res = pd.DataFrame(tf_idf_list, columns=vocas)
tfidf_res.plot()
# 그래프 - 한글 깨짐 방지
font_path = "C:/Windows/Fonts/NGULIM.TTF"
font = font_manager.FontProperties(fname=font_path).get_name()
rc('font', family=font)

plt.show()
print("Done.")'''

def tfidf(docs):
    tfidfv = TfidfVectorizer().fit(docs)
    print(tfidfv.transform(docs).toarray())
    print(len(tfidfv.vocabulary_))

tfidf(docs)
print("Done.")

# morpho_analysis("../Crawling/대전 여행_crawl.txt")