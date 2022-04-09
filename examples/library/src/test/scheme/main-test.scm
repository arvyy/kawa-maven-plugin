(define-library
  (main-test)
  (import (scheme base)
          (srfi 64)
          (library normal-distribution))
  (export run-tests)
  (begin
    (define (run-tests)
      (test-approximate 0.5 (cumulative-normal-probability 10 2 10) 0.001))))