(ns demo-server
  (:require 
    [clojure.tools.logging :refer (info error)]
    [org.httpkit.server :refer (run-server)]
    [compojure.core :refer (defroutes GET ANY context)]
		[compojure.handler :refer (site)]
    [compojure.route :refer (not-found)]
    [ring.util.response :refer (redirect)]
    [clojure.java.io :refer (resource)]
    [hiccup.core :refer (html h)]
    [hiccup.form :refer (form-to label text-field password-field submit-button)]
    [cemerick.friend :as friend]
    (cemerick.friend [workflows :as workflows]
                     [credentials :as creds])
    [friend-jaas :as jaas])
  (:import
    java.io.File))


;; ----------------------------------------------------------------------------------------
;; web pages 

(defn splash-page [req]
  (html 
    [:body
     [:h1 "Demo Server"]
     [:p "A webserver that demonstrates the use of the Friend-JAAS library to authenticate users against Kerberos."]
     [:ul
      [:li [:a {:href "super-only-page"} "super users only page"]]
      [:li [:a {:href "basic-only-page"} "basic users only page"]]
      [:li [:a {:href "authenticated-only-page"} "authenticated users only page"]]
      [:li [:a {:href "authentication-optional-page"} "authentication optional page"]]
      [:li [:a {:href "logout"} "logout"]]]]))

(defn login-page [req]
  (html 
    [:body 
     [:h1 (h "Login")]
     (form-to [:post "login"]
              (h "Username: ") (text-field "username") [:br]
              (h "Password: ") (password-field "password")
              (submit-button "Login"))]))


;; ----------------------------------------------------------------------------------------
;; routes

(defroutes all-routes
  (GET "/" req splash-page)
  (GET "/login" req login-page)
  (friend/logout (ANY "/logout" req (redirect "/")))
  (GET "/super-only-page" [] (friend/authorize #{::super} (str "Only 'super' users should be able to see this; you are:" (friend/current-authentication))))
  (GET "/basic-only-page" [] (friend/authorize #{::basic} (str "Only 'basic' users should be able to see this; you are:" (friend/current-authentication))))
  (GET "/authenticated-only-page" [] (friend/authenticated (str "Only authenticated users should be able to see this; you are:" (friend/current-authentication))))
  (GET "/authentication-optional-page" [] (str "Authenticated or not, you should be able to see this; you are:" (friend/current-authentication)))
  (not-found "Page not found"))


;; ----------------------------------------------------------------------------------------
;; roles stuff

(derive ::super ::basic)

(defn get-roles-fn [roles-map username]
  (get roles-map username #{}))


;; ----------------------------------------------------------------------------------------
;; utils

(defn find-abs-path [filename-on-classpath]
  (let [url (resource filename-on-classpath)]
    (if (nil? url)
      (do
        (error filename-on-classpath "not found on the classpath")
        nil)
      (-> url .toURI File. .getAbsolutePath))))


;; ----------------------------------------------------------------------------------------
;; Friend config

(def auth-config 
  (let [get-roles-fn* (partial get-roles-fn {"sam" #{::super} "bob" #{::basic}})
        jaas-credential-fn* (partial jaas/jaas-credential-fn "kerberos-username-and-password" get-roles-fn*)]
    {:workflows [(workflows/interactive-form :credential-fn jaas-credential-fn*)]}))

(defn config-jaas-for-kerberos! []
  (do
    (System/setProperty "java.security.krb5.conf" (find-abs-path "krb5.conf"))
    (System/setProperty "java.security.auth.login.config" (find-abs-path "login.conf"))
    (System/setProperty "sun.security.krb5.debug" "false")))


;; ----------------------------------------------------------------------------------------
;; the web server

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 200)
    (reset! server nil)))

(defn start-server []
  (do
    (config-jaas-for-kerberos!)
    (reset! server (run-server (site (friend/authenticate all-routes auth-config)) 
                               {:port 3000}))))




