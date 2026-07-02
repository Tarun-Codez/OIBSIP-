import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class OnlineExamination {
    private static final String DEFAULT_USERNAME = "student";
    private static final String DEFAULT_PASSWORD = "student123";
    private static final int EXAM_DURATION_SECONDS = 300;

    private final JFrame frame = new JFrame("Online Examination System");
    private final CardLayout rootLayout = new CardLayout();
    private final JPanel rootPanel = new JPanel(rootLayout);

    private final JTextField loginUsernameField = new JTextField(18);
    private final JPasswordField loginPasswordField = new JPasswordField(18);
    private final JLabel loginMessageLabel = new JLabel(" ");

    private final JTextField usernameField = new JTextField(22);
    private final JTextField fullNameField = new JTextField(22);
    private final JTextField emailField = new JTextField(22);
    private final JPasswordField currentPasswordField = new JPasswordField(22);
    private final JPasswordField newPasswordField = new JPasswordField(22);
    private final JPasswordField confirmPasswordField = new JPasswordField(22);
    private final JLabel profileMessageLabel = new JLabel(" ");

    private final JLabel timerLabel = new JLabel("Time Left: 00:00");
    private final JLabel examStatusLabel = new JLabel(" ");
    private final JButton submitButton = new JButton("Submit Exam");
    private final JButton logoutButton = new JButton("Logout");
    private final JButton closeSessionButton = new JButton("Close Session");

    private final List<Question> questions = buildQuestions();
    private final List<QuestionUI> questionUIs = new ArrayList<>();

    private Timer timer;
    private int remainingSeconds = EXAM_DURATION_SECONDS;
    private boolean sessionActive;
    private boolean submitted;

    private String storedUsername = DEFAULT_USERNAME;
    private String storedPassword = DEFAULT_PASSWORD;
    private String storedFullName = "Demo Student";
    private String storedEmail = "student@example.com";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OnlineExamination().showUI());
    }

    private void showUI() {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1120, 780));
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                handleWindowClose();
            }
        });

        rootPanel.add(buildLoginScreen(), "LOGIN");
        rootPanel.add(buildDashboardScreen(), "DASHBOARD");
        frame.add(rootPanel, BorderLayout.CENTER);

        rootLayout.show(rootPanel, "LOGIN");
        frame.setVisible(true);
    }

    private JPanel buildLoginScreen() {
        JPanel page = createPage("Login");

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        addFieldRow(center, gbc, "Username", loginUsernameField);
        addFieldRow(center, gbc, "Password", loginPasswordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> login());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttons.setOpaque(false);
        buttons.add(loginButton);

        JLabel hint = new JLabel("Default credentials: student / student123");
        hint.setForeground(new Color(95, 108, 121));

        loginMessageLabel.setForeground(new Color(190, 56, 52));

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(hint, BorderLayout.WEST);
        footer.add(loginMessageLabel, BorderLayout.EAST);

        page.add(center, BorderLayout.CENTER);
        page.add(buttons, BorderLayout.SOUTH);
        page.add(footer, BorderLayout.NORTH);
        return page;
    }

    private JPanel buildDashboardScreen() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(new Color(245, 247, 250));

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(16, 18, 16, 18));
        header.setBackground(new Color(31, 78, 121));

        JLabel title = new JLabel("Online Examination Dashboard");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(timerLabel.getFont().deriveFont(Font.BOLD, 18f));

        logoutButton.addActionListener(e -> logout());
        closeSessionButton.addActionListener(e -> closeSession());

        JPanel headerButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerButtons.setOpaque(false);
        headerButtons.add(timerLabel);
        headerButtons.add(logoutButton);
        headerButtons.add(closeSessionButton);

        header.add(title, BorderLayout.WEST);
        header.add(headerButtons, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Profile & Password", buildProfilePanel());
        tabs.addTab("MCQ Exam", buildExamPanel());

        dashboard.add(header, BorderLayout.NORTH);
        dashboard.add(tabs, BorderLayout.CENTER);
        return dashboard;
    }

    private JPanel buildProfilePanel() {
        JPanel page = createPage("Update Profile and Password");
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        usernameField.setEditable(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        addFieldRow(form, gbc, "Username", usernameField);
        addFieldRow(form, gbc, "Full Name", fullNameField);
        addFieldRow(form, gbc, "Email", emailField);
        addFieldRow(form, gbc, "Current Password", currentPasswordField);
        addFieldRow(form, gbc, "New Password", newPasswordField);
        addFieldRow(form, gbc, "Confirm Password", confirmPasswordField);

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> saveProfile());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        actions.setOpaque(false);
        actions.add(saveButton);

        profileMessageLabel.setForeground(new Color(28, 106, 62));

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(actions, BorderLayout.CENTER);
        footer.add(profileMessageLabel, BorderLayout.SOUTH);

        page.add(form, BorderLayout.CENTER);
        page.add(footer, BorderLayout.SOUTH);
        return page;
    }

    private JPanel buildExamPanel() {
        JPanel page = createPage("MCQ Examination");

        JPanel questionsPanel = new JPanel();
        questionsPanel.setLayout(new javax.swing.BoxLayout(questionsPanel, javax.swing.BoxLayout.Y_AXIS));
        questionsPanel.setOpaque(false);

        questionUIs.clear();
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            QuestionUI ui = new QuestionUI(question);
            questionUIs.add(ui);

            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(221, 227, 234)),
                    new EmptyBorder(10, 12, 10, 12)));

            JLabel questionLabel = new JLabel((i + 1) + ". " + question.text);
            questionLabel.setFont(questionLabel.getFont().deriveFont(Font.BOLD, 15f));
            questionLabel.setBorder(new EmptyBorder(4, 4, 8, 4));

            JPanel options = new JPanel(new GridBagLayout());
            options.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(4, 10, 4, 10);
            gbc.gridx = 0;
            gbc.gridy = 0;

            for (String option : question.options) {
                JRadioButton button = new JRadioButton(option);
                button.setOpaque(false);
                ui.group.add(button);
                ui.buttons.add(button);
                options.add(button, gbc);
                gbc.gridy++;
            }

            card.add(questionLabel, BorderLayout.NORTH);
            card.add(options, BorderLayout.CENTER);
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
            questionsPanel.add(card);
            questionsPanel.add(Box.createVerticalStrut(12));
        }

        JScrollPane scrollPane = new JScrollPane(questionsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        submitButton.addActionListener(e -> submitExam(false));

        examStatusLabel.setForeground(new Color(38, 99, 72));

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(submitButton, BorderLayout.EAST);
        footer.add(examStatusLabel, BorderLayout.WEST);

        page.add(scrollPane, BorderLayout.CENTER);
        page.add(footer, BorderLayout.SOUTH);
        return page;
    }

    private void login() {
        String enteredUsername = loginUsernameField.getText().trim();
        String enteredPassword = new String(loginPasswordField.getPassword());

        if (enteredUsername.equals(storedUsername) && enteredPassword.equals(storedPassword)) {
            sessionActive = true;
            submitted = false;
            remainingSeconds = EXAM_DURATION_SECONDS;
            loginMessageLabel.setText(" ");
            loadSessionData();
            resetExamControls();
            startTimer();
            rootLayout.show(rootPanel, "DASHBOARD");
            examStatusLabel.setText("Exam started. Answer carefully before time runs out.");
            timerLabel.setText(formatTime(remainingSeconds));
        } else {
            loginMessageLabel.setText("Invalid username or password.");
        }
    }

    private void loadSessionData() {
        usernameField.setText(storedUsername);
        fullNameField.setText(storedFullName);
        emailField.setText(storedEmail);
        currentPasswordField.setText(storedPassword);
        newPasswordField.setText("");
        confirmPasswordField.setText("");
        profileMessageLabel.setText(" ");
    }

    private void saveProfile() {
        if (!sessionActive) {
            profileMessageLabel.setForeground(new Color(190, 56, 52));
            profileMessageLabel.setText("Please login first.");
            return;
        }

        String currentPassword = new String(currentPasswordField.getPassword());
        if (!currentPassword.equals(storedPassword)) {
            profileMessageLabel.setForeground(new Color(190, 56, 52));
            profileMessageLabel.setText("Current password is incorrect.");
            return;
        }

        String updatedUsername = usernameField.getText().trim();
        String updatedFullName = fullNameField.getText().trim();
        String updatedEmail = emailField.getText().trim();
        String updatedPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (updatedUsername.isEmpty() || updatedFullName.isEmpty() || updatedEmail.isEmpty()) {
            profileMessageLabel.setForeground(new Color(190, 56, 52));
            profileMessageLabel.setText("Username, full name, and email cannot be empty.");
            return;
        }

        if (!updatedPassword.isEmpty() || !confirmPassword.isEmpty()) {
            if (updatedPassword.length() < 6) {
                profileMessageLabel.setForeground(new Color(190, 56, 52));
                profileMessageLabel.setText("New password must be at least 6 characters long.");
                return;
            }
            if (!updatedPassword.equals(confirmPassword)) {
                profileMessageLabel.setForeground(new Color(190, 56, 52));
                profileMessageLabel.setText("New password and confirmation do not match.");
                return;
            }
            storedPassword = updatedPassword;
        }

        storedUsername = updatedUsername;
        storedFullName = updatedFullName;
        storedEmail = updatedEmail;
        currentPasswordField.setText(storedPassword);
        newPasswordField.setText("");
        confirmPasswordField.setText("");

        profileMessageLabel.setForeground(new Color(28, 106, 62));
        profileMessageLabel.setText("Profile updated successfully.");
    }

    private void startTimer() {
        stopTimer();
        timer = new Timer(1000, e -> {
            if (!sessionActive || submitted) {
                return;
            }

            remainingSeconds--;
            timerLabel.setText(formatTime(remainingSeconds));
            if (remainingSeconds <= 0) {
                submitExam(true);
            }
        });
        timer.setInitialDelay(1000);
        timer.start();
    }

    private void submitExam(boolean autoSubmitted) {
        if (!sessionActive || submitted) {
            return;
        }

        submitted = true;
        stopTimer();

        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            int selected = questionUIs.get(i).getSelectedIndex();
            if (selected == questions.get(i).correctIndex) {
                score++;
            }
        }

        disableExamInputs(true);
        examStatusLabel.setForeground(new Color(28, 68, 136));
        examStatusLabel.setText("Final Score: " + score + " / " + questions.size() +
                (autoSubmitted ? " (auto-submitted)" : ""));

        JOptionPane.showMessageDialog(
                frame,
                "Exam submitted successfully.\nScore: " + score + " / " + questions.size() +
                        (autoSubmitted ? "\nThe timer expired, so the system auto-submitted the paper." : ""),
                "Exam Result",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
                frame,
                "Logout and close the current session?",
                "Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            closeSession();
        }
    }

    private void closeSession() {
        stopTimer();
        sessionActive = false;
        submitted = false;
        remainingSeconds = EXAM_DURATION_SECONDS;
        loginUsernameField.setText("");
        loginPasswordField.setText("");
        loginMessageLabel.setText(" ");
        profileMessageLabel.setText(" ");
        examStatusLabel.setText(" ");
        timerLabel.setText("Time Left: 00:00");
        clearExamSelections();
        disableExamInputs(false);
        rootLayout.show(rootPanel, "LOGIN");
    }

    private void handleWindowClose() {
        if (sessionActive) {
            int choice = JOptionPane.showConfirmDialog(
                    frame,
                    "A session is active. Do you want to close it and exit?",
                    "Exit Application",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }

        stopTimer();
        frame.dispose();
    }

    private void resetExamControls() {
        clearExamSelections();
        disableExamInputs(false);
        timerLabel.setText(formatTime(remainingSeconds));
    }

    private void clearExamSelections() {
        for (QuestionUI ui : questionUIs) {
            ui.group.clearSelection();
            for (JRadioButton button : ui.buttons) {
                button.setEnabled(true);
            }
        }
    }

    private void disableExamInputs(boolean disabled) {
        for (QuestionUI ui : questionUIs) {
            for (JRadioButton button : ui.buttons) {
                button.setEnabled(!disabled);
            }
        }
        submitButton.setEnabled(!disabled);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    private JPanel createPage(String titleText) {
        JPanel page = new JPanel(new BorderLayout(0, 14));
        page.setBackground(new Color(245, 247, 250));
        page.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel title = new JLabel(titleText);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        title.setForeground(new Color(34, 44, 58));
        page.add(title, BorderLayout.NORTH);
        return page;
    }

    private void addFieldRow(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setForeground(new Color(50, 60, 71));

        gbc.gridx = 0;
        panel.add(label, gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
        gbc.gridy++;
    }

    private String formatTime(int seconds) {
        Duration duration = Duration.ofSeconds(Math.max(seconds, 0));
        long minutes = duration.toMinutes();
        long remaining = duration.minusMinutes(minutes).getSeconds();
        return String.format("Time Left: %02d:%02d", minutes, remaining);
    }

    private List<Question> buildQuestions() {
        List<Question> list = new ArrayList<>();
        list.add(new Question(
                "Which keyword is used to inherit a class in Java?",
                new String[]{"extends", "implements", "inherits", "instanceof"},
                0));
        list.add(new Question(
                "Which collection stores unique values only?",
                new String[]{"List", "Set", "Map", "Queue"},
                1));
        list.add(new Question(
                "What does HTML stand for?",
                new String[]{"HyperText Markup Language", "HighText Machine Language", "HyperText Markdown Language", "Home Tool Markup Language"},
                0));
        list.add(new Question(
                "Which symbol is used for single-line comments in Java?",
                new String[]{"//", "/*", "<!--", "##"},
                0));
        list.add(new Question(
                "Which method starts a Java application?",
                new String[]{"run()", "start()", "main()", "execute()"},
                2));
        return list;
    }

    private static final class Question {
        private final String text;
        private final String[] options;
        private final int correctIndex;

        private Question(String text, String[] options, int correctIndex) {
            this.text = text;
            this.options = options;
            this.correctIndex = correctIndex;
        }
    }

    private static final class QuestionUI {
        private final ButtonGroup group = new ButtonGroup();
        private final List<JRadioButton> buttons = new ArrayList<>();

        private QuestionUI(Question question) {
        }

        private int getSelectedIndex() {
            for (int i = 0; i < buttons.size(); i++) {
                if (buttons.get(i).isSelected()) {
                    return i;
                }
            }
            return -1;
        }
    }
}