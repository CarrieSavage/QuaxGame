# QuaxGame
A digital implementation of the board game Quax, featuring a full JavaFX GUI and an AI opponent powered by a custom heuristics evaluation algorithm 

# Overview 
Quax is a connection based strategy board game, players alternate placing stones on an 11x11 board of octagon and rhombus cells and win by forming an unbroken chain connecting their two edges of the board. This project brings it to life as a complete, playable desktop application built from scratch in Java and JavaFX by a 3 person team over four Agile sprints. Beyond replicating the core rules, it includes an AI opponent capable of competing against human players, using a heuristic based evaluation function to score and select moves. 

# Features 
- Full stone placement mechanics with real time gameplay validation on an 11x11 board
- Win detection via edge to edge connection
- Pie rule support : Player 2 can swap colours after player 1's opening moves for fairness
- Interactive JavaFX GUI for human vs human or human vs bot play
- Show Strategy Mode : Visualises the bot's move evaluation directly on the board
- Game reset and clean exit handling
- Built iteratively across four Agile sprints with a 3 person team

- Language : Java
- UI : JavaFX
- JDK : Oracle OpenJDK 25.0.2
- Process : Agile (4 sprints)

# Gameplay & Validation 
Players alternate placing stones (octagon or rhombus tiles) on the board, with each placement validated against Quax's rules before being committed to the game state. The game continuously checks for a winning connection, an unbroken path of one player's stones linking their two opposing edges and ends the game as soon as one is found. 


# AI Opponent 
The AI opponent selects moves using a heuristic evaluation function that scores potential board states, allowing it to play competitively against human opponents. Its reasoning can be inspected live via the show strategy toggle, which overlays the bot's evaluated candidate moves on the board. 

# How to Play 
1. Launch the game using the launch instructions
2. Click an octagon or rhombus cell to place the first stone
3. Player 2 may optionally activate the pie rule to swap colours
4. Keep alternating placements, click show strategy at any point to see the bot's move evaluation
5. The game ends automatically once a player connects their two edges, click New Game to reset or Exit Game to quit

