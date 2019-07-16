(defproject fluree/alphabase "3.0.1"
  :description "Clojure(script) library to encode binary data with alphabet base strings."
  :url "https://github.com/fluree/alphabase"
  :license {:name "Public Domain"
            :url "http://unlicense.org/"}

  :deploy-branches ["master"]

  :aliases
  {"clj:test" ["do" ["check"] ["test"]]
   "cljs:check" ["with-profile" "+doo" "cljsbuild" "once"]
   "cljs:repl" ["run" "-m" "clojure.main" "dev/cljs_repl.clj"]
   "cljs:test" ["doo" "rhino" "test" "once"]}

  :plugins
  [[lein-cljsbuild "1.1.7"]
   [lein-doo "0.1.11"]]

  :cljsbuild
  {:builds [{:id "test"
             :source-paths ["src" "test"]
             :compiler {:optimizations :whitespace
                        :output-dir "target/cljs/out"
                        :output-to "target/cljs/tests.js"
                        :main alphabase.test-runner}}]}

  :codox
  {:metadata {:doc/format :markdown}
   :source-uri "https://github.com/greglook/alphabase/blob/master/{filepath}#L{line}"
   :output-path "target/doc/api"}

  :profiles
  {:dev
   {:dependencies
    [[criterium "0.4.4"]
     [org.clojure/clojure "1.10.1"]
     [org.clojure/clojurescript "1.10.520"]]}

   :doo
   {:dependencies
    [[doo "0.1.11"]]}})
