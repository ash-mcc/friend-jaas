# Friend-JAAS

A small library that helps Chas Emerick's Friend library to use JAAS.

## Example use

Here is a snippet of code that shows the use of the `jaas-credential-fn` function from this library.

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

## Demo server

`demo-server.clj` shows how to use this library to authenticate users against Kerberos.

To use it you'll need to...
* Edit `krb5.conf` to provide values suitable for your Kerberos infrastructure.
* Edit `demo-server.clj` (line 84) to map your usernames to roles.
* `lein repl`
* `(start-server)`
* Point your web browser at `http://localhost:3000`

## License

Distributed under the Eclipse Public License, the same as Clojure.