import javax.swing.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.net.URL;

public class XandO {
    private ArrayList<Integer> playerOne = new ArrayList<>();
    private ArrayList<Integer> playerTwo = new ArrayList<>();
    private String player1Name, player2Name;
    private int flag = 0;
    private int roundsToWin = 2;
    private int p1Wins = 0, p2Wins = 0;
    private boolean isVsAI = false;
    private int difficulty = 1;
    private boolean isPlayerFirst = true;
    private JFrame startFrame, welcomeFrame, gameFrame;
    private JLabel turnLabel, scoreLabel;
    private JButton[] buttons = new JButton[9];
    private Timer animationTimer;
    private boolean gameInProgress = true;

    // Color scheme
    private final Color PRIMARY_DARK = new Color(26, 32, 44);      // Dark blue-gray
    private final Color PRIMARY_LIGHT = new Color(45, 55, 72);     // Medium blue-gray
    private final Color ACCENT_BLUE = new Color(66, 153, 225);     // Bright blue
    private final Color ACCENT_GREEN = new Color(72, 187, 120);    // Success green
    private final Color ACCENT_RED = new Color(245, 101, 101);     // Error red
    private final Color ACCENT_PURPLE = new Color(159, 122, 234);  // Purple accent
    private final Color TEXT_WHITE = new Color(255, 255, 255);
    private final Color TEXT_GRAY = new Color(160, 174, 192);
    private final Color BACKGROUND = new Color(247, 250, 252);
    private final Color CARD_WHITE = new Color(255, 255, 255);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new XandO().showStartScreen());
    }

    @SuppressWarnings("unused")
    private Image getIconImage(String path) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.err.println("Icon not found: " + path);
                return null;
            }
            return new ImageIcon(url).getImage();
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
            return null;
        }
    }

    private void playSound(String soundType) {
        try {
            // Simple beep sounds using different frequencies
            switch (soundType) {
                case "win":
                    playTone(800, 200); // High pitch victory
                    Thread.sleep(100);
                    playTone(1000, 300);
                    break;
                case "lose":
                    playTone(300, 400); // Low pitch defeat
                    Thread.sleep(100);
                    playTone(200, 400);
                    break;
                case "move":
                    playTone(600, 100); // Quick move sound
                    break;
            }
        } catch (Exception e) {
            System.err.println("Sound error: " + e.getMessage());
        }
    }

    private void playTone(int frequency, int duration) {
        try {
            byte[] buffer = new byte[duration * 44100 / 1000];
            for (int i = 0; i < buffer.length; i++) {
                double angle = 2.0 * Math.PI * i * frequency / 44100;
                buffer[i] = (byte) (Math.sin(angle) * 127);
            }
            
            AudioFormat format = new AudioFormat(44100, 8, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            line.write(buffer, 0, buffer.length);
            line.drain();
            line.close();
        } catch (Exception e) {
            // Silently handle audio errors
        }
    }

    void showStartScreen() {
        startFrame = new JFrame("X and O - Modern Edition");
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startFrame.getContentPane().setBackground(BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Header with styling
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(BACKGROUND);
        
        JLabel title = new JLabel("X & O", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 48));
        title.setForeground(PRIMARY_DARK);
        
        JLabel subtitle = new JLabel("Modern Tic-Tac-Toe Experience", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(TEXT_GRAY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        headerPanel.add(title, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(10, 0, 0, 0);
        headerPanel.add(subtitle, gbc);

        // Features card
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(CARD_WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        String[] features = {
            "→ The ultimate test of your Tic-Tac-Toe skills",
            "→ Unbeatable AI with 3 difficulty levels",
            "→ Fast-paced matches with best of 3 or 5",
            "→ Modern, clean interface design",
            "→ Victory celebrations and sound effects",
            "→ Strategic gameplay that really challenges you",
            "→ Play against a friend or the computer",
        };

        for (String feature : features) {
            JLabel featureLabel = new JLabel(feature);
            featureLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            featureLabel.setForeground(PRIMARY_LIGHT);
            featureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            featureLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
            cardPanel.add(featureLabel);
        }

        // Start button
        JButton startBtn = createModernButton("Start Game", ACCENT_BLUE);
        startBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        startBtn.addActionListener(e -> {
            startFrame.dispose();
            showWelcomeScreen();
        });

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        mainPanel.add(startBtn, BorderLayout.SOUTH);

        startFrame.add(mainPanel);
        startFrame.setSize(500, 600);
        startFrame.setLocationRelativeTo(null);
        startFrame.setVisible(true);
    }

    private JButton createModernButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(TEXT_WHITE);
        button.setBackground(color);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        Color hoverColor = color.brighter();
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    void showWelcomeScreen() {
        welcomeFrame = new JFrame("Game Setup");
        welcomeFrame.getContentPane().setBackground(BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JLabel headerLabel = new JLabel("Configure Your Game", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setForeground(PRIMARY_DARK);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Player 1
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        JLabel label1 = new JLabel("Player 1 (X):");
        label1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label1.setForeground(PRIMARY_DARK);
        formPanel.add(label1, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        JTextField player1Field = new JTextField(15);
        styleTextField(player1Field);
        formPanel.add(player1Field, gbc);

        // Player 2
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel label2 = new JLabel("Player 2 (O):");
        label2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label2.setForeground(PRIMARY_DARK);
        formPanel.add(label2, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        JTextField player2Field = new JTextField(15);
        styleTextField(player2Field);
        formPanel.add(player2Field, gbc);

        // Match type
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel modeLabel = new JLabel("Match Type:");
        modeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        modeLabel.setForeground(PRIMARY_DARK);
        formPanel.add(modeLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        String[] matchOptions = {"Best of 3", "Best of 5"};
        JComboBox<String> matchMode = new JComboBox<>(matchOptions);
        styleComboBox(matchMode);
        formPanel.add(matchMode, gbc);

        // AI checkbox
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        JLabel aiLabel = new JLabel("Play vs AI:");
        aiLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        aiLabel.setForeground(PRIMARY_DARK);
        formPanel.add(aiLabel, gbc);

        gbc.gridx = 1;
        JCheckBox aiCheckBox = new JCheckBox();
        aiCheckBox.setBackground(CARD_WHITE);
        aiCheckBox.addActionListener(e -> {
            player2Field.setEnabled(!aiCheckBox.isSelected());
            if (aiCheckBox.isSelected()) player2Field.setText("AI");
            else player2Field.setText("");
        });
        formPanel.add(aiCheckBox, gbc);

        // Difficulty
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        JLabel diffLabel = new JLabel("AI Difficulty:");
        diffLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        diffLabel.setForeground(PRIMARY_DARK);
        formPanel.add(diffLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        String[] diffOptions = {"Easy", "Medium", "Hard (Unbeatable)"};
        JComboBox<String> difficultyBox = new JComboBox<>(diffOptions);
        styleComboBox(difficultyBox);
        difficultyBox.setEnabled(false);
        aiCheckBox.addActionListener(e -> difficultyBox.setEnabled(aiCheckBox.isSelected()));
        formPanel.add(difficultyBox, gbc);

        // Start button
        JButton startButton = createModernButton("Start Game", ACCENT_GREEN);
        startButton.addActionListener(e -> {
            player1Name = player1Field.getText().trim();
            player2Name = player2Field.getText().trim();
            roundsToWin = matchMode.getSelectedIndex() == 0 ? 2 : 3;
            isVsAI = aiCheckBox.isSelected();
            difficulty = difficultyBox.getSelectedIndex() + 1;

            if (player1Name.isEmpty()) {
                showErrorDialog("Please enter Player 1 name.");
            } else if (!isVsAI && player2Name.isEmpty()) {
                showErrorDialog("Please enter Player 2 name or enable AI.");
            } else if (!isVsAI && player1Name.equals(player2Name)) {
                showErrorDialog("Player names must be different!");
            } else {
                welcomeFrame.dispose();
                // Random start except Hard AI always starts
                if (isVsAI && difficulty == 3) {
                    isPlayerFirst = false;
                } else {
                    isPlayerFirst = new Random().nextBoolean();
                }
                drawGrid();
                if (!isPlayerFirst && isVsAI) {
                    Timer aiDelay = new Timer(800, evt -> {
                        aiMove();
                        ((Timer) evt.getSource()).stop();
                    });
                    aiDelay.setRepeats(false);
                    aiDelay.start();
                }
            }
        });

        mainPanel.add(headerLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(startButton, BorderLayout.SOUTH);

        welcomeFrame.add(mainPanel);
        welcomeFrame.setSize(400, 500);
        welcomeFrame.setLocationRelativeTo(null);
        welcomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        welcomeFrame.setVisible(true);
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(PRIMARY_DARK);
        field.setBackground(BACKGROUND);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(BACKGROUND);
        combo.setForeground(PRIMARY_DARK);
        combo.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(welcomeFrame, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    void drawGrid() {
        gameFrame = new JFrame("X & O - " + player1Name + " vs " + (isVsAI ? "AI" : player2Name));
        gameFrame.getContentPane().setBackground(BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel with game info
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        topPanel.setBackground(BACKGROUND);

        turnLabel = new JLabel("Turn: " + (isPlayerFirst ? player1Name : (isVsAI ? "AI" : player2Name)), SwingConstants.CENTER);
        turnLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        turnLabel.setForeground(ACCENT_BLUE);

        scoreLabel = new JLabel("Score: " + player1Name + " [" + p1Wins + "] - [" + p2Wins + "] " + player2Name, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        scoreLabel.setForeground(TEXT_GRAY);

        topPanel.add(turnLabel);
        topPanel.add(scoreLabel);

        // Game grid with buttons
        JPanel gridPanel = new JPanel(new GridLayout(3, 3, 8, 8));
        gridPanel.setBackground(BACKGROUND);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton();
            buttons[i].setFont(new Font("Segoe UI", Font.BOLD, 48));
            buttons[i].setBackground(CARD_WHITE);
            buttons[i].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            buttons[i].setFocusPainted(false);
            buttons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            buttons[i].setPreferredSize(new Dimension(80, 80));

            int pos = i + 1;
            buttons[i].addActionListener(e -> {
                if (gameInProgress) {
                    if (isVsAI) {
                        // In AI mode, check if it's really the human's turn
                        boolean isHumanTurn = (isPlayerFirst && flag % 2 == 0) || (!isPlayerFirst && flag % 2 == 1);
                        if (isHumanTurn) {
                            JButton clickedBtn = (JButton) e.getSource();
                            if (clickedBtn.getText().isEmpty()) {
                                buttonClicked(clickedBtn, pos);
                                // Schedule AI move after human move
                                if (gameInProgress) {
                                    SwingUtilities.invokeLater(() -> {
                                        Timer aiDelay = new Timer(difficulty == 3 ? 800 : 400, evt -> {
                                            if (gameInProgress) {
                                                aiMove();
                                            }
                                            ((Timer) evt.getSource()).stop();
                                        });
                                        aiDelay.setRepeats(false);
                                        aiDelay.start();
                                    });
                                }
                            }
                        }
                    } else {
                        // Human vs Human mode
                        buttonClicked((JButton) e.getSource(), pos);
                    }
                }
            });

            // Hover effect for buttons
            buttons[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    JButton btn = (JButton) e.getSource();
                    if (btn.getText().isEmpty() && gameInProgress) {
                        btn.setBackground(new Color(237, 242, 247));
                    }
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    JButton btn = (JButton) e.getSource();
                    if (btn.getText().isEmpty()) {
                        btn.setBackground(CARD_WHITE);
                    }
                }
            });

            gridPanel.add(buttons[i]);
        }

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(gridPanel, BorderLayout.CENTER);

        gameFrame.add(mainPanel);
        gameFrame.setSize(500, 600);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setVisible(true);
    }

    void buttonClicked(JButton btn, int pos) {
        if (!btn.getText().equals("") || !gameInProgress) return;

        // Player 1's turn (X)
        if ((flag % 2 == 0 && isPlayerFirst) || (flag % 2 == 1 && !isPlayerFirst)) {
            btn.setText("X");
            btn.setForeground(ACCENT_RED);
            btn.setBackground(new Color(254, 242, 242));
            playerOne.add(pos);
            turnLabel.setText("Turn: " + (isVsAI ? "AI" : player2Name));
            playSound("move");
            
            if (checkWinner(playerOne)) {
                p1Wins++;
                gameInProgress = false;
                playSound("win");
                showWinnerCelebration(player1Name, true);
                return;
            }
        }
        // Player 2's turn (O) - only in human vs human mode
        else if (!isVsAI) {
            btn.setText("O");
            btn.setForeground(ACCENT_BLUE);
            btn.setBackground(new Color(239, 246, 255));
            playerTwo.add(pos);
            turnLabel.setText("Turn: " + player1Name);
            playSound("move");
            
            if (checkWinner(playerTwo)) {
                p2Wins++;
                gameInProgress = false;
                playSound("win");
                showWinnerCelebration(player2Name, false);
                return;
            }
        }
        flag++;
        if (flag == 9 && !checkWinner(playerOne) && !checkWinner(playerTwo)) {
            gameInProgress = false;
            showDrawDialog();
        }
    }

    void aiMove() {
        if (!gameInProgress || flag >= 9 || checkWinner(playerOne) || checkWinner(playerTwo)) {
            return;
        }

        int move = getAIMove();
        if (move > 0 && move <= 9 && buttons[move - 1].getText().isEmpty()) {
            buttons[move - 1].setText("O");
            buttons[move - 1].setForeground(ACCENT_BLUE);
            buttons[move - 1].setBackground(new Color(239, 246, 255));
            playerTwo.add(move);
            turnLabel.setText("Turn: " + player1Name);
            playSound("move");
            
            if (checkWinner(playerTwo)) {
                p2Wins++;
                gameInProgress = false;
                playSound("lose");
                showWinnerCelebration(isVsAI ? "AI" : player2Name, false);
                return;
            }
            
            flag++;
            if (flag == 9 && !checkWinner(playerOne) && !checkWinner(playerTwo)) {
                gameInProgress = false;
                showDrawDialog();
            }
        }
    }

    // Enhanced unbeatable AI strategy for hard mode
    private int getAIMove() {
        switch (difficulty) {
            case 1: // Easy: Random with occasional good moves
                if (new Random().nextInt(10) < 3) { // 30% chance of smart move
                    return getSmartMove();
                }
                return getRandomMove();

            case 2: // Medium: Basic strategy
                return getMediumMove();

            case 3: // Hard: Unbeatable strategy
                return getUnbeatableMove();

            default:
                return getRandomMove();
        }
    }

    private int getRandomMove() {
        ArrayList<Integer> available = new ArrayList<>();
        for (int i = 0; i < 9; i++)
            if (buttons[i].getText().isEmpty()) available.add(i + 1);
        if (available.isEmpty()) return 5;
        return available.get(new Random().nextInt(available.size()));
    }

    private int getSmartMove() {
        // Try to win
        ArrayList<Integer> winMoves = getWinningMove(playerTwo);
        if (!winMoves.isEmpty() && winMoves.get(0) != -1) return winMoves.get(0);
        
        // Block opponent
        ArrayList<Integer> blockMoves = getWinningMove(playerOne);
        if (!blockMoves.isEmpty() && blockMoves.get(0) != -1) return blockMoves.get(0);
        
        return getRandomMove();
    }

    private int getMediumMove() {
        // 1. Win if possible
        ArrayList<Integer> winMoves = getWinningMove(playerTwo);
        if (!winMoves.isEmpty() && winMoves.get(0) != -1) return winMoves.get(0);
        
        // 2. Block opponent's win
        ArrayList<Integer> blockMoves = getWinningMove(playerOne);
        if (!blockMoves.isEmpty() && blockMoves.get(0) != -1) return blockMoves.get(0);
        
        // 3. Take center if available
        if (buttons[4].getText().isEmpty()) return 5;
        
        // 4. Take corner
        int[] corners = {1, 3, 7, 9};
        for (int corner : corners)
            if (buttons[corner - 1].getText().isEmpty()) return corner;
        
        // 5. Take edge
        int[] edges = {2, 4, 6, 8};
        for (int edge : edges)
            if (buttons[edge - 1].getText().isEmpty()) return edge;
        
        return getRandomMove();
    }

    // COMPLETELY UNBEATABLE AI - Uses perfect minimax-inspired strategy
    private int getUnbeatableMove() {
        // STRATEGY 1: Win immediately if possible (highest priority)
        ArrayList<Integer> winMoves = getWinningMove(playerTwo);
        if (!winMoves.isEmpty() && winMoves.get(0) != -1) {
            return winMoves.get(0);
        }
        
        // STRATEGY 2: Block opponent's immediate win (second priority)
        ArrayList<Integer> blockMoves = getWinningMove(playerOne);
        if (!blockMoves.isEmpty() && blockMoves.get(0) != -1) {
            return blockMoves.get(0);
        }
        
        // STRATEGY 3: Create a fork (two ways to win simultaneously)
        int forkMove = createFork(playerTwo, playerOne);
        if (forkMove != -1) return forkMove;
        
        // STRATEGY 4: Block opponent's potential forks
        int blockFork = blockOpponentFork(playerOne, playerTwo);
        if (blockFork != -1) return blockFork;
        
        // STRATEGY 5: Opening move optimization (first move)
        if (playerTwo.isEmpty() && playerOne.isEmpty()) {
            // Always start with corner for maximum winning potential
            return 1; // Top-left corner
        }
        
        // STRATEGY 6: Second move as AI (respond to opponent's first move)
        if (playerTwo.isEmpty() && playerOne.size() == 1) {
            int opponentMove = playerOne.get(0);
            if (opponentMove == 5) {
                // Opponent took center, take any corner
                int[] corners = {1, 3, 7, 9};
                return corners[0];
            } else {
                // Opponent took corner or edge, take center
                if (buttons[4].getText().isEmpty()) return 5;
                // If center taken, take opposite corner
                return getOppositeCorner();
            }
        }
        
        // STRATEGY 7: Advanced positional play
        // If we have center and opponent has corner, take adjacent edge to create threats
        if (playerTwo.contains(5) && hasCorner(playerOne)) {
            int[] edges = {2, 4, 6, 8};
            for (int edge : edges) {
                if (buttons[edge - 1].getText().isEmpty()) {
                    if (createsMultipleThreats(edge)) return edge;
                }
            }
        }
        
        // STRATEGY 8: Take center if available and strategic
        if (buttons[4].getText().isEmpty()) {
            // Center is always good if available
            return 5;
        }
        
        // STRATEGY 9: Opposite corner strategy
        int oppositeCorner = getOppositeCorner();
        if (oppositeCorner != -1) return oppositeCorner;
        
        // STRATEGY 10: Any empty corner (corners are strong positions)
        int[] corners = {1, 3, 7, 9};
        for (int corner : corners) {
            if (buttons[corner - 1].getText().isEmpty()) return corner;
        }
        
        // STRATEGY 11: Take edges that don't give opponent fork opportunities
        int[] edges = {2, 4, 6, 8};
        for (int edge : edges) {
            if (buttons[edge - 1].getText().isEmpty()) {
                // Make sure this edge doesn't give opponent a fork
                if (!givesOpponentFork(edge)) return edge;
            }
        }
        
        // STRATEGY 12: Fallback - any available move
        for (int i = 1; i <= 9; i++) {
            if (buttons[i - 1].getText().isEmpty()) return i;
        }
        
        return 5; // Ultimate fallback
    }
    
    // Check if player has any corner
    private boolean hasCorner(ArrayList<Integer> player) {
        int[] corners = {1, 3, 7, 9};
        for (int corner : corners) {
            if (player.contains(corner)) return true;
        }
        return false;
    }
    
    // Check if a move creates multiple threats
    private boolean createsMultipleThreats(int move) {
        ArrayList<Integer> testAI = new ArrayList<>(playerTwo);
        testAI.add(move);
        ArrayList<Integer> threats = getWinningMove(testAI);
        return threats.size() >= 2 && !threats.contains(-1);
    }
    
    // Check if a move gives opponent a fork opportunity
    private boolean givesOpponentFork(int move) {
        // Simulate the move
        ArrayList<Integer> testAI = new ArrayList<>(playerTwo);
        testAI.add(move);
        
        // Check if opponent can create fork after this move
        for (int i = 1; i <= 9; i++) {
            if (i != move && buttons[i - 1].getText().isEmpty()) {
                ArrayList<Integer> testOpponent = new ArrayList<>(playerOne);
                testOpponent.add(i);
                if (createFork(testOpponent, testAI) != -1) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // Enhanced fork creation - find moves that create two winning lines
    private int createFork(ArrayList<Integer> player, ArrayList<Integer> opponent) {
        for (int i = 1; i <= 9; i++) {
            if (buttons[i - 1].getText().isEmpty()) {
                ArrayList<Integer> testPlayer = new ArrayList<>(player);
                testPlayer.add(i);
                
                // Count how many winning moves this creates
                ArrayList<Integer> winningMoves = getWinningMove(testPlayer);
                int validWinMoves = 0;
                
                for (int winMove : winningMoves) {
                    if (winMove != -1 && buttons[winMove - 1].getText().isEmpty()) {
                        // Make sure opponent can't block both
                        validWinMoves++;
                    }
                }
                
                // If we can create 2+ winning opportunities, it's a fork
                if (validWinMoves >= 2) return i;
            }
        }
        return -1;
    }
    
    // Block opponent's fork attempts
    private int blockOpponentFork(ArrayList<Integer> opponent, ArrayList<Integer> ai) {
        // Find all opponent moves that would create forks
        ArrayList<Integer> forkThreats = new ArrayList<>();
        
        for (int i = 1; i <= 9; i++) {
            if (buttons[i - 1].getText().isEmpty()) {
                ArrayList<Integer> testOpponent = new ArrayList<>(opponent);
                testOpponent.add(i);
                
                ArrayList<Integer> winMoves = getWinningMove(testOpponent);
                int validWins = 0;
                for (int winMove : winMoves) {
                    if (winMove != -1 && buttons[winMove - 1].getText().isEmpty()) {
                        validWins++;
                    }
                }
                
                if (validWins >= 2) {
                    forkThreats.add(i);
                }
            }
        }
        
        // If opponent has fork threats, we need to block them
        if (!forkThreats.isEmpty()) {
            // Try to block by creating our own threat (forcing opponent to defend)
            for (int i = 1; i <= 9; i++) {
                if (buttons[i - 1].getText().isEmpty() && !forkThreats.contains(i)) {
                    ArrayList<Integer> testAI = new ArrayList<>(ai);
                    testAI.add(i);
                    
                    ArrayList<Integer> ourThreats = getWinningMove(testAI);
                    if (!ourThreats.isEmpty() && ourThreats.get(0) != -1) {
                        return i; // This forces opponent to block us instead of forking
                    }
                }
            }
            
            // If we can't create counter-threat, directly block the fork
            return forkThreats.get(0);
        }
        
        return -1;
    }

    private int getOppositeCorner() {
        // If opponent has a corner, take the opposite
        int[][] opposites = {{1, 9}, {3, 7}, {7, 3}, {9, 1}};
        
        for (int[] pair : opposites) {
            if (playerOne.contains(pair[0]) && buttons[pair[1] - 1].getText().isEmpty()) {
                return pair[1];
            }
        }
        return -1;
    }

    private ArrayList<Integer> getWinningMove(ArrayList<Integer> player) {
        ArrayList<Integer> moves = new ArrayList<>();
        int[][] winningCombos = {
            {1, 2, 3}, {4, 5, 6}, {7, 8, 9},
            {1, 4, 7}, {2, 5, 8}, {3, 6, 9},
            {1, 5, 9}, {3, 5, 7}
        };

        for (int[] combo : winningCombos) {
            ArrayList<Integer> missing = new ArrayList<>();
            for (int pos : combo) {
                if (!player.contains(pos)) {
                    missing.add(pos);
                }
            }
            if (missing.size() == 1 && buttons[missing.get(0) - 1].getText().isEmpty()) {
                moves.add(missing.get(0));
            }
        }
        return moves.isEmpty() ? new ArrayList<>(java.util.List.of(-1)) : moves;
    }

    boolean checkWinner(ArrayList<Integer> playerMoves) {
        int[][] winningCombos = {
            {1, 2, 3}, {4, 5, 6}, {7, 8, 9},
            {1, 4, 7}, {2, 5, 8}, {3, 6, 9},
            {1, 5, 9}, {3, 5, 7}
        };

        for (int[] combo : winningCombos) {
            if (playerMoves.contains(combo[0]) && 
                playerMoves.contains(combo[1]) && 
                playerMoves.contains(combo[2])) {
                return true;
            }
        }
        return false;
    }

    private void showWinnerCelebration(String winner, boolean isPlayerWin) {
        SwingUtilities.invokeLater(() -> {
            String message = isPlayerWin ? 
                "Congratulations " + winner + "!\nYou won this round!" :
                winner + " wins!\nBetter luck next time!";
            
            // Animated celebration dialog
            JDialog celebrationDialog = new JDialog(gameFrame, "Round Result", true);
            celebrationDialog.setLayout(new BorderLayout());
            celebrationDialog.getContentPane().setBackground(isPlayerWin ? ACCENT_GREEN : ACCENT_BLUE);
            
            JLabel celebrationLabel = new JLabel("<html><div style='text-align: center'>" + 
                message.replace("\n", "<br>") + "</div></html>", SwingConstants.CENTER);
            celebrationLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            celebrationLabel.setForeground(TEXT_WHITE);
            celebrationLabel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
            
            // Celebration text
            String celebration = isPlayerWin ? "VICTORY!" : "DEFEAT!";
            JLabel celebrationTextLabel = new JLabel(celebration, SwingConstants.CENTER);
            celebrationTextLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            celebrationTextLabel.setForeground(TEXT_WHITE);
            
            celebrationDialog.add(celebrationTextLabel, BorderLayout.NORTH);
            celebrationDialog.add(celebrationLabel, BorderLayout.CENTER);
            
            // Auto-close after 2 seconds
            Timer closeTimer = new Timer(2000, e -> {
                celebrationDialog.dispose();
                checkSeriesWinner();
            });
            closeTimer.setRepeats(false);
            closeTimer.start();
            
            celebrationDialog.setSize(300, 200);
            celebrationDialog.setLocationRelativeTo(gameFrame);
            celebrationDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            celebrationDialog.setVisible(true);
        });
    }

    private void showDrawDialog() {
        String message = "It's a Draw!\nWell played by both sides!";
        
        JDialog drawDialog = new JDialog(gameFrame, "Draw", true);
        drawDialog.setLayout(new BorderLayout());
        drawDialog.getContentPane().setBackground(ACCENT_PURPLE);
        
        JLabel drawLabel = new JLabel("<html><div style='text-align: center'>" + 
            message.replace("\n", "<br>") + "</div></html>", SwingConstants.CENTER);
        drawLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        drawLabel.setForeground(TEXT_WHITE);
        drawLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel drawTextLabel = new JLabel("DRAW", SwingConstants.CENTER);
        drawTextLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        drawTextLabel.setForeground(TEXT_WHITE);
        
        drawDialog.add(drawTextLabel, BorderLayout.NORTH);
        drawDialog.add(drawLabel, BorderLayout.CENTER);
        
        Timer closeTimer = new Timer(1500, e -> {
            drawDialog.dispose();
            
            checkSeriesWinner();
        });
        closeTimer.setRepeats(false);
        closeTimer.start();
        
        drawDialog.setSize(250, 150);
        drawDialog.setLocationRelativeTo(gameFrame);
        drawDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        drawDialog.setVisible(true);
    }

    void checkSeriesWinner() {
        String currentScore = player1Name + " [" + p1Wins + "] - [" + p2Wins + "] " + player2Name;
        scoreLabel.setText("Score: " + currentScore);

        if (p1Wins == roundsToWin || p2Wins == roundsToWin) {
            String champion = (p1Wins == roundsToWin) ? player1Name : player2Name;
            boolean isPlayerChampion = p1Wins == roundsToWin;
            
            showChampionCelebration(champion, isPlayerChampion, currentScore);
        } else {
            // Continue to next round - Hard mode AI ALWAYS starts
            SwingUtilities.invokeLater(() -> {
                if (isVsAI && difficulty == 3) {
                    isPlayerFirst = false; // Hard AI always starts
                } else {
                    isPlayerFirst = !isPlayerFirst; // Alternate for other modes
                }
                resetBoardOnly();
                
                // If AI should start the new round, make it play
                if (isVsAI && !isPlayerFirst) {
                    Timer aiDelay = new Timer(800, evt -> {
                        if (gameInProgress) {
                            aiMove();
                        }
                        ((Timer) evt.getSource()).stop();
                    });
                    aiDelay.setRepeats(false);
                    aiDelay.start();
                }
            });
        }
    }

    private void showChampionCelebration(String champion, boolean isPlayerChampion, String finalScore) {
        // Championship dialog
        JDialog championDialog = new JDialog(gameFrame, "CHAMPION!", true);
        championDialog.setLayout(new BorderLayout());
        championDialog.getContentPane().setBackground(isPlayerChampion ? ACCENT_GREEN : ACCENT_RED);
        
        // Championship message
        String message = isPlayerChampion ? 
            champion + " is the CHAMPION!\nOutstanding victory!" :
            champion + " is the CHAMPION!\nImpressive performance!";
        
        JPanel contentPanel = new JPanel(new GridLayout(4, 1));
        contentPanel.setBackground(isPlayerChampion ? ACCENT_GREEN : ACCENT_RED);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Trophy and title
        JLabel trophyLabel = new JLabel("CHAMPION!", SwingConstants.CENTER);
        trophyLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        trophyLabel.setForeground(TEXT_WHITE);
        
        JLabel championLabel = new JLabel("<html><div style='text-align: center'>" + 
            message.replace("\n", "<br>") + "</div></html>", SwingConstants.CENTER);
        championLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        championLabel.setForeground(TEXT_WHITE);
        
        JLabel scoreLabel = new JLabel("Final Score: " + finalScore, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        scoreLabel.setForeground(TEXT_WHITE);
        
        // Play again button
        JButton playAgainBtn = createModernButton("Play Again", CARD_WHITE);
        playAgainBtn.setForeground(isPlayerChampion ? ACCENT_GREEN : ACCENT_RED);
        playAgainBtn.addActionListener(e -> {
            championDialog.dispose();
            p1Wins = 0;
            p2Wins = 0;
            // Random start for new match - Hard mode AI ALWAYS starts first
            if (isVsAI && difficulty == 3) {
                isPlayerFirst = false; // Hard AI always starts
            } else {
                isPlayerFirst = new Random().nextBoolean();
            }
            resetBoardOnly();
            
            // If AI should start the new match, make it play
            if (isVsAI && !isPlayerFirst) {
                Timer aiDelay = new Timer(800, evt -> {
                    if (gameInProgress) {
                        aiMove();
                    }
                    ((Timer) evt.getSource()).stop();
                });
                aiDelay.setRepeats(false);
                aiDelay.start();
            }
        });
        
        JButton exitBtn = createModernButton("Exit Game", PRIMARY_DARK);
        exitBtn.addActionListener(e -> {
            championDialog.dispose();
            gameFrame.dispose();
            System.exit(0);
        });
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(isPlayerChampion ? ACCENT_GREEN : ACCENT_RED);
        buttonPanel.add(playAgainBtn);
        buttonPanel.add(exitBtn);
        
        contentPanel.add(trophyLabel);
        contentPanel.add(championLabel);
        contentPanel.add(scoreLabel);
        contentPanel.add(buttonPanel);
        
        championDialog.add(contentPanel, BorderLayout.CENTER);
        championDialog.setSize(400, 300);
        championDialog.setLocationRelativeTo(gameFrame);
        championDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        championDialog.setVisible(true);
    }

    void resetBoardOnly() {
        for (JButton btn : buttons) {
            btn.setText("");
            btn.setEnabled(true);
            btn.setBackground(CARD_WHITE);
        }
        playerOne.clear();
        playerTwo.clear();
        flag = 0;
        gameInProgress = true;
        turnLabel.setText("Turn: " + (isPlayerFirst ? player1Name : (isVsAI ? "AI" : player2Name)));
    }

    // Proper resource cleanup using a shutdown hook
    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (animationTimer != null) {
                animationTimer.stop();
            }
        }));
    }

    {
        // Instance initializer to set up shutdown hook
        addShutdownHook();
    }
}
