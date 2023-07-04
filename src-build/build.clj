(ns build
  (:require [clojure.tools.build.api :as b]
            [hyperfiddle.electric.svg-importer :as importer]
            [clojure.java.io :as io]
            [clojure.pprint :as p]
            [clojure.string :as str]
            [deps-deploy.deps-deploy :as d]))

(def lib 'com.hyperfiddle/heroicons-electric)
(def version (b/git-process {:git-args "describe --tags --long --always --dirty"}))
(def basis (b/create-basis {:project "deps.edn" :aliases [:dev]}))
(def jar-file (format "target/%s-%s.jar" (name lib) version))

(defn pprint-str [o]
  (with-out-str
    (p/with-pprint-dispatch
      p/code-dispatch
      (p/pprint o))))

(defn ns-form [ns]
  (list 'ns ns
    '(:refer-clojure :exclude [key map])
    '(:require [hyperfiddle.electric :as e]
               [hyperfiddle.electric-dom2 :as dom]
               [hyperfiddle.electric-svg :as svg])))

(defn generate-source-file! [ns svg-source-path]
  (let [segments (str/split (name ns) #"\.")
        out-path (str "target/gen/" (str/join "/" segments ) ".cljc")]
    (io/make-parents out-path)
    (spit out-path (str (pprint-str (ns-form ns)) "\n"))
    (binding [*ns* (create-ns ns)]
      (doseq [def (importer/generate-defs svg-source-path)]
        (spit out-path (str (pprint-str def) "\n") :append true)))))

(defn build [_]
  (b/delete {:path "target"})
  (b/write-pom {:target   "target"
                :lib      lib
                :version  version
                :basis    basis
                :src-dirs ["src"]
                :scm      {:url                 "https://github.com/hyperfiddle/heroicons-electric"
                           :connection          "scm:git:git://github.com/hyperfiddle/heroicons-electric.git"
                           :developerConnection "scm:git:ssh://git@github.com/hyperfiddle/heroicons-electric.git"}})

  (generate-source-file! 'heroicons.electric.v24.outline "vendors/heroicons/optimized/24/outline")
  (generate-source-file! 'heroicons.electric.v24.solid "vendors/heroicons/optimized/24/solid")

  (b/jar {:class-dir "target/gen"
          :jar-file jar-file}))

(defn deploy [_]
  (d/deploy {:installer :remote
             :artifact  jar-file
             :pom-file  "target/pom.xml"}))

(comment
  (build nil)
  )
