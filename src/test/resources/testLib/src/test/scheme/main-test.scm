(define-library
  (main-test)
  (import (scheme base)
          (class org.apache.commons.text StringSubstitutor) ;;check <scope>test</scope> works
          (srfi 64)
          (test test))
  (export run-tests)
  (begin
    (define (run-tests)
      (test-assert (StringSubstitutor:createInterpolator))
      (test-equal "ok1" (test)))))