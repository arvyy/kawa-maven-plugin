(define-library
  (main-test)
  (import (scheme base)
          (srfi 64)
          (application normal-distribution))
  (export run-tests)
  (begin
    (define (run-tests)
        (define input (open-input-string "0\n1\n0"))
        (define output (open-output-string))
        (parameterize ((current-input-port input)
                       (current-output-port output))
          (run)
          (test-equal "Enter mean\nEnter standard deviation\nEnter x, for which to calculate P(X < x)\nThe result is 0.5\n" (get-output-string output))))))

