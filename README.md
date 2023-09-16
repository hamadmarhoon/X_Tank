# XTank

A multiplayer tank-based game developed in Java.

## Description

XTank is a multiplayer game where players operate tanks on a variety of terrains. Tanks can move in any direction, fire bullets, and even experience collisions with other tanks. The game is implemented with Java and uses the SWT library for its user interface. The server manages communications between clients and makes sure all movements in one tank are reflected across all clients.

## Features

- Multiplayer capabilities
- Real-time movements of tanks
- Bullet animations and shooting mechanics
- Randomized tank starting positions and directions
- Collision detection
- Varied terrains for gameplay

## Files

- `XTankUI`: This is the main UI class where we create the user interface, handle tank movements, bullet animations, and drawing of tanks.
- `XTankServer`: This server class manages communication between clients and ensures synchronization of tank movements.

## Developers

- **Hamad Marhoon** 
- **Abdullah Alkhamis**

## How to Play

1. Start the game client.
2. Use the arrow keys to move your tank around.
3. Press SPACE to shoot a bullet.
4. Avoid getting shot by other players!

## Dependencies

- Java JDK
- SWT Library

## Acknowledgements

- The game uses images for terrains like `gravel.png`, `dclaveau.png`, `grass.png`, and `sand.png`.

## License

This project is licensed under the MIT License.

## Future Enhancements

- Introduce different tank models with varied attributes.
- Incorporate power-ups and health packs.
- Introduce team-based gameplay and objectives.
