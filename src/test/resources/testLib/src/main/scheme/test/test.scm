(define-library
  (test test)
  (import (scheme base)
          (class org.apache.commons.lang3.mutable MutableInt))
  (export test)
  (begin
    (define (test)
      (if (= 1 ((MutableInt 1):getValue))
          "ok1"
          "fail1"))))