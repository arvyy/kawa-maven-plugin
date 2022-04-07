(define-library
  (test2 test2)
  (import (scheme base)
          (class org.apache.commons.math3.distribution NormalDistribution))
  (export test2)
  (begin
    (define (test2)
      (if (= 1 ((NormalDistribution):getStandardDeviation))
          "ok2"
          "fail2"))))