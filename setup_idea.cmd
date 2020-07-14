@echo off
title setupDecompWorkspace
echo ####################
echo setupDecompWorkspace
echo ####################
echo.
.\gradlew setupDecompWorkspace

echo.
echo.
echo.

echo ####################
echo Loading IDEA
echo ####################
echo.
title IDEA
.\gradlew idea
.\gradlew genIntellijRuns
echo Done &pause>nul