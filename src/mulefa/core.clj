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
  [state transition])

(defn- run-route
  [[state transition the-rest]]
  (run-transition state transition))

(defn- get-routes
  [graph start-state]
  ;;TODO
  (first graph))

(defn run
  [graph start-state]
  (->> (get-routes graph start-state)
       (map run-route)))
