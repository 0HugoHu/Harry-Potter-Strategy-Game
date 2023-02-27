;;; package --- Summary
;;;  Display code coverage

;;; Commentary:
;;;   cov.el doesn't work in console mode, and doesn't show partially covered branches.
;;;   It also doesn't show a summary after tests are run

;;; Code:
(require 'elquery)
(require 'gradle-mode)

(defgroup dcoverage nil "Shows unit test coverage in colors in source code buffers.")

(defcustom dcoverage-covered-stm-color  "#008700"
  "The color to use for a statement that got covered by the tests."
  :tag "dcoverage covered statement color"
  :group 'dcoverage
  :type 'color)

(defcustom dcoverage-uncovered-stm-color  "#870000"
  "The color to use for a statement that was not covered by the tests."
  :tag "dcoverage uncovered statement color"
  :group 'dcoverage
  :type 'color)

(defcustom dcoverage-part-covered-branch-color  "#878700"
  "The color to use for a branch that was partially covered."
  :tag "dcoverage part covered branch color"
  :group 'dcoverage
  :type 'color)


(defcustom dcoverage-well-covered-report-color  "green"
  "The color to use to report a 100% covered class."
  :tag "dcoverage well covered report color"
  :group 'dcoverage
  :type 'color)

(defcustom dcoverage-moderate-covered-report-color  "dark orange"
  "The color to use to report a moderately covered class."
  :tag "dcoverage moderately covered report color"
  :group 'dcoverage
  :type 'color)

(defcustom dcoverage-poorly-covered-report-color  "red"
  "The color to use to report a pooly covered class."
  :tag "dcoverage poorly covered report color"
  :group 'dcoverage
  :type 'color)


(defcustom dcoverage-run-clover-function (lambda() (gradle-run "clean cloverGenerateReport"))
  "The elisp function to execute in order to run clover to generate test coverage results."
  :tag "dcoverage clover execution function"
  :group 'dcoverage
  :type 'function)

(defcustom dcoverage-key-generate-and-show (kbd "C-c C-t")
  "The keyboard sequence to generate results and show them."
  :tag "dcoverage key sequence generation and show"
  :group 'dcoverage
  :type 'key-sequence)

(defcustom dcoverage-key-clear-all (kbd "C-c -")
  "The keyboard sequence to clear coloration from all buffs."
  :tag "dcoverage key sequence clear"
  :group 'dcoverage
  :type 'key-sequence)

(defcustom dcoverage-package-name-elide ""
  "Elide this string from the start of package names when displaying class names."
  :tag "dcoverage package name elide"
  :group 'dcoverage
  :type 'string)

(defun dcoverage-find-project-root ()
  "Find the project root, by looking for build.gradle file."
  (let ((dir (locate-dominating-file default-directory "gradlew")))
    (if dir dir default-directory)))

(defun dcoverage-default-find-cov-file ()
  "Default way to find coverage file: find project root, then go to 'build/reports/clover/clover.xml'."
  (concat (file-name-as-directory (dcoverage-find-project-root)) "app/build/reports/clover/clover.xml"))

(defvar dcoverage-find-cov-file-fn (symbol-function 'dcoverage-default-find-cov-file))
"Specifies how to find the coverage file.  Set if you have a different setup."


(defun dcoverage-color-stmts (stmtxml)
  "Take an XML row for a statement (STMTXML) and return a (line . color) pair."
  (let* ((lnumstr (elquery-prop stmtxml "num"))
         (lnum (string-to-number lnumstr))
         (color (if (equal (string-to-number (elquery-prop stmtxml "count")) 0)
                    dcoverage-uncovered-stm-color
                  dcoverage-covered-stm-color)))
    (cons lnum color)))

(defun dcoverage-color-branches (brxml)
  "Take an XML row for a branch (BRXML) and return a (line . color) pair."
  (let* ((lnumstr (elquery-prop brxml "num"))
         (lnum (string-to-number lnumstr))
         (tcount (string-to-number (elquery-prop brxml "truecount")))
         (fcount (string-to-number (elquery-prop brxml "falsecount")))
         (coveredct0 (if (equal tcount 0) 0 1))
         (coveredct  (+ coveredct0 (if (equal fcount 0) 0 1)))
         (color  (cond
                  ((equal coveredct 0) dcoverage-uncovered-stm-color)
                  ((equal coveredct 2) dcoverage-covered-stm-color)
                  (t dcoverage-part-covered-branch-color))))
    (cons lnum color)))
                  

             
                          

(defun dcoverage-mark-curr-line (color)
  "Mark the current line in specified COLOR."
  (let* ((ol (make-overlay (line-beginning-position)
                 (line-end-position )
                           (current-buffer)
                           nil
                           nil)))
    (overlay-put ol 'face (list :background color))
    (overlay-put ol 'oltype 'coveragemarker)
    (overlay-put ol 'evaporate t)))

(defun dcoverage-mark-list-of-lines (lst)
  "Takes in a list (LST) of (linenumber . color) pairs, and colors them all."
  (save-excursion
    (save-restriction
      (widen)
      (goto-char 1)
      (let ((currlinum 1))
        (dolist (item lst t)
          (let* ((linum (car item))
                 (lidelta (- linum currlinum))
                 (color (cdr item))
                 (i1 (forward-line lidelta))
                 (i2 (setq currlinum linum)))
            (dcoverage-mark-curr-line color)))))))

(defun dcoverage-clear-my-overlays ()
  "Clears all coloring put in by this package."
  (dolist (ol (overlays-in 1 (point-max)) t)
    (if (eq 'coveragemarker (overlay-get ol 'oltype))
        (delete-overlay ol))))

(defun dcoverage-color-buffer-from-list (lst)
  "Clears all coloring, then colors buffer according to LST."
  (dcoverage-clear-my-overlays)
  (dcoverage-mark-list-of-lines lst))

(defvar dcoverage-current-coverage-data (make-hash-table :test 'equal))


(defun dcoverage-merge-colors-in-list (lst)
  "Merge the line/color lists in LST, selecting the more 'severe' color when multiple apply."
  (let* ((sorted-list (sort (copy-sequence lst) (lambda (a b) (> (car a) (car b)))))
         (ordering (list dcoverage-uncovered-stm-color dcoverage-part-covered-branch-color dcoverage-covered-stm-color))
         (maybe-insert (lambda (newlst item)
                         (cond
                          ((null newlst) (list item))
                          ((equal (nth 0 item) (nth 0 (car newlst)))
                           (let*  ((lnum (car item))
                                   (c1 (cdr item))
                                   (c2 (cdr (car newlst)))
                                   (best-color (if (< (seq-position ordering c1) (seq-position ordering c2)) c1 c2)))
                             (cons (cons lnum best-color) (cdr newlst))))

                          (t (cons item newlst))))))
    (seq-reduce maybe-insert sorted-list '())))
          

(defun dcoverage-parse-file (fname)
  "Parse a clover.xml file (in FNAME) into a useable representation."
  (let* ((xml (elquery-read-file fname))
         (list-of-files (elquery-$ "file" xml)))
    (dolist (fitem list-of-files dcoverage-current-coverage-data)
      (let* ((fpath (elquery-prop fitem "path"))
             (fname (elquery-prop fitem "name"))
             (pkgname (elquery-prop (elquery-parent fitem) "name"))
             (fmetrics-lst  (elquery-$ "metrics" fitem))
             (empty-metrics-lst '(:coveredstatements 0 :coveredconditionals 0 :statements 0 :conditionals 0))
             (fmetrics (if (null fmetrics-lst) empty-metrics-lst (elquery-props (car fmetrics-lst))))
             (stmts (elquery-$ "[type=stmt]" fitem))
             (branches (elquery-$ "[type=cond]" fitem))
             (stmt-colors (mapcar 'dcoverage-color-stmts stmts))
             (branch-colors (mapcar 'dcoverage-color-branches branches))
             (all-line-colors (dcoverage-merge-colors-in-list (append stmt-colors branch-colors)))
             (data (plist-put fmetrics :dcoverage-line-colors all-line-colors))
             (finaldata (plist-put data :fully-qualified-name (concat pkgname "." fname))))
        (puthash fpath finaldata dcoverage-current-coverage-data)))))

(defun dcoverage-load-default-file()
  (interactive)
  "Load the coverage data from the default file (as determined by dcoverage-find-cov-file-fn)"
  (dcoverage-parse-file (funcall dcoverage-find-cov-file-fn)))

(defun dcoverage-color-current-buffer ()
  "Colors the current buffer based on the coverage data, reading it if needed."
  (interactive)
  (let* ((fname (buffer-file-name))
         (data (gethash fname dcoverage-current-coverage-data )))
    (when data
      (dcoverage-color-buffer-from-list (plist-get data :dcoverage-line-colors)))))

(defun dcoverage-fold-hash (fn hash start)
  "Fold FN (key valye ans) over HASH starting with START as initial value/answer."
  (let ((ans start))
    (maphash (lambda (k v) (setq ans (funcall fn k v ans))) hash)
    ans))


(defun dcoverage-build-coverage-row (fpath filedata otherrows)
  "Take the FPATH and FILEDATA from one file's results in dcoverage-parse-file and build a row for the summary, then conses it onto OTHERROWS."
  (let* ((fqname (plist-get filedata :fully-qualified-name))
         (adjname (string-remove-suffix ".java" (string-remove-prefix dcoverage-package-name-elide fqname)))
         (ign (message "fqname is %s, elide is %s adjname is %s" fqname dcoverage-package-name-elide adjname))
         (covstm (string-to-number (plist-get filedata :coveredstatements)))
         (covbr  (string-to-number (plist-get filedata :coveredconditionals)))
         (allstm (string-to-number (plist-get filedata :statements)))
         (allbr  (string-to-number (plist-get filedata :conditionals)))
         (covtot (+ covstm covbr))
         (alltot (+ allstm allbr))
         (covpct (if (equal alltot 0) 100 (/ (* 100 covtot) alltot))))
    (cons (list adjname covpct covstm allstm covbr allbr fpath)
          otherrows)))


(defun dcoverage-print-table-entry (str pad width sep )
  "Print one table entry (STR) padded (with PAD) to WIDTH and followed by SEP."
  (let* ((w0  (length str))
         (wpad (- width w0))
         (left (/ wpad 2))
         (right (- wpad left))
         (start (point))
         (ign0 (insert-char pad left))
         (ign1 (insert str))
         (ign2 (insert-char pad right))
         (end (point))
         (ign3 (insert sep)))
    (cons start end)))
                           
(defun dcoverage-print-table-row (cols propfns pad widths sep)
  "Print a row with COLS adjusted to have properties by PROPFNS with PAD padding at WIDTHS wide and split by SEP."
  (insert sep)
  (dolist (col cols t)
    (let* ((w (car widths))
           (se (dcoverage-print-table-entry col pad w sep))
           (start (car se))
           (end (cdr se))
           (pf (car propfns))
           (ign (funcall pf col start end)))
      (progn
       (setq propfns (cdr propfns))
       (setq widths (cdr widths)))))
  (insert "\n"))
  

(defun dcoverage-map-nested (fn lst)
  "Map FN over a two deep nested LST."
  (mapcar (lambda (x) (mapcar fn x)) lst))


        

(defun dcoverage-result-class-named-pressed (btn)
  "Handle press of BTN."
  (message "Button press for %s" (button-get btn 'dcovinfo))
  (save-selected-window
    (let*
        ((fname (button-get btn 'dcovinfo)))
      (find-file-other-window fname))))

(defun dcoverage-color-all-open-files (fnamelist)
  "Color all open files in FNAMELIST based on current data."
  (let ((startbuf (current-buffer)))
    (dolist (fnm fnamelist t)
      (let ((b (find-buffer-visiting fnm)))
        (when b
          (set-buffer b)
          (dcoverage-color-current-buffer))))
    (set-buffer startbuf)))

(defun dcoverage-show-coverage-results()
  "Show the coverage results in both a summary buffer and coloring of open buffers."
  (interactive)
  (let* ((resbuf (get-buffer-create "*Coverage Results*"))
         (nothing (set-buffer resbuf))
         (nothing2 (erase-buffer))
         (header-row (list "Class Name " " Total Coverage% " " Cvrd Stmts " " Ttl Stmts " " Cvrdd Brs " " Ttl Brs "))
         (dash-row   (list "-----------" "-----------------" "------------" "-----------" "-----------" "---------"))
         (nop-pf     (lambda (col start end) t))
         (nop-pfs    (list nop-pf nop-pf nop-pf nop-pf nop-pf nop-pf))
         (cov-pf     (lambda (col start end)
                       (let* ((n (string-to-number col))
                              (color (cond
                                      ((< n 50) dcoverage-poorly-covered-report-color)
                                      ((< n 100) dcoverage-moderate-covered-report-color)
                                      (t dcoverage-well-covered-report-color)))
                              (ol (make-overlay start end)))
                         (overlay-put ol 'face (list :foreground color))
                         (overlay-put ol 'evaporate t))))
         (data-row-pfs  (list cov-pf nop-pf nop-pf nop-pf nop-pf)) ; will add one more per row as we evaluate each fname
         (init-widths (mapcar (symbol-function 'length) header-row))
         ;;this makes a list of rows, where each row has
         ;; 1. fully qualified name (e.g., foo.bar.MyClass)
         ;;   -though we remove dcoverage-package-name-elide
         ;; 2. coverage percent
         ;; 3. covered statement count
         ;; 4. all statement count
         ;; 5. covered branch count
         ;; 6. all branch count
         ;; 7. file pathname
         (rows (dcoverage-fold-hash (symbol-function 'dcoverage-build-coverage-row) dcoverage-current-coverage-data '()))
         ;;sort the rows by order of increasing coverage percentage
         (sorted-rows (sort (copy-sequence rows) (lambda (a b) (< (nth 1 a) (nth 1 b)))))
         ;;turn all the row data into strings
         (str-rows (dcoverage-map-nested (lambda (v) (if (numberp v) (number-to-string v) v)) sorted-rows))
         ;;rev-row is each row reversed, so that file pathname is first.
         (rev-row (mapcar 'reverse str-rows))
         ;;build  up a list of just the file pathnames (but the list is backwards)
         (fnames-r (seq-reduce (lambda (ans r) (cons (car r) ans)) rev-row '()))
         ;;reverse that so they are in the right oder
         (fnames (reverse fnames-r))
         (orig-fnames fnames)
         (rev-data-rows (mapcar 'cdr rev-row))
         (data-rows (mapcar 'reverse rev-data-rows))
         (sum-data-elt (lambda (n)
                           (seq-reduce #'+ (mapcar (lambda (row) (nth n row)) rows) 0)))
         (total-cv-stm (funcall sum-data-elt 2))
         (total-stm (funcall sum-data-elt 3))
         (total-cv-br (funcall sum-data-elt 4))
         (total-br (funcall sum-data-elt 5))
         (total-cv (+ total-cv-stm total-cv-br))
         (total-denom (+ total-stm total-br))
         (total-pct (if (equal total-denom 0) 100 (/ (* 100 total-cv) total-denom)))
         (total-row (list "Totals" (number-to-string total-pct) (number-to-string total-cv-stm) (number-to-string total-stm) (number-to-string total-cv-br) (number-to-string total-br)))
         (per-row-widths (dcoverage-map-nested 'length data-rows))
         (col-widths (seq-reduce (lambda (currw r)  (seq-mapn 'max currw r)) per-row-widths init-widths))
         (r0 (dcoverage-print-table-row dash-row nop-pfs ?- col-widths "+"))
         (r1 (dcoverage-print-table-row header-row nop-pfs ?\s col-widths "|"))
         (r2 (dcoverage-print-table-row dash-row nop-pfs ?- col-widths "+")))
    (dolist (row data-rows t)
      (let* ((myfname (car fnames))
             (lam  (lambda (col start end)
                     (make-button start end
                                  'action 'dcoverage-result-class-named-pressed
                                  'dcovinfo myfname)))
             (pfs (cons lam data-row-pfs)))
        (dcoverage-print-table-row row pfs ?\s col-widths "|")
        (setq fnames (cdr fnames))))
    (dcoverage-print-table-row dash-row nop-pfs ?- col-widths "+")
    (dcoverage-print-table-row total-row (cons nop-pf data-row-pfs) ?\s col-widths "|")
    (dcoverage-print-table-row dash-row nop-pfs ?- col-widths "+")
    (insert "\n\n")
    (insert-button "Remove Colors From All Buffers" 'action (lambda (btn) (dcoverage-clear-all)))
    (insert "\n")
    (dcoverage-color-all-open-files orig-fnames)
    (switch-to-buffer-other-window resbuf)))

(defun dcoverage-run-clover()
  "Rename clover.xml file to have .old, then run the build command."
  (interactive)
  (let* ((fname (dcoverage-default-find-cov-file))
         (newfname (concat fname ".old"))
         (ign  (if (file-exists-p fname)
                   (rename-file fname newfname t)
                 t)))
    (funcall dcoverage-run-clover-function)))

(defun dcoverage-generate-and-show()
  "Generate the coverage results, and show them."
  (interactive)
  (setq compilation-exit-message-function (lambda (pstat estat emesg)
                                            (if (equal estat 0)
                                                (progn (dcoverage-load-default-file)
                                                       (dcoverage-show-coverage-results))
                                              t)
                                            (setq compilation-exit-message-function nil)
                                            (cons emesg "")))
                                              
                                              
  (dcoverage-run-clover))


(setq dcoverage-save-to-file-name "cov.txt")

;;this is a hack to let us run dcoverage-generate-and-save
;;from a script.  Since the compilation is async, the script
;;would just exit.  We can make the script wait for
;;this variable to become true.  Would be nice
;;to use mutex/cv but those don't exist until Emacs 26.
(setq dcoverage-save-done nil)


(defun dcoverage-generate-and-save(fname)
  "Generates the coverage results, and saves them to a file named FNAME."
  (interactive)
  (setq dcoverage-save-to-file-name fname)
  (setq dcoverage-save-done nil)
  (setq compilation-exit-message-function (lambda (pstat estat emesg)
                                            (if (equal estat 0)
                                                (progn (dcoverage-load-default-file)
                                                       (dcoverage-show-coverage-results)
                                                       ;;show-coverage-results leaves us in results buffer
                                                       (write-file dcoverage-save-to-file-name))
                                              t)
                                            (setq dcoverage-save-done t)
                                            (cons emesg "")))
                                              
                                              
  (dcoverage-run-clover))


(defun dcoverage-clear-all()
  "Clears coloration from all open buffers and drops coverage data."
  (interactive)
  (setq dcoverage-current-coverage-data (make-hash-table :test 'equal))
  (let ((startbuf (current-buffer))
        (blist (buffer-list)))
    (dolist (buf blist t)
      (set-buffer buf)
      (dcoverage-clear-my-overlays))
    (set-buffer startbuf)))
        
(global-set-key dcoverage-key-generate-and-show 'dcoverage-generate-and-show)
(global-set-key dcoverage-key-clear-all 'dcoverage-clear-all)


(add-hook 'find-file-hook 'dcoverage-color-current-buffer)

(provide 'dcoverage)
;;; dcoverage.el ends here




