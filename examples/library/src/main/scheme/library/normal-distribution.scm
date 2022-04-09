(define-library
  (library normal-distribution)
  (import (scheme base)
          ;; importing class from library included as a dependency in pom.xml
          (class org.apache.commons.math3.distribution NormalDistribution))
  (export cumulative-normal-probability)
  (begin
    (define (cumulative-normal-probability mean std x)
      ((NormalDistribution mean std):cumulativeProbability x))))