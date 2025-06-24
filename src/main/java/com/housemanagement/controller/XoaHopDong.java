package com.housemanagement.controller;

import com.housemanagement.controller.QLyHopDong;
import com.housemanagement.model.Contract;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;

public class XoaHopDong extends JDialog {
    private QLyHopDong qlyHopDongController;
    private Contract contract;
    private boolean isDeleted = false;

    // UI Components
    private JLabel lblContractInfo;
    private JTextArea txtContractDetails;
    private JCheckBox chkConfirmDelete;
    private JButton btnXoa, btnHuy;
    private JLabel lblWarning;

    public XoaHopDong(Frame parent, int contractId) {
        super(parent, "Xóa Hợp Đồng", true);
        this.qlyHopDongController = new QLyHopDong();

        try {
            this.contract = qlyHopDongController.getContractById(contractId);
            if (this.contract == null) {
                JOptionPane.showMessageDialog(parent, "Không tìm thấy hợp đồng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Lỗi khi tải thông tin hợp đồng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        initializeUI();
        loadContractData();
        setupEventHandlers();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(244, 67, 54));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Icon and Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(new Color(244, 67, 54));

        JLabel iconLabel = new JLabel("⚠");
        iconLabel.setFont(new Font("Arial", Font.BOLD, 24));
        iconLabel.setForeground(Color.WHITE);

        JLabel titleLabel = new JLabel("XÁC NHẬN XÓA HỢP ĐỒNG");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        titlePanel.add(iconLabel);
        titlePanel.add(Box.createHorizontalStrut(10));
        titlePanel.add(titleLabel);

        panel.add(titlePanel, BorderLayout.WEST);

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Contract info label
        lblContractInfo = new JLabel("Thông tin hợp đồng sẽ bị xóa:");
        lblContractInfo.setFont(new Font("Arial", Font.BOLD, 14));
        lblContractInfo.setForeground(new Color(51, 51, 51));

        // Contract details
        txtContractDetails = new JTextArea();
        txtContractDetails.setEditable(false);
        txtContractDetails.setBackground(new Color(245, 245, 245));
        txtContractDetails.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        txtContractDetails.setFont(new Font("Arial", Font.PLAIN, 12));
        txtContractDetails.setRows(8);

        JScrollPane scrollPane = new JScrollPane(txtContractDetails);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Warning label
        lblWarning = new JLabel("<html><div style='text-align: center;'>" +
                "<b>CẢNH BÁO:</b><br/>" +
                "• Hành động này không thể hoàn tác<br/>" +
                "• Tất cả dữ liệu liên quan sẽ bị mất<br/>" +
                "• Trạng thái phòng sẽ được cập nhật tự động" +
                "</div></html>");
        lblWarning.setFont(new Font("Arial", Font.PLAIN, 12));
        lblWarning.setForeground(new Color(244, 67, 54));
        lblWarning.setHorizontalAlignment(SwingConstants.CENTER);
        lblWarning.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(244, 67, 54)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Confirmation checkbox
        chkConfirmDelete = new JCheckBox("Tôi hiểu và xác nhận muốn xóa hợp đồng này");
        chkConfirmDelete.setFont(new Font("Arial", Font.BOLD, 12));
        chkConfirmDelete.setBackground(Color.WHITE);
        chkConfirmDelete.setForeground(new Color(244, 67, 54));

        // Layout
        panel.add(lblContractInfo, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(Box.createVerticalStrut(15), BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(lblWarning, BorderLayout.NORTH);
        bottomPanel.add(Box.createVerticalStrut(15), BorderLayout.CENTER);
        bottomPanel.add(chkConfirmDelete, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        btnXoa = new JButton("Xóa Hợp Đồng");
        btnXoa.setBackground(new Color(244, 67, 54));
        btnXoa.setForeground(Color.WHITE);
        btnXoa.setFont(new Font("Arial", Font.BOLD, 14));
        btnXoa.setPreferredSize(new Dimension(140, 40));
        btnXoa.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnXoa.setFocusPainted(false);
        btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXoa.setEnabled(false); // Disabled until checkbox is checked

        btnHuy = new JButton("Hủy");
        btnHuy.setBackground(new Color(158, 158, 158));
        btnHuy.setForeground(Color.WHITE);
        btnHuy.setFont(new Font("Arial", Font.BOLD, 14));
        btnHuy.setPreferredSize(new Dimension(100, 40));
        btnHuy.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnHuy.setFocusPainted(false);
        btnHuy.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panel.add(btnHuy);
        panel.add(btnXoa);

        return panel;
    }

    private void loadContractData() {
        if (contract == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        StringBuilder details = new StringBuilder();

        details.append("ID Hợp Đồng: ").append(contract.getContractId()).append("\n");
        details.append("Khách Hàng: ").append(contract.getCustomerName() != null ? contract.getCustomerName() : "N/A").append("\n");
        details.append("Phòng: ").append(contract.getRoomName() != null ? contract.getRoomName() : "N/A").append("\n");
        details.append("Ngày Bắt Đầu: ").append(contract.getStartDate() != null ? sdf.format(contract.getStartDate()) : "N/A").append("\n");
        details.append("Ngày Kết Thúc: ").append(contract.getEndDate() != null ? sdf.format(contract.getEndDate()) : "Chưa xác định").append("\n");

        if (contract.getRoomRent() > 0) {
            details.append("Giá Thuê: ").append(String.format("%,.0f VNĐ/tháng", contract.getRoomRent())).append("\n");
        }

        if (contract.getDeposit() > 0) {
            details.append("Tiền Cọc: ").append(String.format("%,.0f VNĐ", contract.getDeposit())).append("\n");
        }

        details.append("Trạng Thái: ").append(getStatusInVietnamese(contract.getStatus())).append("\n");

        if (contract.getNote() != null && !contract.getNote().trim().isEmpty()) {
            details.append("Ghi Chú: ").append(contract.getNote()).append("\n");
        }

        if (contract.getCreated() != null) {
            details.append("Ngày Tạo: ").append(sdf.format(contract.getCreated()));
        }

        txtContractDetails.setText(details.toString());
        txtContractDetails.setCaretPosition(0); // Scroll to top
    }

    private void setupEventHandlers() {
        // Checkbox change handler
        chkConfirmDelete.addActionListener(e -> {
            btnXoa.setEnabled(chkConfirmDelete.isSelected());
        });

        // Delete button handler
        btnXoa.addActionListener(e -> deleteContract());

        // Cancel button handler
        btnHuy.addActionListener(e -> dispose());

        // ESC key handler
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void deleteContract() {
        if (contract == null) {
            showError("Không có thông tin hợp đồng để xóa!");
            return;
        }

        // Final confirmation
        int finalConfirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có THỰC SỰ chắc chắn muốn xóa hợp đồng này?\n" +
                        "Hành động này không thể hoàn tác!",
                "Xác Nhận Cuối Cùng",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (finalConfirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Show progress
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        btnXoa.setEnabled(false);
        btnHuy.setEnabled(false);
        btnXoa.setText("Đang xóa...");

        // Perform deletion in background thread
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                qlyHopDongController.deleteContract(contract.getContractId());
                return true;
            }

            @Override
            protected void done() {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

                try {
                    get(); // Check for exceptions
                    isDeleted = true;
                    showInfo("Xóa hợp đồng thành công!\n" +
                            "Phòng " + contract.getRoomName() + " đã được cập nhật trạng thái.");
                    dispose();
                } catch (Exception e) {
                    btnXoa.setEnabled(true);
                    btnHuy.setEnabled(true);
                    btnXoa.setText("Xóa Hợp Đồng");
                    showError("Lỗi khi xóa hợp đồng: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }

    private String getStatusInVietnamese(String status) {
        if (status == null) return "N/A";

        switch (status.toLowerCase()) {
            case "active":
                return "Đang hoạt động";
            case "expired":
                return "Đã hết hạn";
            case "terminated":
                return "Đã kết thúc";
            default:
                return status;
        }
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    // Public method to check if deletion was successful
    public boolean isDeleted() {
        return isDeleted;
    }

    // Static method to show delete dialog
    public static boolean showDeleteDialog(Frame parent, int contractId) {
        XoaHopDong dialog = new XoaHopDong(parent, contractId);
        dialog.setVisible(true);
        return dialog.isDeleted();
    }
}