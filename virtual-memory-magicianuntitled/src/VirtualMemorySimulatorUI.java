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

    public VirtualMemorySimulatorUI() {
        setTitle("Virtual Memory Simulator");
        setSize(650, 500);
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

        // Initialize MemoryManager
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
            dispose();
            new VirtualMemorySimulatorUI().setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VirtualMemorySimulatorUI().setVisible(true));
    }
}