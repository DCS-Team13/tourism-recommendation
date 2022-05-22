<h1>Crawling using BeautifulSoup in Python</h1>

<h2>#Environment Setup</h2>
<div>$wget https://bootstrap.pypa.io/pip/3.5/get-pip.py</div>
<div>$python3 get-pip.py</div>
<div>$pip install ~</div>

<h2>#execution</h2>
<div>$python3 crawling.py</div>

<h2>#result</h2>
<div>{area} {query}_crawl.txt</div>
<div>loc.txt : _crawl.txt 파일 위치 저장 (형태소변환 시 일일히 작성하는 작업 자동화)</div>

<h2>🛠Troubleshooting</h2>
<body>
  <table>
  <tr>
    <th>문제</th>
    <th>원인</th>
    <th>해결방법</th>
  </tr>
  <tr>
    <td>일부 블로그에서 스크래핑이 진행되지 않음</td>
    <td>네이버블로그 에디터가 2가지 존재</td>
    <td>기존에 div class='se-main-container'만 불러왔으나<br>div id='postViewArea'도 추가</td>
  </tr>
  <tr>
    <td>한번 크롤링에 최대 30개의 블로그만 탐색</td>
    <td>블로그 검색은 페이지단위가 아님</td>
    <td>추가 단어를 적용하여 여러번 검색 진행</td>
  </tr>
</table>
</body>
