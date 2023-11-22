(ns app
  (:require [hyperfiddle.electric :as e]
            heroicons.electric.v24.outline
            heroicons.electric.v24.solid
            [hyperfiddle.electric-dom2 :as dom])
  #?(:cljs (:require-macros [app])))


(defmacro all-icons [ns]
  (let [icons-syms (for [var (vals (ns-publics ns))
                         :when (.isMacro var)]
                     (.toSymbol var))]
    `(do ~@(take ##Inf (map list icons-syms))))) ; HACK FIXME too many icons produce a too large JS function. Need incremental comp.

(e/defn App []
  (binding [dom/node js/document.body]
    (dom/h1 (dom/text "All icons"))
    (dom/h2 (dom/text "Outline"))
    (dom/div
      (dom/props {:style {:display :grid, :gap "0.5rem", :grid-template-columns "repeat(auto-fill, 2rem)", #_#_:grid-auto-flow :column}})
      (all-icons heroicons.electric.v24.outline))
    (dom/h2 (dom/text "Solid"))
    (dom/div
      (dom/props {:style {:display :grid, :gap "0.5rem", :grid-template-columns "repeat(auto-fill, 2rem)", #_#_:grid-auto-flow :column}})
      (all-icons heroicons.electric.v24.solid))))

