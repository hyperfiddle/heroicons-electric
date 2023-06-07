(ns hyperfiddle.electric.svg-importer
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [hickory.core :as html]
   [hyperfiddle.electric :as-alias e]
   [hyperfiddle.electric-dom2 :as-alias dom]
   [hyperfiddle.electric-svg :as-alias svg])
  (:import [org.jsoup Jsoup]
           [org.jsoup.nodes Attribute Attributes Comment DataNode Document
            DocumentType Element TextNode XmlDeclaration]
           [org.jsoup.parser Tag Parser]))

(def context->ns {::html "hyperfiddle.electric-dom2"
                  ::svg  "hyperfiddle.electric-svg"})

(defn infer-context "return [current-context next-context]" [context tag]
  (case context
    ::html (case (str/lower-case (name tag))
             "svg" [::svg ::svg]
             [::html ::html])
    ::svg  (case tag
             :foreignObject [::svg ::html]
             [::svg ::svg])))

(defn svg->hiccup [svg-str]
  (with-redefs [hickory.utils/lower-case-keyword keyword]
    (html/as-hiccup (first (Parser/parseXmlFragment svg-str "")))))

(defn hiccup->electric "Only accept static, explicit hiccup. Props should always be provided, no dynamic expressions."
  ([form] (hiccup->electric ::html form))
  ([context form]
   (cond
     (vector? form) (let [[tag props & body] form
                          [context next-context] (infer-context context tag)]
                      `(~(symbol (context->ns context) (name tag))
                        ~@(when (seq props) [`(dom/props ~(dissoc props :xmlns))])
                        ~@(when (seq body)
                            (remove nil? (map (partial hiccup->electric next-context) body)))))
     (string? form) (let [s (str/trim form)]
                      (when-not (str/blank? s)
                        (if (re-matches #"^<\!--(.*?)-->" s)
                          `(dom/comment_ ~s)
                          `(dom/text ~s))))
     :else (throw (ex-info "unknown form" {:form form})))))

(defn svg->electric [svg-str]
  (hiccup->electric (svg->hiccup svg-str)))

(comment
  (svg->electric (slurp "./vendors/heroicons/src/24/outline/academic-cap.svg"))
  )


(defn icon-name [^java.io.File file] (str/join (str/split (.getName file) #"\.[^\.]*$")))

(defn generate-defs [path]
  (let [files (->> (io/file path)
                (file-seq)
                (filter #(.isFile %))
                (sort))
        bindings (reduce (fn [r file]
                           (let [[svg & body]   (svg->electric (slurp file))
                                 icon-name      (icon-name file)
                                 Body           (gensym "Body_")]
                             (conj r `((e/def ~(symbol (str icon-name "*"))
                                         (e/fn [~Body]
                                           ~(list* svg `(new ~Body) body)))
                                       (defmacro ~(symbol icon-name) [~'& ~'body]
                                         (list 'new '~(symbol (name (ns-name *ns*)) (str icon-name "*"))
                                           (list* `e/fn [] ~'body)))))))
                   []
                   files)]
    (mapcat identity bindings)))

(comment
  (generate-defs "./vendors/heroicons/src/24/outline") )
