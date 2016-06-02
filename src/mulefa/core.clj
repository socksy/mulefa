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
    (fn []
      (apply f args))))

(defn- select
  [driver selector]
  (fn []
    (first (t/find-element driver {:css selector}))))

;; state checking
(defn url?
  [driver string]
  (fn []
    (= (t/current-url driver) string)))


(def exists? (pass-on t/exists?))

(def value? (pass-on t/visible?))

;; transitions
(def url (pass-on t/to))

(defn fill-in
  [driver sel val]
  (fn []
    (t/input-text driver (select sel) val)))

(defn submit
  [driver sel]
  (fn []
    (t/submit driver (select sel))))

;; internal graph running
(defn- eval-with-driver
  [driver [function & args :as test]]
  ((concat (list function driver) args)))

(defn- check-state
  [driver state]
  (let [_         (println "checking state")
        new-state (filter #(not= (first %) 'url) state)
        url-to-go (first (filter #(= (first %) 'url) state))]
    (when url-to-go (eval-with-driver driver url-to-go))
    (map (partial eval-with-driver driver) new-state)))

(defn- run-transition
  [driver state transition]
  ;; TODO get check-state response and fail out properly
  ;; i.e. no more (do)
  (do
    (println "running-transition")
    (check-state driver state)
    (map (partial eval-with-driver driver) transition)))

(defn- run-route
  ([arg-list]
   (println "run route1")
   (run-route arg-list (t/new-driver {:browser :firefox})))
  ([[state transition & the-rest] driver]
   (println "run route2")
   (run-transition driver state transition)
   (when the-rest
     (recur the-rest driver))))

(defn- get-routes
  [graph start-state]
  ;;TODO
  (first graph))

(defn run
  [graph start-state]
  (->> (get-routes graph start-state)
       (map run-route)))
