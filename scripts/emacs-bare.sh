#!/bin/bash

cat > ~/.emacs <<EOF
(require 'package)
(add-to-list 'package-archives '("melpa" . "http://melpa.org/packages/"))
(package-initialize)
(package-refresh-contents)
EOF

cat > package-list <<EOF
(package-install 'load-relative)
(package-install 'loc-changes)
(package-install 'elquery)
(package-install 'gradle-mode)
EOF

echo "y" | emacs --batch -u `whoami` --script package-list

cat >> ~/.emacs <<EOF 
(add-to-list 'load-path "~/.emacs.d/dcoverage/")
(require 'dcoverage)
EOF
