package com.housemanagement.controller;

import com.housemanagement.model.Room;
import com.housemanagement.ui.HouseUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class XoaPhong extends JDialog {
    private Room roomToDelete;
    private QLyPhong qLyPhongController;
    private JLabel lblRoomInfo;
    private JButton btnConfirmDelete;
    private JButton btnCancel;
    private boolean deleteConfirmed = false;

    public XoaPhong(Frame parent, Room room, QLyPhong controller) {
        super(parent, "Xác nhận xóa phòng", true);
        this.roomToDelete = room;
        this.qLyPhongController = controller;

        initComponents();
        setupDialog();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        getRootPane().setBorder(new EmptyBorder(20, 20, 20, 20));
        getContentPane().setBackground(Color.WHITE);

        // Header panel với icon cảnh báo
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        headerPanel.setBackground(Color.WHITE);

        JLabel iconLabel = new JLabel("⚠️");
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 32));
        headerPanel.add(iconLabel);

        JLabel titleLabel = new JLabel("Xác nhận xóa phòng");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(HouseUI.DANGER_COLOR);
        headerPanel.add(titleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Content panel với thông tin phòng
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel warningLabel = new JLabel("Bạn có chắc chắn muốn xóa phòng này không?");
        warningLabel.setFont(HouseUI.DEFAULT_FONT);
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(warningLabel);

        contentPanel.add(Box.createVerticalStrut(15));

        // Thông tin chi tiết phòng
        JPanel roomInfoPanel = new JPanel();
        roomInfoPanel.setLayout(new BoxLayout(roomInfoPanel, BoxLayout.Y_AXIS));
        roomInfoPanel.setBackground(new Color(248, 249, 250));
        roomInfoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        addInfoRow(roomInfoPanel, "Tên phòng:", roomToDelete.getRoomName());
        addInfoRow(roomInfoPanel, "Trạng thái:", roomToDelete.getStatus());
        addInfoRow(roomInfoPanel, "Đơn giá:", formatCurrency(roomToDelete.getRent()));

        if (roomToDelete.getMax() > 0) {
            addInfoRow(roomInfoPanel, "Số người tối đa:", String.valueOf(roomToDelete.getMax()));
        }

        contentPanel.add(roomInfoPanel);
        contentPanel.add(Box.createVerticalStrut(15));

        JLabel noteLabel = new JLabel("<html><center><i>Lưu ý: Hành động này không thể hoàn tác!</i></center></html>");
        noteLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        noteLabel.setForeground(Color.GRAY);
        noteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(noteLabel);

        add(contentPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addInfoRow(JPanel parent, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setBackground(parent.getBackground());
        rowPanel.setBorder(new EmptyBorder(3, 0, 3, 0));

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Arial", Font.BOLD, 13));
        lblLabel.setPreferredSize(new Dimension(120, lblLabel.getPreferredSize().height));
        rowPanel.add(lblLabel, BorderLayout.WEST);

        JLabel lblValue = new JLabel(value != null ? value : "N/A");
        lblValue.setFont(HouseUI.DEFAULT_FONT);
        rowPanel.add(lblValue, BorderLayout.CENTER);

        parent.add(rowPanel);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        btnCancel = createStyledButton("Hủy bỏ", HouseUI.LIGHT_GRAY_COLOR, Color.DARK_GRAY);
        btnCancel.addActionListener(e -> {
            deleteConfirmed = false;
            dispose();
        });

        btnConfirmDelete = createStyledButton("Xóa phòng", HouseUI.DANGER_COLOR, Color.WHITE);
        btnConfirmDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performDelete();
            }
        });

        // Đặt focus vào nút Hủy để tránh xóa nhầm
        SwingUtilities.invokeLater(() -> btnCancel.requestFocusInWindow());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnConfirmDelete);

        return buttonPanel;
    }

    private void performDelete() {
        // Hiển thị progress indicator
        btnConfirmDelete.setText("Đang xóa...");
        btnConfirmDelete.setEnabled(false);
        btnCancel.setEnabled(false);

        // Thực hiện xóa trong background thread để không block UI
        SwingWorker<Boolean, Void> deleteWorker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    return qLyPhongController.deleteRoomById(roomToDelete.getRoomId(), roomToDelete.getRoomName());
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        deleteConfirmed = true;
                        JOptionPane.showMessageDialog(XoaPhong.this,
                                "Xóa phòng '" + roomToDelete.getRoomName() + "' thành công!",
                                "Thành công",
                                JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        // Reset button states
                        btnConfirmDelete.setText("Xóa phòng");
                        btnConfirmDelete.setEnabled(true);
                        btnCancel.setEnabled(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(XoaPhong.this,
                            "Có lỗi xảy ra khi xóa phòng: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);

                    // Reset button states
                    btnConfirmDelete.setText("Xóa phòng");
                    btnConfirmDelete.setEnabled(true);
                    btnCancel.setEnabled(true);
                }
            }
        };

        deleteWorker.execute();
    }

    private JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(HouseUI.DEFAULT_FONT);
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setMargin(new Insets(8, 16, 8, 16));
        button.setPreferredSize(new Dimension(120, 35));

        // Thêm hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalBg = button.getBackground();

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(bgColor.darker());
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(originalBg);
                }
            }
        });

        return button;
    }

    private void setupDialog() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(getParent());

        // Đặt kích thước tối thiểu
        Dimension prefSize = getPreferredSize();
        setMinimumSize(new Dimension(Math.max(400, prefSize.width), prefSize.height));
    }

    private String formatCurrency(double amount) {
        return String.format("%,.0f VNĐ", amount);
    }

    public boolean isDeleteConfirmed() {
        return deleteConfirmed;
    }

    // Static method để sử dụng dialog một cách tiện lợi
    public static boolean showDeleteConfirmDialog(Frame parent, Room room, QLyPhong controller) {
        XoaPhong dialog = new XoaPhong(parent, room, controller);
        dialog.setVisible(true);
        return dialog.isDeleteConfirmed();
    }
}