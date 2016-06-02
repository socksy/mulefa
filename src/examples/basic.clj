(ns examples.basic
  (:require [mulefa.core :as m]))
(comment
;;attempt v0.0.1
  ; These define the selectors needed
  (m/def-sel language-picker "#searchLanguage")
  (m/def-sel search-input "#searchInput")
  (m/def-sel page-head "#firstHeading")
  ; or
  (m/def-sels [language-picker "#searchLanguage"
               search-input    "#searchInput"
               page-head       "#firstHeading"])

  (m/def-page home-page   "https://www.wiktionary.org")
  (m/def-page flocci-page "https://en.wiktionary.org/wiki/floccinaucinihilipilification")


  ; a fact is a current state of the world
  (-> (m/fact home-page
              [language-picker (m/val "English")
               search-input    (m/val "floccinaucinihilipilification")])

      ; an act modifies a fact and returns a new fact
      (m/act (m/submit search-input))

      ; returned fact should have these truth values
      (m/fact flocci-page
              [page-head (m/= "floccinaucinihilipilification")
               page-head (m/!= "noccinaucinihilipilification")]))

  (= returned-fact
     (m/fact )))

;; attempt v.0.0.2

(def start ^:m-state
  #{'(m/url     "https://www.wiktionary.org/")
    '(m/exists? "#searchInput")
    '(m/url?    "https://www.wiktionary.org/")
    '(m/value?  "#searchLanguage" "en")})


(def search-for-flocci ^:m-transaction
  ['(m/fill-in "#searchInput" "floccinaucinihilipilification")
   '(m/submit "#searchInput")])

(def flocci-state ^:m-state
  #{'(m/exists? "#firstHeading")
    '(m/value?  "#firstHeading" "floccinaucinihilipilification")})

(def search-for-happy ^:m-transaction
  ['(m/fill-in "#searchInput" "happy")
   '(m/submit "#searchInput")])

(def happy-state ^:m-state
  #{'(m/exists? "#firstHeading")
    '(m/value?  "#firstHeading" "happy")})

(def graph ^:m-graph
  #{'(start search-for-flocci flocci-state)
    '(start search-for-happy  happy-state)})

(defn -main
  []
  (m/run graph start))
