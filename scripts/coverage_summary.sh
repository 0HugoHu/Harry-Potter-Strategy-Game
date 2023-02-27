#!/bin/bash


emacs --batch -u `whoami` --script scripts/docov.el

cv=`egrep "\| *Totals *\|" coverage.txt | cut -f 3 -d"|" | tr -d " "`

echo "TOTAL COVERAGE: ${cv}%"
