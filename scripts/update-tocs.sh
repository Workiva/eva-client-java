#!/bin/bash
# Updates table of contents in all relevant readmes
# Requires markdown-toc
MAX_DEPTH=5
MAX_DEPTH_API_DOCS=3

# Parent README
markdown-toc -i docs/readme_clojure.md --maxdepth $MAX_DEPTH
markdown-toc -i docs/readme_java.md --maxdeptth $MAX_DEPTH
