(ns user) ; Must be ".clj" file, Clojure doesn't auto-load user.cljc
  ;; For fastest REPL startup, no heavy deps here, REPL conveniences only
  ;; (Clojure has to compile all this stuff on startup)

; lazy load dev stuff - for faster REPL startup and cleaner dev classpath
(def shadow-start! (delay @(requiring-resolve 'shadow.cljs.devtools.server/start!)))
(def shadow-watch (delay @(requiring-resolve 'shadow.cljs.devtools.api/watch)))
(def start-electric-server! (delay @(requiring-resolve 'electric-server-java11-jetty10/start-server!)))
(def rcf-enable! (delay @(requiring-resolve 'hyperfiddle.rcf/enable!)))

; Server-side Electric userland code is lazy loaded by the shadow build.
; WARNING: make sure your REPL and shadow-cljs are sharing the same JVM!

(def electric-server-config
  {:host "0.0.0.0", :port 8080, :resources-path "public", :manifest-path "public/js/manifest.edn"})

(defn main [& args]
  (println "Starting Electric compiler and server...")
  (@shadow-start!) ; serves index.html as well
  (@shadow-watch :dev) ; depends on shadow server
  (def server (@start-electric-server! electric-server-config))
  (comment (.stop server)))
