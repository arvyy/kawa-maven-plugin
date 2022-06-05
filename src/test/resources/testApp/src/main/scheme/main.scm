(import
  (scheme base)
  (scheme file)
  (scheme write)
  (scheme process-context)
  (test test)
  (test2 test2))

(with-output-to-file
  "test.out"
  (lambda ()
    (display (test))
    (newline)
    (display (test2))
    (newline)
    (display (cadr (command-line)))))
