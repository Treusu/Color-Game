Android Color Game
A vibrant, Perya-style "Color Game" application for Android. This project simulates the popular carnival board game where players bet on colors and roll three dice to win multipliers based on matches.

Features
Dynamic Dice Rolling – Smooth, real-time color animations for the three dice using a custom Handler logic.

Real-Time Betting System
– Adjust bets with dedicated Increase/Decrease buttons.
– Cash-In system to top up balance and Cash-Out to reset.
– Persistent state handling (Save/Restore) so you don't lose your balance on screen rotation.

Immersive Audio – Integrated SoundPool for low-latency game sounds (including "Let it Ride" and "Let's Go Gambling" memes).

Game History – A scrolling history log to track your wins and losses across rounds.

Smart UI – Material Design components with dynamic button states and color-coded betting indicators.

Tech Stack
Language: Java

Architecture: Decoupled Logic (Separation of MainActivity and GameLogic)

UI Components: ConstraintLayout, MaterialButtons, SharedPreferences (planned), SoundPool

Target SDK: 34+ (Android 14)

How to Play
Cash In – Start by clicking "Cash In" to add ₱100.00 to your balance.

Select a Color – Pick one of the six colors (Red, Blue, Green, Yellow, White, Violet).

Set Your Bet – Use the +/- buttons or type directly into the bet field.

Roll – Hit the "Roll" button!

Payout Multipliers:

1 Match = 2x Payout (Your bet back + 1x win)

2 Matches = 3x Payout

3 Matches = 4x Payout

Setup Instructions
Clone the repository:

bash
git clone https://github.com/YourUsername/ColorGame.git
Open the project in Android Studio.

Add audio files (letitride.raw and letsgogambling.raw) to the res/raw folder.

Build and run on an emulator or physical device.

Roadmap
Multi-Betting – Allow betting on multiple colors at once.

Haptic Feedback – Add vibrations when the dice land.

Global Leaderboards – Firebase integration for high scores.

Animations – Transition to ObjectAnimator for a realistic "spinning" effect.
