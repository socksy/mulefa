(ns examples.basic
  (:require [mulefa.core :as m]))

;; attempt v.0.0.2

(def start ^:m-state
  #{;'(m/url     "https://www.wiktionary.org/")
    (m/exists? "#searchInput")
    (m/url?    "https://www.wiktionary.org/")
    (m/value?  "#searchLanguage" "en")})


(def search-for-flocci ^:m-transaction
  [(m/fill-in "#searchInput" "floccinaucinihilipilification")
   (m/submit "#searchInput")])

(def flocci-state ^:m-state
  #{(m/exists? "#firstHeading")
    (m/value?  "#firstHeading" "floccinaucinihilipilification")})

(def search-for-happy ^:m-transaction
  [(m/fill-in "#searchInput" "happy")
   (m/submit "#searchInput")])

(def happy-state ^:m-state
  #{(m/exists? "#firstHeading")
    (m/value?  "#firstHeading" "happy")})

(def graph ^:m-graph
  #{'(start search-for-flocci flocci-state)
    '(start search-for-happy  happy-state)})

(defn -main
  []
  (m/run graph start))
