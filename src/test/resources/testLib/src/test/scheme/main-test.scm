;; intentionally faulty test, to check behavior on test failures
(import (scheme base)
        (test test)
        (srfi 64))

(test-begin "Test testLib")
(test-equal "ok1" (test))
(test-end)