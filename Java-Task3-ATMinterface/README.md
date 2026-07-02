# Online Examination System

`OnlineExamination.java` is a self-contained Java Swing application that simulates a small online exam portal. It includes login, profile/password management, multiple-choice questions, a countdown timer with auto-submit, and session/logout handling.

## Features

- Secure-style **login screen** with a demo student account
- **Update profile** details such as username, full name, and email
- **Change password** directly from the application
- **Answer MCQs** using radio-button selections
- **Countdown timer** displayed in the dashboard header
- **Auto submit** when the timer reaches zero
- **Submit manually** before time expires
- **Close session / Logout** to return to the login screen safely

## Demo credentials

Use these credentials to sign in:

- **Username:** `student`
- **Password:** `student123`

You can change the password from the profile tab after logging in.

## Project files

- `OnlineExamination.java` — the main application file containing the full user interface and exam logic

## Requirements

- Java Development Kit (JDK) 8 or later
- A desktop environment capable of running Java Swing applications

## How to run

Compile:

```text
javac OnlineExamination.java
```

Run:

```text
java OnlineExamination
```

## Application flow

1. Start the application.
2. Log in using the demo credentials.
3. Open **Profile & Password** to update profile details or change the password.
4. Switch to **MCQ Exam** and select one answer per question.
5. Watch the timer in the dashboard header.
6. Submit the exam manually, or let the app auto-submit at timeout.
7. Use **Logout** or **Close Session** to end the current session and return to the login screen.

## Exam behavior

- The timer starts immediately after a successful login.
- The exam duration is set to **5 minutes** by default.
- When the timer hits zero, the system automatically submits the current selections.
- After submission, answer controls are disabled and the final score is shown.

## Customization

You can easily tailor the project by editing `OnlineExamination.java`:

- Change `EXAM_DURATION_SECONDS` to adjust the time limit.
- Edit `buildQuestions()` to add, remove, or replace MCQs.
- Update the default profile values for a different demo user.
- Wire the login and profile data to a database later if you want persistence.

## Notes

- This is a desktop Java Swing app, not a web application.
- The project is intentionally self-contained so it can be compiled and run from a single file.
- If you want a next step, this can be extended with database-backed users, stored test results, and question banks.

---

# ATM Interface

`ATMInterface.java` is a console-based ATM simulation that supports login, transaction history, withdraw, deposit, transfer, and quit actions.

## Demo credentials

- **User ID:** `123456`
- **PIN:** `1234`

To test transfers, you can send money to account `654321`.

## Project files

- `ATMInterface.java` — main console application with the ATM workflow and demo accounts

## How to run

Compile:

```text
javac ATMInterface.java
```

Run:

```text
java ATMInterface
```

## Available operations

1. View transaction history
2. Withdraw cash
3. Deposit cash
4. Transfer funds
5. Quit session

## Notes

- The app starts by prompting for user ID and PIN.
- Successful login unlocks the ATM menu.
- Transaction history is recorded for deposits, withdrawals, and transfers.
- Demo accounts are hardcoded for easy testing.
