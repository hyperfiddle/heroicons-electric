# Use Heroicons from Electric Clojure

Provides `heroicons.electric` for Electric Clojure, just like Heroicons provides
`@heroicons/react` and `@heroicons/vue` for JavaScript.

Javascript:
```javascript
import {ChevronUpDown} from "@heroicons/react/24/ouline";
```

Electric Clojure:
```clojure
(require '[heroicons.electric.v24.ouline :refer [chevron-up-down])
```


# Build steps

- `git submodule init --recursive`
- `clj -T:build build`
