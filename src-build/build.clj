(ns build
  (:require [clojure.tools.build.api :as b]
            [hyperfiddle.electric3.svg-importer :as importer]
            [clojure.java.io :as io]
            [clojure.pprint :as p]
            [clojure.string :as str]
            [deps-deploy.deps-deploy :as d]))

(def lib 'com.hyperfiddle/heroicons-electric)
(def version (b/git-process {:git-args "describe --tags --long --always --dirty"}))
(def basis (b/create-basis {:project "deps.edn"}))
(def jar-file (format "target/%s-%s.jar" (name lib) version))

(defn pprint-str [o]
  (with-out-str
    (p/with-pprint-dispatch
      p/code-dispatch
      (p/pprint o))))

(defn ns-form [ns]
  (format
"(ns %s
  (:refer-clojure :exclude [key map])
  (:require [hyperfiddle.electric3 :as e]
            [hyperfiddle.electric-dom3 :as dom]
            [hyperfiddle.electric-svg3 :as svg]))
  #?(:cljs (:require-macros [%s]))" ns ns))

(defn generate-source-file! [ns svg-source-path]
  (let [segments (str/split (name ns) #"\.")
        out-path (str "target/gen/" (str/join "/" segments ) ".cljc")]
    (io/make-parents out-path)
    (spit out-path (str (ns-form ns) "\n"))
    (binding [*ns* (create-ns ns)]
      (doseq [def (importer/generate-defs ns svg-source-path)]
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
                           :developerConnection "scm:git:ssh://git@github.com/hyperfiddle/heroicons-electric.git"}
                :pom-data [[:licenses
                            [:license
                             [:name "Eclipse Public License v2.0"]
                             [:url "https://www.eclipse.org/legal/epl-v20.html"]
                             [:comments "Covers the code translating Heroicon's svg files into Electric code."]]
                            [:license
                             [:name "MIT License"]
                             [:url "https://github.com/hyperfiddle/heroicons-electric/blob/0327158262396639aa779ea2e8d11e67367da214/licenses/Notice.txt"]
                             [:comments "Covers translated Heroicons's SVG files."]]]]})

  (generate-source-file! 'heroicons.electric3.v24.outline "vendors/heroicons/optimized/24/outline")
  (generate-source-file! 'heroicons.electric3.v24.solid "vendors/heroicons/optimized/24/solid")

  (b/jar {:class-dir "target/gen"
          :jar-file jar-file}))

(defn deploy [_]
  (d/deploy {:installer :remote
             :artifact  jar-file
             :pom-file  "target/pom.xml"}))

(comment
  (build nil)
  )
