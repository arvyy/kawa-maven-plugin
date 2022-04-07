(define-library
  (main-test)
  (import (scheme base)
          (srfi 64)
          (test test))
  (export run-tests)
  (begin
    (define (run-tests)
      (test-equal "ok1" (test)))))