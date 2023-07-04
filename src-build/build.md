# Build steps

- `git submodule init --recursive`
- `clj -X:build build`
- `env CLOJARS_USERNAME=username CLOJARS_PASSWORD=clojar-token clj -X:build deploy`
