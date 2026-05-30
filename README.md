# Rubiks Race

A single-player version of the popular game, Rubik's Race. The objective of the game is to move tiles to match the center of your 5x5 grid to the 3x3 target grid as quickly and efficiently as possible.

## 🚀 Live Demo
Play the game instantly in your browser here: 👉 **[Rubik's Race Live](https://vinukranaweera.github.io/rubiks-race/)**

*(Note: The initial load may take 15–30 seconds as CheerpJ caches the runtime components in your browser. Expand window screen to full-size to fully experience the game.)*

## ✨ Features

* **Interactive Graphical UI:** Built with Java Swing components for accurate, responsive tile clicking and navigation
* **Custom Dynamic Rendering:** Seamless rendering of all puzzle grids and sliding tiles
* **Performance Tracking:** Move counter to track player actions and real-time timer to measure performance
* **Instant Replayability:** Reset and shuffle functionality generate completely randomized 3x3 target patterns for replayability
* **Difficulty options**:

  - **Larger grid**: Play on a 6x6 grid to match the center to the 4x4 target pattern
  - **Limited Moves**: Complete the puzzle within the move threshold for a multiplier to your score
  - **Limited Time**: Complete the puzzle before time runs out for an additional score multiplier
    
* **Dynamic Scoring:** Calculates score upon puzzle completion based on difficulty options enabled, total time elapsed, and moves taken.
* **Local Leaderboard:** Saves and ranks performance data to the leaderboard, which displays the top ten highest registered scores

## 🛠️ Technology Stack

* **Java Development Kit (JDK):** Version 17
* **GUI Framework:** Java Swing / AWT (Abstract Window Toolkit)
* **Web Deployment:** [CheerpJ v4.3](https://leaningtech.com/cheerpj/) (Java bytecode to WebAssembly runtime architecture)
* **Hosting Platform:** GitHub Pages
