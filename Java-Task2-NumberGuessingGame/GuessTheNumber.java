import javax.swing.JOptionPane;
import java.util.Random;

public class GuessTheNumber {
    private static final int MIN_NUMBER = 1;
    private static final int MAX_NUMBER = 100;
    private static final int MAX_ATTEMPTS = 10;

    public static void main(String[] args) {
        Random random = new Random();

        JOptionPane.showMessageDialog(
                null,
                "Welcome to Guess the Number!\n" +
                "I will pick a number between " + MIN_NUMBER + " and " + MAX_NUMBER + ".\n" +
                "You get " + MAX_ATTEMPTS + " attempts per round.");

        int rounds = askForPositiveInt("How many rounds would you like to play?");
        int totalScore = 0;
        int roundsWon = 0;

        for (int round = 1; round <= rounds; round++) {
            int secretNumber = random.nextInt(MAX_NUMBER - MIN_NUMBER + 1) + MIN_NUMBER;
            int attemptsUsed = 0;
            boolean guessedCorrectly = false;

            JOptionPane.showMessageDialog(
                    null,
                    "Round " + round + " of " + rounds + "\n" +
                    "Try to guess the number between " + MIN_NUMBER + " and " + MAX_NUMBER + ".");

            while (attemptsUsed < MAX_ATTEMPTS && !guessedCorrectly) {
                int remainingAttempts = MAX_ATTEMPTS - attemptsUsed;
                int guess = askForGuess(
                        "Round " + round + "\n" +
                        "Attempts left: " + remainingAttempts + "\n" +
                        "Enter your guess:");

                attemptsUsed++;

                if (guess < secretNumber) {
                    JOptionPane.showMessageDialog(null, "Too low! Try a higher number.");
                } else if (guess > secretNumber) {
                    JOptionPane.showMessageDialog(null, "Too high! Try a lower number.");
                } else {
                    guessedCorrectly = true;
                    roundsWon++;
                    int roundScore = MAX_ATTEMPTS - attemptsUsed + 1;
                    totalScore += roundScore;

                    JOptionPane.showMessageDialog(
                            null,
                            "Correct! You guessed the number in " + attemptsUsed + " attempt(s).\n" +
                            "You earned " + roundScore + " point(s) this round.");
                }
            }

            if (!guessedCorrectly) {
                JOptionPane.showMessageDialog(
                        null,
                        "Out of attempts! The correct number was " + secretNumber + ".");
            }
        }

        JOptionPane.showMessageDialog(
                null,
                "Game over!\n" +
                "Rounds played: " + rounds + "\n" +
                "Rounds won: " + roundsWon + "\n" +
                "Total score: " + totalScore);
    }

    private static int askForPositiveInt(String message) {
        while (true) {
            String input = JOptionPane.showInputDialog(null, message);
            if (input == null) {
                JOptionPane.showMessageDialog(null, "Thanks for playing!");
                System.exit(0);
            }

            try {
                int value = Integer.parseInt(input.trim());
                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // Fall through to the error message below.
            }

            JOptionPane.showMessageDialog(null, "Please enter a positive whole number.");
        }
    }

    private static int askForGuess(String message) {
        while (true) {
            String input = JOptionPane.showInputDialog(null, message);
            if (input == null) {
                JOptionPane.showMessageDialog(null, "Thanks for playing!");
                System.exit(0);
            }

            try {
                int guess = Integer.parseInt(input.trim());
                if (guess >= MIN_NUMBER && guess <= MAX_NUMBER) {
                    return guess;
                }
            } catch (NumberFormatException ignored) {
                // Fall through to the error message below.
            }

            JOptionPane.showMessageDialog(
                    null,
                    "Please enter a number between " + MIN_NUMBER + " and " + MAX_NUMBER + ".");
        }
    }
}
