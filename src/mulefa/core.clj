(ns mulefa.core
  (:require [clj-webdriver.taxi :as t]))

(defn pass-on
  "So this turns a function into one that can be called to create a new function

  (kinda like a thunk)

  so:
  user=> (def foo (pass-on println))
  #'user/foo
  user=> (foo \"hello\")
  #function[user/pass-on/fn-24335/fn-24336]
  user=> ((foo \"test\"))
  test
  nil"
  [f]
  (fn [& args]
    (fn [driver]
      (apply f (concat [driver] args)))))

(defn- select
  [selector]
  (fn [driver]
    (first (t/find-element driver {:css selector}))))

;; state checking
(defn url?
  [string]
  (fn [driver]
    (= (t/current-url driver) string)))

(def exists? (pass-on t/exists?))

(def value? (pass-on t/visible?))

;; transitions
(def url (pass-on t/to))

(defn fill-in
  [sel val]
  (fn [driver]
    (t/input-text driver (select sel) val)))

(defn submit
  [sel]
  (fn [driver]
    (t/submit driver (select sel))))

;; internal graph running

(defn- check-state
  [driver state]
  (let [_         (println "checking state")
        ;new-state (filter #(not= (first %) 'url) state)
        url-to-go  "https://www.wiktionary.org/" #_(first (filter #(= (first %) 'url) state))]
    (when url-to-go ((url url-to-go) driver))
    (map #(% driver) state)))

(defn- run-transition
  [driver state transition]
  ;; TODO get check-state response and fail out properly
  ;; i.e. no more (do)
  (do
    (println "running-transition")
    (check-state driver state)
    (doall (map #(% driver) transition))))

(defn run-route
  ([arg-list]
   (println "run route1")
   (run-route (t/new-driver {:browser :chrome}) arg-list))
  ([driver [state transition & the-rest]]
   (println "run route2")
   (run-transition driver state transition)
   (when the-rest
     (recur driver the-rest))))

(defn- get-routes
  [graph start-state]
  ;;TODO actually get possible paths from the graph
  graph)

(defn run
  [graph start-state]
  (->> (get-routes graph start-state)
       (run! run-route)))
