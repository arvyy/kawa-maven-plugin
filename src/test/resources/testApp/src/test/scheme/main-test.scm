(define-library
  (main-test)
  (import (scheme base)
          (srfi 64)
          (test2 test2))
  (export run-tests)
  (begin
    (define (run-tests)
      (test-equal "fail2" (test2)))))
