# Clj-ip

A Clojure library designed to for testing whether an IP address is
part of a subnet.

## Usage

Add the following to your dependencies:

```clj
[functionalbytes/clj-ip "0.9.0"]
```

Require the `clj.ip` namespace and simply use the `has-ip?` function
on two Strings, like so:

```clj
(require [clj.ip :refer (has-ip? compile)])

(has-ip? "192.168.0.0/24" "192.168.0.54")
;=> true
```

When a subnet/range is used many times, it can be compiled to a faster
version, using `compile`. A compiled version can also be used as a
predicate function.

```clj
(def subnet (compile "192.168.0.0/24")

(has-ip? subnet "192.168.1.54")
;=> false

(subnet "192.168.0.54")
;=> true
```

## License

Copyright © 2015-2024 Functional Bytes

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
