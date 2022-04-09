(define-library
  (application normal-distribution)
  (import (scheme base)
          (scheme write)
          (scheme read)
          (library normal-distribution))
  (export run)
  (begin
    (define (get-number text)
      (display text)
      (let ((n (string->number (read-line))))
        (if n
            n
            (begin
              (display "Not a number!\n")
              (get-number text)))))
    (define (run)
      (define mean (get-number "Enter mean\n"))
      (define std (get-number "Enter standard deviation\n"))
      (define x (get-number "Enter x, for which to calculate P(X < x)\n"))
      (display (string-append "The result is " (number->string (cumulative-normal-probability mean std x)) "\n")))))