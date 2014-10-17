(defproject ash-mcc/friend-jaas "0.1.1"
  :description "Helps Chas Emerick's Friend library to use JAAS."
  :url "https://github.com/ash-mcc/friend-jaas"
  :author "Ashley McClenaghan"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.logging "0.3.1"]
                 [com.cemerick/friend "0.2.1"]]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[http-kit "2.1.13"]
                                  [compojure "1.1.5"]
                                  [hiccup "1.0.1"]]}})