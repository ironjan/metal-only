#!/bin/bash
echo "Old Version:"
cloc --include-lang=Java,Kotlin,XML old-version/app/src/ old-version/metal-only-client-library/src/ old-version/core/src/

echo "Rewrite:"
cloc --include-lang=Java,Kotlin,XML rewrite/app/src/
