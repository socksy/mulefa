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

;; state checking

(defn url?
  [string]
  (fn [driver]
    (= (t/current-url driver) string)))

(def exists? (pass-on t/exists?))

(defn value?
  [sel expected-val]
  (fn [driver]
    (= (t/value driver sel) expected-val)))

;; transitions

(def navigate (pass-on t/to))

(defn fill-in
  [sel val]
  (fn [driver]
    (t/input-text driver sel val)))

(defn submit
  [sel]
  (fn [driver]
    (t/submit driver sel)))

;; internal graph running

(defn- check-state
  [driver state]
  (println "checking state")
  (doall (map #(% driver) state)))

(defn- run-transition
  [driver state transition]
  ;; TODO get check-state response and fail out properly
  ;; i.e. no more (do)
  (do
    (println "running-transition")
    (check-state driver state)
    (doall (map #(% driver) transition))))

(defn- run-route
  [driver [state transition & the-rest]]
  (println "run route")
  (run-transition driver state transition)
  (when the-rest
    (recur driver the-rest)))

(defn- get-routes
  [graph start-state]
  ;;TODO actually get possible routes from the graph
  [(first graph)])

(defn run
  ([graph start-state starting-url]
   (run (t/new-driver {:browser :chrome}) graph start-state starting-url))
  ([driver graph start-state starting-url]
   (->> (get-routes graph start-state)
        (run! (fn [route]
                (t/to driver starting-url)
                (run-route driver route))))
   (t/close driver)))
