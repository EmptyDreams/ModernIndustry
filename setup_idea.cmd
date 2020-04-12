@echo off
title setupDecompWorkspace
echo ####################
echo setupDecompWorkspace
echo ####################
echo.
cmd /c ".\gradlew setupDecompWorkspace"

echo.
echo.
echo.

echo ####################
echo Loading IDEA
echo ####################
echo.
title IDEA
cmd /c ".\gradlew idea"
cmd /c ".\gradlew genIntellijRuns"
echo Done &pause>nul