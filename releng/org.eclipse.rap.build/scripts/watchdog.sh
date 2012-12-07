#!/bin/bash
#

URL="http://rap.eclipsesource.com/rapdemo/examples"
TMPFILE="$WORKSPACE/examples_$BUILD_ID.html"

# cleanup workspace
/usr/bin/find "$WORKSPACE" -type f -cmin +720 -delete

/usr/bin/wget --timeout=10 --tries=5 -O "$TMPFILE" "$URL" || exit 1

/bin/grep -q discoverAjax "$TMPFILE"
if [ $? != 0 ]; then
  echo "=== received page content doesn't look right ==="
  cat "$TMPFILE"
  exit 2
fi
