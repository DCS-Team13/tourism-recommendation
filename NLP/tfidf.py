from sklearn.feature_extraction.text import TfidfVectorizer

# 각 지역별 키워드 저장할 딕셔너리
region_keywords = dict()

# 문서들
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


def tfidf(docs):
    tfidfv = TfidfVectorizer().fit(docs)
    print(tfidfv.transform(docs).toarray())
    print(len(tfidfv.vocabulary_))

tfidf(docs)
print("Done.")