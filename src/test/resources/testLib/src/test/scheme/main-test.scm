(define-library
  (main-test)
  (import (scheme base)
          (class org.apache.commons.text StringSubstitutor) ;;check <scope>test</scope> works
          (class org.apache.commons.io IOUtils)
          (class java.nio.charset StandardCharsets)
          (srfi 64)
          (test test))
  (export run-tests)
  (begin
    (define (run-tests)
      (test-assert (StringSubstitutor:createInterpolator))
      (test-equal "ok1" (test))
      (test-equal "ok2" (read-file "/file-main.txt"))
      (test-equal "ok3" (read-file "/file-test.txt")))

    (define (read-file file)
        (IOUtils:resourceToString file StandardCharsets:UTF_8))
      ))