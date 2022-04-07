(import (scheme base)
        (scheme process-context)
        (srfi 64)
        (main-test))

(test-begin "Test")

(let* ((runner (test-runner-current))
       (callback (test-runner-on-final runner)))
  (test-runner-on-final!
    runner
    (lambda (r)
      (callback r)
      (exit (= 0 (test-runner-fail-count r))))))
(run-tests)

(test-end)