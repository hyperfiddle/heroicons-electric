(ns app
  (:require [hyperfiddle.electric :as e]
            [heroicons.electric.v24.outline :as i]
            [hyperfiddle.electric-dom2 :as dom])
  #?(:cljs (:require-macros [app])))


(defmacro all-icons []
  (let [icons-syms (for [var (vals (ns-publics 'heroicons.electric.v24.outline))
                         :when (.isMacro var)]
                     (.toSymbol var))]
    `(do ~@(take 150 (map list icons-syms))))) ; HACK FIXME too many icons produce a too large JS function. Need incremental comp.

(e/defn App []
  (binding [dom/node js/document.body]
    (all-icons)))

