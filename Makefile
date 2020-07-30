.PHONY: cljtest cljstest test jar js-deps install deploy clean

target/alphabase.jar: pom.xml src/deps.cljs deps.edn src/**/*
	clojure -A:jar

pom.xml: deps.edn
	clojure -Spom

src/deps.cljs: package.json
	clojure -A:js-deps

js-deps: src/deps.cljs

jar: target/alphabase.jar

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
	rm -rf cljs-test-runner-out
	rm -rf .cpcache