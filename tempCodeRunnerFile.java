import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class Connect6Game extends JFrame {
    private static final int BOARD_SIZE = 19;
    private static final int WIN_COUNT = 6;
    private int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
    private boolean playerTurn = true; // true = player, false = AI
    private int currentPlayer = 1; // Player = 1 (Blue)
    private int aiPlayer = 2;      // AI = 2 (Red)
    private JButton[][] buttons = new JButton[BOARD_SIZE][BOARD_SIZE];
    private JComboBox<String> algoSelector;
    private JLabel statusLabel;

    public Connect6Game() {
        setTitle("Connect6 Game");
        setSize(900, 950);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                JButton btn = new JButton();
                btn.setBackground(Color.WHITE);
                btn.setOpaque(true);
                btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                final int r = i, c = j;
                btn.addActionListener(e -> {
                    if (playerTurn && board[r][c] == 0 && !isGameOver()) {
                        makeMove(r, c, currentPlayer);
                        playerTurn = false;
                        statusLabel.setText("AI is thinking...");
                        SwingUtilities.invokeLater(() -> {
                            aiMove();
                            playerTurn = true;
                        });
                    }
                });
                buttons[i][j] = btn;
                boardPanel.add(btn);
            }
        }
        add(boardPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        String[] algorithms = {
                "Minimax",
                "Minimax + Alpha-Beta",
                "Heuristic 1 (Center)",
                "Heuristic 2 (Neighbors)",
                "Minimax + Heuristic 1",
                "Minimax + Heuristic 2",
                "Symmetry Reduction",
                "Heuristic Reduction"
        };

        algoSelector = new JComboBox<>(algorithms);
        controlPanel.add(new JLabel("Select AI Algorithm: "));
        controlPanel.add(algoSelector);

        statusLabel = new JLabel("Your turn");
        controlPanel.add(statusLabel);

        add(controlPanel, BorderLayout.SOUTH);
    }

    private void makeMove(int r, int c, int player) {
        board[r][c] = player;
        buttons[r][c].setBackground(player == 1 ? Color.BLUE : Color.RED);
        buttons[r][c].setEnabled(false);
        if (checkWin(player)) {
            statusLabel.setText((player == 1 ? "Player" : "AI") + " Wins!");
            disableAllButtons();
        } else if (getValidMoves(board).isEmpty()) {
            statusLabel.setText("Draw!");
        } else {
            statusLabel.setText((player == 1 ? "AI" : "Player") + " turn");
        }
    }

    private void disableAllButtons() {
        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++)
                buttons[i][j].setEnabled(false);
    }

    private void aiMove() {
        Point move = null;
        int selectedAlgo = algoSelector.getSelectedIndex();

        switch (selectedAlgo) {
            case 0: // Minimax
                move = minimaxDecision(board, aiPlayer, 2);
                break;
            case 1: // Minimax + Alpha-Beta
                move = minimaxAlphaBetaDecision(board, aiPlayer, 2, Integer.MIN_VALUE, Integer.MAX_VALUE);
                break;
            case 2: // Heuristic 1
                move = heuristic1Decision(board);
                break;
            case 3: // Heuristic 2
                move = heuristic2Decision(board);
                break;
            case 4: // Minimax + Heuristic 1
                move = minimaxDecisionWithHeuristic(board, aiPlayer, 2, 1);
                break;
            case 5: // Minimax + Heuristic 2
                move = minimaxDecisionWithHeuristic(board, aiPlayer, 2, 2);
                break;
            case 6: // Symmetry Reduction
                move = symmetryReductionDecision(board);
                break;
            case 7: // Heuristic Reduction
                move = heuristicReductionDecision(board);
                break;
            default:
                move = minimaxDecision(board, aiPlayer, 2);
        }

        if (move != null) {
            makeMove(move.x, move.y, aiPlayer);
        }
    }

    private boolean checkWin(int player) {
        // تحقق إذا كان player وصل 6 أو أكثر على التوالي في أي اتجاه
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (board[r][c] == player) {
                    if (checkDirection(r, c, 1, 0, player) ||
                            checkDirection(r, c, 0, 1, player) ||
                            checkDirection(r, c, 1, 1, player) ||
                            checkDirection(r, c, 1, -1, player)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkDirection(int r, int c, int dr, int dc, int player) {
        int count = 0;
        for (int i = 0; i < WIN_COUNT; i++) {
            int nr = r + dr * i;
            int nc = c + dc * i;
            if (nr < 0 || nc < 0 || nr >= BOARD_SIZE || nc >= BOARD_SIZE) return false;
            if (board[nr][nc] == player) count++;
            else break;
        }
        return count == WIN_COUNT;
    }

    private List<Point> getValidMoves(int[][] state) {
        List<Point> moves = new ArrayList<>();
        for (int r = 0; r < BOARD_SIZE; r++)
            for (int c = 0; c < BOARD_SIZE; c++)
                if (state[r][c] == 0)
                    moves.add(new Point(r, c));
        return moves;
    }

    private Point minimaxDecision(int[][] state, int player, int depth) {
        // Minimax باعمق محدد، بدون تحسينات
        int bestValue = Integer.MIN_VALUE;
        Point bestMove = null;
        for (Point move : getValidMoves(state)) {
            int[][] newState = copyBoard(state);
            newState[move.x][move.y] = player;
            int value = minimaxValue(newState, depth - 1, false, player);
            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
        }
        return bestMove;
    }

    private int minimaxValue(int[][] state, int depth, boolean maximizingPlayer, int player) {
        if (depth == 0 || checkWin(aiPlayer) || checkWin(currentPlayer)) {
            return evaluateBoard(state, player);
        }
        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Point move : getValidMoves(state)) {
                int[][] newState = copyBoard(state);
                newState[move.x][move.y] = player;
                int eval = minimaxValue(newState, depth - 1, false, player);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            int opponent = (player == 1) ? 2 : 1;
            for (Point move : getValidMoves(state)) {
                int[][] newState = copyBoard(state);
                newState[move.x][move.y] = opponent;
                int eval = minimaxValue(newState, depth - 1, true, player);
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }

    private Point minimaxAlphaBetaDecision(int[][] state, int player, int depth, int alpha, int beta) {
        int bestValue = Integer.MIN_VALUE;
        Point bestMove = null;
        for (Point move : getValidMoves(state)) {
            int[][] newState = copyBoard(state);
            newState[move.x][move.y] = player;
            int value = minimaxAlphaBetaValue(newState, depth - 1, alpha, beta, false, player);
            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
            alpha = Math.max(alpha, bestValue);
            if (beta <= alpha)
                break;
        }
        return bestMove;
    }

    private int minimaxAlphaBetaValue(int[][] state, int depth, int alpha, int beta, boolean maximizingPlayer, int player) {
        if (depth == 0 || checkWin(aiPlayer) || checkWin(currentPlayer)) {
            return evaluateBoard(state, player);
        }
        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Point move : getValidMoves(state)) {
                int[][] newState = copyBoard(state);
                newState[move.x][move.y] = player;
                int eval = minimaxAlphaBetaValue(newState, depth - 1, alpha, beta, false, player);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha)
                    break;
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            int opponent = (player == 1) ? 2 : 1;
            for (Point move : getValidMoves(state)) {
                int[][] newState = copyBoard(state);
                newState[move.x][move.y] = opponent;
                int eval = minimaxAlphaBetaValue(newState, depth - 1, alpha, beta, true, player);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha)
                    break;
            }
            return minEval;
        }
    }

    private Point heuristic1Decision(int[][] state) {
        // أقرب نقطة لمركز اللوحة
        Point center = new Point(BOARD_SIZE / 2, BOARD_SIZE / 2);
        Point bestMove = null;
        double bestDist = Double.MAX_VALUE;
        for (Point move : getValidMoves(state)) {
            double dist = center.distance(move);
            if (dist < bestDist) {
                bestDist = dist;
                bestMove = move;
            }
        }
        return bestMove;
    }

    private Point heuristic2Decision(int[][] state) {
        // الحركة التي تحيط بها أكبر عدد من حجرت AI
        int maxNeighbors = -1;
        Point bestMove = null;
        for (Point move : getValidMoves(state)) {
            int count = countNeighbors(state, move.x, move.y, aiPlayer);
            if (count > maxNeighbors) {
                maxNeighbors = count;
                bestMove = move;
            }
        }
        return bestMove;
    }

    private int countNeighbors(int[][] state, int r, int c, int player) {
        int count = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int nr = r + dr;
                int nc = c + dc;
                if (nr >= 0 && nc >= 0 && nr < BOARD_SIZE && nc < BOARD_SIZE) {
                    if (state[nr][nc] == player) count++;
                }
            }
        }
        return count;
    }

    private Point minimaxDecisionWithHeuristic(int[][] state, int player, int depth, int heuristicNum) {
        int bestValue = Integer.MIN_VALUE;
        Point bestMove = null;
        for (Point move : getValidMoves(state)) {
            int[][] newState = copyBoard(state);
            newState[move.x][move.y] = player;
            int value = minimaxValueWithHeuristic(newState, depth - 1, false, player, heuristicNum);
            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
        }
        return bestMove;
    }

    private int minimaxValueWithHeuristic(int[][] state, int depth, boolean maximizingPlayer, int player, int heuristicNum) {
        if (depth == 0 || checkWin(aiPlayer) || checkWin(currentPlayer)) {
            return evaluateBoardWithHeuristic(state, player, heuristicNum);
        }
        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Point move : getValidMoves(state)) {
                int[][] newState = copyBoard(state);
                newState[move.x][move.y] = player;
                int eval = minimaxValueWithHeuristic(newState, depth - 1, false, player, heuristicNum);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            int opponent = (player == 1) ? 2 : 1;
            for (Point move : getValidMoves(state)) {
                int[][] newState = copyBoard(state);
                newState[move.x][move.y] = opponent;
                int eval = minimaxValueWithHeuristic(newState, depth - 1, true, player, heuristicNum);
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }

    private int evaluateBoardWithHeuristic(int[][] state, int player, int heuristicNum) {
        if (heuristicNum == 1) {
            // قيمة أكبر للقطع القريبة من المركز
            int center = BOARD_SIZE / 2;
            int score = 0;
            for (int r = 0; r < BOARD_SIZE; r++) {
                for (int c = 0; c < BOARD_SIZE; c++) {
                    if (state[r][c] == player) {
                        int dist = Math.abs(r - center) + Math.abs(c - center);
                        score -= dist; // كل ما الاقرب المركز اعلى القيمة
                    } else if (state[r][c] != 0) {
                        score += dist; // للخصم
                    }
                }
            }
            return score;
        } else {
            // Heuristic 2: عدد الجيران
            int score = 0;
            for (int r = 0; r < BOARD_SIZE; r++) {
                for (int c = 0; c < BOARD_SIZE; c++) {
                    if (state[r][c] == player) {
                        score += countNeighbors(state, r, c, player);
                    } else if (state[r][c] != 0) {
                        score -= countNeighbors(state, r, c, (player == 1 ? 2 : 1));
                    }
                }
            }
            return score;
        }
    }

    private Point symmetryReductionDecision(int[][] state) {
        // تبسيط عن طريق تجاهل الحركات المتشابهة (تقريباً يختار الحركة الأقرب للمركز مع تفريق الحركات متماثلة)
        Point center = new Point(BOARD_SIZE / 2, BOARD_SIZE / 2);
        Set<String> seenSymmetries = new HashSet<>();
        Point bestMove = null;
        double bestDist = Double.MAX_VALUE;

        for (Point move : getValidMoves(state)) {
            String symKey = symmetryKey(move);
            if (seenSymmetries.contains(symKey)) continue;
            seenSymmetries.add(symKey);
            double dist = center.distance(move);
            if (dist < bestDist) {
                bestDist = dist;
                bestMove = move;
            }
        }
        return bestMove;
    }

    private String symmetryKey(Point p) {
        // توليد مفتاح مميز لمجموعة المتماثلات للحركة
        int r = p.x;
        int c = p.y;
        int center = BOARD_SIZE / 2;
        int dr = r - center;
        int dc = c - center;
        // نأخذ الحد الأعلى بعد تطبيق انعكاسات 8 احتمالات (تماثلات المربع)
        int minR = Math.min(Math.min(Math.min(dr, -dr), dc), -dc);
        int minC = Math.min(Math.min(Math.min(dc, -dc), dr), -dr);
        return minR + "," + minC;
    }

    private Point heuristicReductionDecision(int[][] state) {
        // نختار الحركة ذات التقييم الأفضل حسب heuristics 1 و 2 معاً
        Point bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        for (Point move : getValidMoves(state)) {
            int[][] newState = copyBoard(state);
            newState[move.x][move.y] = aiPlayer;
            int score1 = evaluateBoardWithHeuristic(newState, aiPlayer, 1);
            int score2 = evaluateBoardWithHeuristic(newState, aiPlayer, 2);
            int combined = score1 + score2;
            if (combined > bestScore) {
                bestScore = combined;
                bestMove = move;
            }
        }
        return bestMove;
    }

    private int[][] copyBoard(int[][] state) {
        int[][] newBoard = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++)
            System.arraycopy(state[i], 0, newBoard[i], 0, BOARD_SIZE);
        return newBoard;
    }

    private boolean isGameOver() {
        return checkWin(currentPlayer) || checkWin(aiPlayer) || getValidMoves(board).isEmpty();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Connect6Game game = new Connect6Game();
            game.setVisible(true);
        });
    }
}
