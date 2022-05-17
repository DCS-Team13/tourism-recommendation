from konlpy.tag import Mecab

m = Mecab("C:\\Mecab\\mecab-ko-dic")

# TEST
print(m.pos("안녕하세요분산시스템및컴퓨팅프로젝트입니다."))

