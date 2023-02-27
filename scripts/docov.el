(setq gradle-executable-path "./gradlew")
(dcoverage-generate-and-save "coverage.txt")
(while (not dcoverage-save-done)
  (sleep-for 1))

