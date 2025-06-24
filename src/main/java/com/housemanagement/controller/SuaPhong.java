package com.housemanagement.controller;

import com.housemanagement.model.Room;
import com.housemanagement.ui.HouseUI;
import com.housemanagement.ui.sidebar.phong; // Assuming 'phong' is the correct class name

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class SuaPhong extends JDialog {
    private Room roomToEdit;
    private QLyPhong qLyPhongController;
    private phong phongPanel; // Consider renaming 'phong' to 'PhongPanel' for Java naming conventions
    private boolean updateConfirmed = false;

    private JTextField txtRoomName;
    private JTextField txtRent;
    private JTextField txtMax;
    private JComboBox<String> cbStatus;

    private final DecimalFormat currencyFormatter = new DecimalFormat("#,###");

    public SuaPhong(Frame parent, Room room, QLyPhong controller, phong phongPanel) {
        super(parent, "Sửa thông tin phòng", true);
        this.roomToEdit = room;
        this.qLyPhongController = controller;
        this.phongPanel = phongPanel;

        initComponents();
        loadRoomData();
        setupDialog();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Panel tiêu đề
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Sửa thông tin phòng: " + roomToEdit.getRoomName());
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(33, 37, 41));
        headerPanel.add(lblTitle, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // Panel form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        // Tên phòng
        JPanel pnlRoomName = createFormRow("Tên phòng:", true);
        txtRoomName = new JTextField(20);
        styleTextField(txtRoomName);
        pnlRoomName.add(txtRoomName);
        formPanel.add(pnlRoomName);
        formPanel.add(Box.createVerticalStrut(10));

        // Tiền thuê
        JPanel pnlRent = createFormRow("Tiền thuê (VNĐ):", true);
        txtRent = new JTextField(20);
        styleTextField(txtRent);
        pnlRent.add(txtRent);
        formPanel.add(pnlRent);
        formPanel.add(Box.createVerticalStrut(10));

        JPanel pnlMax = createFormRow("Số người tối đa:", false); // Max occupants can be optional or 0
        txtMax = new JTextField(5);
        styleTextField(txtMax);
        pnlMax.add(txtMax);
        formPanel.add(pnlMax);
        formPanel.add(Box.createVerticalStrut(10));

        // Trạng thái phòng
        JPanel pnlStatus = createFormRow("Trạng thái phòng:", true);
        cbStatus = new JComboBox<>(new String[]{"Còn trống", "Đã cho thuê"});
        cbStatus.setFont(HouseUI.DEFAULT_FONT);
        cbStatus.setBackground(Color.WHITE);
        cbStatus.setPreferredSize(new Dimension(200, 30)); // Standard size for combo box
        pnlStatus.add(cbStatus);
        formPanel.add(pnlStatus);
        formPanel.add(Box.createVerticalStrut(10));

        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove scroll pane border if not needed
        add(formScrollPane, BorderLayout.CENTER);

        // Panel nút
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createFormRow(String labelText, boolean required) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        panel.setBackground(Color.WHITE); // Ensure consistent background

        JLabel label = new JLabel(labelText + (required ? " *" : ""));
        label.setFont(HouseUI.DEFAULT_FONT);
        if (required) {
            // Make the asterisk red to indicate requirement, not the whole label
            // For better visual, you might need to use HTML in JLabel or split label and asterisk
            label.setText("<html>" + labelText + (required ? " <font color='red'>*</font>" : "") + "</html>");
        }
        label.setPreferredSize(new Dimension(180, 25)); // Consistent label width

        panel.add(label);
        return panel;
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(HouseUI.DEFAULT_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218), 1), // Softer border color
                BorderFactory.createEmptyBorder(5, 10, 5, 10) // Padding inside text field
        ));
        textField.setPreferredSize(new Dimension(200, 30)); // Set a preferred size for text fields for alignment
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 240)); // Light gray background for button area
        buttonPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JButton btnCancel = createStyledButton("Hủy", new Color(108, 117, 125), Color.BLACK);
        btnCancel.addActionListener(e -> dispose()); // Lambda for brevity

        JButton btnDelete = createStyledButton("Xoá", HouseUI.DANGER_COLOR, Color.BLACK);
        btnDelete.addActionListener(e -> {
            // Ensure XoaPhong.showDeleteConfirmDialog is correctly implemented
            // and handles the deletion logic through qLyPhongController.
            boolean confirmedByDeleteDialog = XoaPhong.showDeleteConfirmDialog(
                    (Frame) SwingUtilities.getWindowAncestor(SuaPhong.this),
                    roomToEdit,
                    qLyPhongController
            );

            if (confirmedByDeleteDialog) { // If deletion was successful and confirmed in XoaPhong dialog
                updateConfirmed = true; // Set this dialog's flag to indicate a change occurred
                if (phongPanel != null) {
                    phongPanel.loadRoomData();      // Refresh the list in the parent panel
                    phongPanel.switchToRoomList();  // Switch to the room list view
                }
                dispose(); // Close this "SuaPhong" dialog
            }
        });

        JButton btnUpdate = createStyledButton("Cập nhật", HouseUI.SUCCESS_COLOR, Color.BLACK);
        btnUpdate.addActionListener(e -> {
            if (validateAndUpdateRoom()) {
                dispose(); // Close dialog if update is successful
            }
        });

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnUpdate);

        return buttonPanel;
    }

    private JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        // button.setBorderPainted(false); // Keep border for better UI or use custom rounded border
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16)); // Padding based border
        button.setMargin(new Insets(8, 16, 8, 16)); // Margin for button content
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Indicate clickable
        return button;
    }

    private void loadRoomData() {
        if (roomToEdit == null) {
            // Handle case where roomToEdit might be null, though constructor should prevent this
            JOptionPane.showMessageDialog(this, "Không có thông tin phòng để sửa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        txtRoomName.setText(roomToEdit.getRoomName());
        txtRent.setText(currencyFormatter.format(roomToEdit.getRent()));
        txtMax.setText(roomToEdit.getMax() > 0 ? String.valueOf(roomToEdit.getMax()) : "");

        if ("Đã cho thuê".equals(roomToEdit.getStatus())) {
            cbStatus.setSelectedItem("Đã cho thuê");
        } else {
            cbStatus.setSelectedItem("Còn trống");
        }
    }

    private boolean validateAndUpdateRoom() {
        try {
            String roomName = txtRoomName.getText().trim();
            if (roomName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên phòng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtRoomName.requestFocusInWindow();
                return false;
            }

            String rentText = txtRent.getText().replaceAll("[,.]", ""); // Remove formatting for parsing
            String maxText = txtMax.getText().trim();

            if (rentText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tiền thuê.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtRent.requestFocusInWindow();
                return false;
            }

            double rent;
            int max = 0; // Default to 0 if not specified

            try {
                rent = Double.parseDouble(rentText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Tiền thuê không hợp lệ. Vui lòng nhập số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtRent.requestFocusInWindow();
                return false;
            }

            if (!maxText.isEmpty()) {
                try {
                    max = Integer.parseInt(maxText);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Số người tối đa không hợp lệ. Vui lòng nhập số nguyên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    txtMax.requestFocusInWindow();
                    return false;
                }
            }

            if (rent < 0) {
                JOptionPane.showMessageDialog(this, "Tiền thuê không được là số âm.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtRent.requestFocusInWindow();
                return false;
            }
            if (max < 0) {
                JOptionPane.showMessageDialog(this, "Số người tối đa không được là số âm.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtMax.requestFocusInWindow();
                return false;
            }


            // Cập nhật thông tin phòng vào đối tượng roomToEdit
            roomToEdit.setRoomName(roomName);
            roomToEdit.setRent(rent);
            roomToEdit.setMax(max);
            roomToEdit.setStatus("Còn trống".equals(cbStatus.getSelectedItem().toString()) ? "Còn trống" : "Đã cho thuê");

            // Gọi controller để xử lý việc cập nhật dữ liệu (ví dụ: lưu vào database)
            boolean success = qLyPhongController.processUpdateRoom(roomToEdit);

            if (success) {
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin phòng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                updateConfirmed = true; // Đánh dấu rằng đã có cập nhật thành công

                if (phongPanel != null) {
                    phongPanel.loadRoomData();      // Tải lại dữ liệu trên panel cha
                    phongPanel.switchToRoomList();  // Chuyển về danh sách phòng
                }
                return true; // Trả về true để đóng dialog
            } else {
                // Controller có thể đã hiển thị lỗi riêng, hoặc có thể hiển thị lỗi chung ở đây
                // JOptionPane.showMessageDialog(this, "Không thể cập nhật phòng. Vui lòng thử lại.", "Lỗi cập nhật", JOptionPane.ERROR_MESSAGE);
                return false; // Không đóng dialog nếu cập nhật thất bại
            }

        } catch (Exception e) {
            // Catch-all cho các lỗi không mong muốn khác
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi không mong muốn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // In stack trace ra console để debug
            return false;
        }
    }

    private void setupDialog() {
        setTitle("Sửa thông tin phòng - " + roomToEdit.getRoomName()); // Cập nhật tiêu đề dialog
        setSize(600, 550); // Adjusted height slightly, review based on content
        setMinimumSize(new Dimension(500, 450)); // Prevent dialog from being too small
        setLocationRelativeTo(getOwner()); // Center dialog relative to parent
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Dispose on close to free resources
    }

    public boolean isUpdateConfirmed() {
        return updateConfirmed;
    }

    // Phương thức static để hiển thị dialog một cách tiện lợi
    public static boolean showEditDialog(Frame parent, Room room, QLyPhong controller, phong phongPanel) {
        SuaPhong dialog = new SuaPhong(parent, room, controller, phongPanel);
        dialog.setVisible(true); // Hiển thị dialog và block cho đến khi nó được đóng
        return dialog.isUpdateConfirmed(); // Trả về trạng thái xác nhận cập nhật
    }
}

