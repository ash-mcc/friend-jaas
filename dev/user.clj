(ns user
  (:require [demo-server :refer (start-server stop-server)]))

(println "In the repl, use...
   (start-server)        ...to start the demo server
   http://localhost:3000 ...to try it via a web browser
   (stop-server)         ...to stop it")