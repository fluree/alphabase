.PHONY: cljtest cljstest test uberjar js-deps install deploy clean

target/alphabase.jar: pom.xml src/deps.cljs deps.edn src/**/*
	clojure -A:uberjar

pom.xml: deps.edn
	clojure -Spom

src/deps.cljs: package.json
	clojure -A:js-deps

js-deps: src/deps.cljs

uberjar: target/alphabase.jar

cljtest:
	clojure -A:test:cljrunner

cljstest:
	clojure -A:test:cljsrunner

test: cljtest cljstest

install: target/alphabase.jar
	clojure -A:install

deploy: target/alphabase.jar
	clojure -A:deploy

clean:
	rm -rf out
	rm -rf target