# Friend-JAAS

A small library that helps Chas Emerick's Friend library to use JAAS.

## Example use

```clojure
(let [get-roles-fn (fn [username] #{::user})
      jaas-credential-fn* (partial jaas-credential-fn name-of-a-jaas-login-ctx get-roles-fn)
      auth-config {:workflows [(interactive-form :credential-fn jaas-credential-fn*)]}]
  (authenticate ring-handler-to-be-secured auth-config))
```
where:
* `get-roles-fn` outputs a (possibly empty) collection of roles when given a username.
* `name-of-a-jaas-login-ctx` (a string) is the name of a JAAS login.conf entry (a login context).
  This example uses `interactive-form` so the named login context must support username&password credentials.

## Demo 

## License

Distributed under the Eclipse Public License, the same as Clojure.