package Source;

public class Logic {
    private int result;
    private String binaryResult;
    private java.beans.PropertyChangeSupport changes;
    private static final int MAX_HISTORY_ENTRIES = 10;
    private String[] recentCalculations = new String[MAX_HISTORY_ENTRIES];
    private int historyIndex = 0;

    public Logic() {
        changes = new java.beans.PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        changes.addPropertyChangeListener(l);
    }

    private void updateResult(int newResult) {
        int oldResult = this.result;
        this.result = newResult;
        String rawBinary = Integer.toBinaryString(newResult);
        if (rawBinary.length() < 8) {
            rawBinary = String.format("%8s", rawBinary).replace(' ', '0');
        }
        this.binaryResult = rawBinary;
        changes.firePropertyChange("result", oldResult, newResult);
        changes.firePropertyChange("binaryResult", null, binaryResult);
    }

    public int add(int a, int b) {
        long result = (long) a + b;
        if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE) {
            throw new ArithmeticException("Addition overflow");
        }
        updateResult((int) result);
        return (int) result;
    }

    public int subtract(int a, int b) {
        long result = (long) a - b;
        if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE) {
            throw new ArithmeticException("Subtraction overflow");
        }
        updateResult((int) result);
        return (int) result;
    }

    public int multiply(int a, int b) {
        long result = (long) a * b;
        if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE) {
            throw new ArithmeticException("Multiplication overflow");
        }
        updateResult((int) result);
        return (int) result;
    }

    public int divide(int a, int b) {
        throw new UnsupportedOperationException("Division operation removed");
    }

    public int modulo(int a, int b) {
        throw new UnsupportedOperationException("Modulo operation removed");
    }

    public int and(int a, int b) {
        int result = a & b;
        updateResult(result);
        return result;
    }

    public int or(int a, int b) {
        int result = a | b;
        updateResult(result);
        return result;
    }

    public int not(int a) {
        int result = ~a;
        updateResult(result);
        return result;
    }

    public int leftShift(int a, int b) {
        throw new UnsupportedOperationException("Left shift operation removed");
    }

    public int rightShift(int a, int b) {
        throw new UnsupportedOperationException("Right shift operation removed");
    }

    public String getBinaryResult() {
        return binaryResult;
    }

    public boolean isValidInput(String input, String base) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            if ("BINARY".equals(base.toUpperCase())) {
                for (char c : input.toCharArray()) {
                    if (c != '0' && c != '1') {
                        return false;
                    }
                }
                if (input.length() <= 32) {
                    Integer.parseInt(input, 2);
                } else {
                    return false;
                }
            } else {
                Integer.parseInt(input);
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void addToHistory(String operation, int a, int b, int result, String base) {
        String entry;
        if (operation.equals("NOT")) {
            entry = formatHistoryEntry(operation, a, 0, result, base, true);
        } else {
            entry = formatHistoryEntry(operation, a, b, result, base, false);
        }
        recentCalculations[historyIndex] = entry;
        historyIndex = (historyIndex + 1) % MAX_HISTORY_ENTRIES;
        changes.firePropertyChange("historyUpdate", null, getHistory());
    }

    public void clearHistory() {
        for (int i = 0; i < recentCalculations.length; i++) {
            recentCalculations[i] = null;
        }
        historyIndex = 0;
        changes.firePropertyChange("historyUpdate", null, getHistory());
    }

    public void removeHistoryEntry(int index) {
        if (index < 0 || index >= recentCalculations.length || recentCalculations[index] == null) return;
        for (int i = index; i < recentCalculations.length - 1; i++) {
            recentCalculations[i] = recentCalculations[i + 1];
        }
        recentCalculations[recentCalculations.length - 1] = null;
        int count = 0;
        for (String entry : recentCalculations) {
            if (entry != null) count++;
        }
        historyIndex = count % MAX_HISTORY_ENTRIES;
        changes.firePropertyChange("historyUpdate", null, getHistory());
    }

    private String formatNumber(int number, String base) {
        if ("BINARY".equals(base.toUpperCase())) {
            return Integer.toBinaryString(number);
        }
        return Integer.toString(number);
    }

    private String formatHistoryEntry(String operation, int a, int b, int result, String base, boolean isUnary) {
        String numAStr = formatNumber(a, base);
        String resultStr = formatNumber(result, base);

        if (isUnary) {
            return String.format("%s %s = %s",
                numAStr,
                operation,
                resultStr);
        } else {
            String numBStr = formatNumber(b, base);
            return String.format("%s %s %s = %s",
                numAStr,
                operation,
                numBStr,
                resultStr);
        }
    }

    public String[] getHistory() {
        return recentCalculations;
    }
}