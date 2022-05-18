# 형태소 분석기 선택 기준
- 연산 속도
- 분석 품질 (띄어쓰기유무, 오탈자에도 어느 정도 품질 요구)
- 언어

# KOMORAN vs mecab
||KOMORAN|mecab|
|---|---|---|
|연산 속도|로딩시간: 0.9542 / 형태소 분석 시간: mecab에 비해 느림(다른 라이브러리에 비해서는 빠른 편)|로딩시간: 0.0004 / 형태소 분석 시간: 압도적으로 빠름|
|분석 품질|오탈자가 있고 띄어쓰기가 없는 문장에서도 분석 품질 좋음|KOMORAN에 비해 떨어지는 품질, 그래도 품질 나쁘지 않은 편|
|언어|자바|파이썬|

> ## 선택 - mecab
> ### 이유
> - 빅데이터 처리 시 품질을 조금 포기하는 대신에 연산 속도가 빠른 것으로 하여 성능을 높이고자 함 (mecab을 선택한다고 키워드 추출 품질이 크게 나빠지지 않음)
> - 크롤링 코드와 연동이 되어야 함 - 파이썬

# 트러블 슈팅 기록
### 파이썬 matplotlib로 그래프 나타내면 한글 깨져서 나오는 문제 
- 사용하고자 하는 한글 폰트의 경로를 알려준 후 그 폰트로 텍스트가 쓰여지게 해야한다. 
  - 윈도우 PC에서 폰트는 C:\Windows\Fonts에서 원하는 폰트의 영문명을 찾아 해당 경로 뒤에 붙인 뒤 다음 코드 추가
> from matplotlib import font_manager, rc
> 
> font_path = "C:/Windows/Fonts/NGULIM.TTF"
> 
> font = font_manager.FontProperties(fname=font_path).get_name()
> 
> rc('font', family=font)

참고) 
- 형태소 분석기 비교 https://iostream.tistory.com/144
- Mecab 설치 https://lsjsj92.tistory.com/612
- TF-IDF https://wikidocs.net/31698
