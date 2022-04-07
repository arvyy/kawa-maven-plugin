(import
  (scheme base)
  (scheme file)
  (scheme write)
  (test test)
  (test2 test2))

(with-output-to-file
  "test.out"
  (lambda ()
    (display (test))
    (newline)
    (display (test2))))
