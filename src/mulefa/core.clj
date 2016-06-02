(ns mulefa.core)

;; state checking
(defn url?
  [situation string]
  )


;; internal graph running
(defn- eval-test-with-state
  [situation [function & args :as test]]
  (eval (concat (list function situation) args)))

(defn- check-state
  [state]
  (map eval-test-with-state state))

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
