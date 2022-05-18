from konlpy.tag import Mecab
from collections import Counter
import matplotlib.pyplot as plt
from matplotlib import font_manager, rc
import numpy as np

m = Mecab("C:\\Mecab\\mecab-ko-dic")

# 각 지역별 키워드 저장할 2차원 딕셔너리
region_keywords = dict()
# 품사 태그
tags = ['NNG', 'NNP']
# 지역
regions = ["강원", "경기", "충청", "전라", "경상"]


# 단어 형태소 분석 - 빈도수 체크
def morpho_analysis(region_name, filePath):
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
        if (key[0] in regions) | (region_name in key[0]):
            keywords.remove(key)

    # 의미없는 단어 거르기 - 추후 결정
    del_words = ["여행", "곳", "길", "호텔", "사람", "생각", "사진", "집", "시간", "때", "이용", "텍스트", "층", "공간", "원", "뤼", "다음", "안"]
    for key in keywords:
        if key[0] in del_words:
            keywords.remove(key)

    # 빈도수 체크
    count_items = Counter(keywords)
    print(count_items)

    # 빈도수 그래프

    font_path = "C:/Windows/Fonts/NGULIM.TTF"
    font = font_manager.FontProperties(fname=font_path).get_name()
    rc('font', family=font)

    x = np.arange(20)
    temp = list(count_items.items())
    temp.sort(key=lambda x: x[1], reverse=True)
    print(temp)

    # 상위 20개만 보여주기
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

    plt.show()

    # region_keywords에 추가
    region_keywords[region_name] = count_items

# TF-IDF 벡터화
def tf_idf():
    return 0

morpho_analysis("대전", "../Crawling/대전 여행_crawl.txt")