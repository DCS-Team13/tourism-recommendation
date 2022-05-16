<h1>Crawling using BeautifulSoup in Python</h1>

<h2>#Environment Setup</h2>
<div>$sudo apt-get install python3</div>
<div>$wget https://bootstrap.pypa.io/pip/3.5/get-pip.py</div>
<div>$python3 get-pip.py</div>
<div>$pip3 install requests</div>
<div>$pip3 install beautifulsoup4</div>
<div>$pip install --upgrade chardet</div>

<h2>#execution</h2>
<div>$python3 crawling.py</div>

<h2>#result</h2>
<div>{query}_crawl.txt</div>

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
    <td></td>
    <td></td>
    <td></td>
  </tr>
</table>
</body>
