(ns friend-jaas
  (:require
    [clojure.tools.logging :refer (info warn)])
  (:import 
    (javax.security.auth.login LoginContext LoginException)
    (javax.security.auth.callback CallbackHandler NameCallback PasswordCallback)))


(defn mk-username-password-handler 
  "Returns a username&password JAAS CallbackHandler"
  [username, password] 
  (reify CallbackHandler 
        (handle [this callbacks]
          (doseq [callback callbacks]
            (cond
              (instance? NameCallback callback) (.setName callback username)
              (instance? PasswordCallback callback) (.setPassword callback (char-array password))
              :else (warn "Unsupported Callback: " callback))))))


(defn jaas-credential-fn
  "A JAAS-using credentials function that is intended to be used 
   with `cemerick.friend/authenticate` or individual authentication workflows.

   Example use:

     (let [get-roles-fn (fn [username] #{::user})
           jaas-credential-fn* (partial jaas-credential-fn name-of-a-jaas-login-ctx get-roles-fn) ;; 
           auth-config {:workflows [(interactive-form :credential-fn jaas-credential-fn*)]}]
       (authenticate ring-handler-to-be-secured auth-config))

   ...where:

     get-roles-fn outputs a (possibly empty) collection of roles when given a username

     name-of-a-jaas-login-ctx is a string that is the name of a JAAS login.conf entry
       that, in this case (because of the use of interactive-form), supports username&password credentials"
  [login-ctx-name get-roles {:keys [username password]}]
  (let [loginCtx (LoginContext. login-ctx-name (mk-username-password-handler username password))]
    (try
      (do
        (.login loginCtx)
        (info login-ctx-name "login succeeded for" username ", returned identity:" (some-> loginCtx .getSubject .getPrincipals))
        {:identity username :roles (get-roles username)})
      (catch LoginException e
        (do
          (info e login-ctx-name "login failed for" username)
          nil)))))





