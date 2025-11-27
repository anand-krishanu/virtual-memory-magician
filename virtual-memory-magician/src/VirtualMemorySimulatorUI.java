import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VirtualMemorySimulatorUI extends JFrame {

    private JTextArea logArea;
    private JTable frameTable;
    private JTextField pageInput;
    private MemoryManager memoryManager;
    private int[] accessSequence = {1,2,3,2,4,1,5,2,1};
    private JRadioButton fifoRadio;
    private JRadioButton lruRadio;

    public VirtualMemorySimulatorUI() {
        setTitle("Virtual Memory Simulator");
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(new TitledBorder("Logs"));
        add(scrollPane, BorderLayout.CENTER);

        // Frame table using DefaultTableModel
        String[] columns = {"Frame", "Page"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        for (int i = 0; i < 3; i++) {
            tableModel.addRow(new Object[]{i, "FREE"});
        }
        frameTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(frameTable);
        tableScroll.setBorder(new TitledBorder("Frame Table"));
        add(tableScroll, BorderLayout.EAST);

        // Top panel for algorithm selection
        JPanel topPanel = new JPanel();
        topPanel.setBorder(new TitledBorder("Page Replacement Algorithm"));
        fifoRadio = new JRadioButton("FIFO (First-In-First-Out)", true);
        lruRadio = new JRadioButton("LRU (Least Recently Used)");
        ButtonGroup algorithmGroup = new ButtonGroup();
        algorithmGroup.add(fifoRadio);
        algorithmGroup.add(lruRadio);
        topPanel.add(fifoRadio);
        topPanel.add(lruRadio);
        add(topPanel, BorderLayout.NORTH);

        // Controls
        JPanel controlPanel = new JPanel();
        JButton nextBtn = new JButton("Next Access");
        JButton runBtn = new JButton("Run All");
        JButton resetBtn = new JButton("Reset");
        pageInput = new JTextField(3);
        pageInput.setToolTipText("Enter page number");

        controlPanel.add(new JLabel("Page Input:"));
        controlPanel.add(pageInput);
        controlPanel.add(nextBtn);
        controlPanel.add(runBtn);
        controlPanel.add(resetBtn);
        add(controlPanel, BorderLayout.SOUTH);

        // Initialize MemoryManager with FIFO as default
        memoryManager = new MemoryManager(10, 3, new FIFOReplacement(), logArea, frameTable);

        final int[] accessIndex = {0};

        // Next button
        nextBtn.addActionListener(e -> {
            int page;
            if (!pageInput.getText().isEmpty()) {
                try {
                    page = Integer.parseInt(pageInput.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Enter a valid integer");
                    return;
                }
            } else {
                if (accessIndex[0] >= accessSequence.length) {
                    JOptionPane.showMessageDialog(this, "Sequence finished!");
                    return;
                }
                page = accessSequence[accessIndex[0]++];
            }
            memoryManager.accessPage(page);
        });

        // Run all button
        runBtn.addActionListener(e -> {
            for (int page : accessSequence) {
                memoryManager.accessPage(page);
            }
        });

        // Reset button
        resetBtn.addActionListener(e -> {
            logArea.setText("");
            accessIndex[0] = 0;
            pageInput.setText("");
            
            // Recreate memory manager with selected algorithm
            ReplacementPolicy policy = fifoRadio.isSelected() ? 
                new FIFOReplacement() : new LRUReplacement();
            memoryManager = new MemoryManager(10, 3, policy, logArea, frameTable);
            
            logArea.append("System reset with " + 
                (fifoRadio.isSelected() ? "FIFO" : "LRU") + " algorithm\n");
        });
        
        // Add listeners to radio buttons to require reset
        fifoRadio.addActionListener(e -> {
            if (memoryManager.hasActivity()) {
                JOptionPane.showMessageDialog(this, 
                    "Click Reset to apply the new algorithm", 
                    "Algorithm Changed", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        lruRadio.addActionListener(e -> {
            if (memoryManager.hasActivity()) {
                JOptionPane.showMessageDialog(this, 
                    "Click Reset to apply the new algorithm", 
                    "Algorithm Changed", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // Initial message
        logArea.append("Virtual Memory Simulator Ready\n");
        logArea.append("Algorithm: FIFO (First-In-First-Out)\n");
        logArea.append("Pages: 10 | Frames: 3\n");
        logArea.append("Default sequence: [1,2,3,2,4,1,5,2,1]\n\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VirtualMemorySimulatorUI().setVisible(true));
    }
}