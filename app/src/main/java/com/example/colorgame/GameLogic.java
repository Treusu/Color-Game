package com.example.colorgame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GameLogic implements Serializable {
    public enum GameColor {
        RED("#F44336", "Red"),
        BLUE("#2196F3", "Blue"),
        GREEN("#4CAF50", "Green"),
        YELLOW("#FFEB3B", "Yellow"),
        WHITE("#FFFFFF", "White"),
        VIOLET("#9C27B0", "Violet");

        public final String hex;
        public final String name;

        GameColor(String hex, String name) {
            this.hex = hex;
            this.name = name;
        }
    }

    public static class GameResult implements Serializable {
        public final GameColor selectedColor;
        public final GameColor[] diceResults;
        public final double betAmount;
        public final double winAmount;

        public GameResult(GameColor selectedColor, GameColor[] diceResults, double betAmount, double winAmount) {
            this.selectedColor = selectedColor;
            this.diceResults = diceResults;
            this.betAmount = betAmount;
            this.winAmount = winAmount;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(selectedColor.name).append(": ");
            for (GameColor color : diceResults) {
                sb.append(getColorEmoji(color)).append(" ");
            }
            if (winAmount > 0) {
                sb.append("+₱").append(String.format(Locale.US, "%.2f", winAmount));
            } else {
                sb.append("-₱").append(String.format(Locale.US, "%.2f", betAmount));
            }
            return sb.toString();
        }

        private String getColorEmoji(GameColor color) {
            switch (color) {
                case RED: return "🟥";
                case BLUE: return "🟦";
                case GREEN: return "🟩";
                case YELLOW: return "🟨";
                case WHITE: return "⬜";
                case VIOLET: return "🟪";
                default: return "❓";
            }
        }
    }

    private double balance;
    private double currentBet;
    private GameColor selectedColor;
    private List<GameResult> history;
    private final Random random;

    public GameLogic() {
        this.balance = 0.00; // Start at 0 for cash-in simulation
        this.currentBet = 1.00;
        this.selectedColor = null;
        this.history = new ArrayList<>();
        this.random = new Random();
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void cashIn(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }

    public double cashOut() {
        double amount = this.balance;
        this.balance = 0;
        this.currentBet = 1.0;
        return amount;
    }

    public double getCurrentBet() {
        return currentBet;
    }

    public void setCurrentBet(double bet) {
        this.currentBet = bet;
    }

    public GameColor getSelectedColor() {
        return selectedColor;
    }

    public List<GameResult> getHistory() {
        return history;
    }

    public void setSelectedColor(GameColor color) {
        this.selectedColor = color;
    }

    public void setHistory(List<GameResult> history) {
        this.history = history;
    }

    public boolean increaseBet() {
        if (currentBet < balance) {
            currentBet += 1.0;
            return true;
        }
        return false;
    }

    public boolean decreaseBet() {
        if (currentBet > 1.0) {
            currentBet -= 1.0;
            return true;
        }
        return false;
    }

    public GameResult rollDice() {
        if (selectedColor == null || currentBet > balance || currentBet <= 0) {
            return null;
        }

        GameColor[] results = new GameColor[3];
        int matches = 0;
        for (int i = 0; i < 3; i++) {
            results[i] = GameColor.values()[random.nextInt(GameColor.values().length)];
            if (results[i] == selectedColor) {
                matches++;
            }
        }

        double winAmount = 0;
        if (matches > 0) {
            winAmount = currentBet * matches;
            balance += winAmount;
        } else {
            balance -= currentBet;
            winAmount = -currentBet;
        }

        if (currentBet > balance) {
            currentBet = Math.max(0, balance);
        }

        GameResult result = new GameResult(selectedColor, results, Math.abs(currentBet), Math.max(0, winAmount));
        history.add(0, result);
        if (history.size() > 10) {
            history.remove(history.size() - 1);
        }

        return result;
    }
}
