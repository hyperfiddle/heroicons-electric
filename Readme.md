[![Clojars Project](https://img.shields.io/clojars/v/com.hyperfiddle/heroicons-electric.svg)](https://clojars.org/com.hyperfiddle/heroicons-electric)

# Heroicons in Electric Clojure

Provides `heroicons.electric` for Electric Clojure, just like [Heroicons](https://github.com/tailwindlabs/heroicons) provides
`@heroicons/react` and `@heroicons/vue` for JavaScript.

Javascript:
```javascript
import {ChevronUpDown} from "@heroicons/react/24/ouline";
```

Electric Clojure:

```clojure
(ns my-view
  (:require [hyperfiddle.electric :as e]
            [hyperfiddle.electric-dom2 :as dom]
            [heroicons.electric.v24.outline :refer [chevron-up-down]]))

(e/defn MyIcon []
  (chevron-up-down (dom/props {:style {:width "1em"}})))

```

# Build steps

- `git submodule init --recursive`
- `clj -X:build build`
- `env CLOJARS_USERNAME=username CLOJARS_PASSWORD=clojar-token clj -X:build deploy`

# Run demo app

Renders all icons for both solid and outline styles.

```
clj -X:dev user/main
```

