(ns mulefa.core-test
  (:require [clojure.test :refer :all]
            [mulefa.core :as m]))

(def set-up ^:m-transaction
  [(m/navigate "https://www.wiktionary.org/")])

(def initial ^:m-state
  #{(m/url?    "https://www.wiktionary.org/")
    (m/exists? "#searchInput")
    (m/value?  "#searchLanguage" "en")})

(def search-for-flocci ^:m-transaction
  [(m/fill-in "#searchInput" "floccinaucinihilipilification")
   (m/submit "#searchInput")])

(def flocci-state ^:m-state
  #{(m/exists? "#firstHeading")
    (m/value?  "#firstHeading" "floccinaucinihilipilification")})

(def graph ^:m-graph
  #{[set-up initial search-for-flocci flocci-state]})

(deftest test-run
  (testing "traversal of a minimal graph"
    (is (nil? (m/run graph set-up)))))
