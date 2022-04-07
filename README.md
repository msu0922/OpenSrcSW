# 2022 오픈소스 SW 입문

2022-1학기 건국대학교 오픈소스 SW 입문

## 파일 구조

```bash
├── README.md
├── collection.xml
├── index.xml
├── index.post
├── data
│     ├── 떡.html
│     ├── 라면.html
│     ├── 아이스크림.html
│     ├── 초밥.html
│     └── 파스타.html
└── src
     └── scripts
            ├── kuir.java
            ├── makeCollection.java
            ├── makeKeyword.java
            ├── indexer.java 
            └── searcher.java

``` 

## 인코딩

**Encoding : UTF-8**

## 디렉토리 설명

**src/scripts** : .java 소스 파일이 저장되는 디렉토리

**data/** : html 파일들이 저장되어 있는 디렉토리

## 컴파일 명령어

### MAC

`javac -cp (외부 jar 파일 이름 1):(외부 jar 파일 이름 2):,,,, src/scripts/*.java -d bin (-encoding UTF8)`

ex) `javac -cp jars/jsoup-1.14.3.jar:jars/kkma-2.1.jar src/scripts/*.java -d bin -encoding UTF8`

### WINDOWS

`javac -cp (외부 jar 파일 이름 1):(외부 jar 파일 이름 2):,,,, src/scripts/*.java -d bin (-encoding UTF8)`

ex) `javac -cp jars/jsoup-1.14.3.jar:jars/kkma-2.1.jar src/scripts/*.java -d bin -encoding UTF8`

`javac -cp "(외부 jar 파일 이름 1);(외부 jar 파일 이름 2);,,,," src/scripts/*.java -d bin (-encoding UTF8)`

ex) `javac -cp "jars/jsoup-1.14.3.jar;jars/kkma-2.1.jar" src/scripts/*.java -d bin -encoding UTF8`

## 실행 명령어

### MAC

`java -cp (외부 jar 파일 이름 1):(외부 jar 파일 이름 2):,,,,:bin scripts.kuir (args 1) (args 2) ,,, (args n)`

ex) `java -cp ./jars/jsoup-1.14.3.jar:./jars/kkma-2.1.jar:bin scripts.kuir -c ./data/`

ex) `java -cp ./jars/jsoup-1.14.3.jar:./jars/kkma-2.1.jar:bin scripts.kuir -k ./collection.xml`

ex) `java -cp ./jars/jsoup-1.14.3.jar:./jars/kkma-2.1.jar:bin scripts.kuir -i ./index.xml`

ex) `java -cp ./jars/jsoup-1.14.3.jar:./jars/kkma-2.1.jar:bin scripts.kuir -s ./index.post -q "query 내용"`


### WINDOWS

`java -cp (외부 jar 파일 이름 1);(외부 jar 파일 이름 2);,,,,;bin scripts.kuir (args 1) (args 2) ,,, (args n)`

ex) `java -cp ./jars/jsoup-1.13.1.jar;./jars/kkma-2.1.jar;bin scripts.kuir -c ./data/`

ex) `java -cp ./jars/jsoup-1.13.1.jar;./jars/kkma-2.1.jar;bin scripts.kuir -k ./collection.xml`

ex) `java -cp ./jars/jsoup-1.13.1.jar;./jars/kkma-2.1.jar;bin scripts.kuir -i ./index.xml`

ex) `java -cp ./jars/jsoup-1.13.1.jar;./jars/kkma-2.1.jar;bin scripts.kuir -s ./index.post -q "query 내용"`