package com.housemanagement.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.net.URL;

public class ImagePanel extends JPanel {
    private Image backgroundImage;

    public ImagePanel(String imagePath) {
        // Cố gắng tải từ hệ thống tệp trước
        File imageFile = new File(imagePath);
        if (imageFile.exists() && !imageFile.isDirectory()) {
            try {
                backgroundImage = ImageIO.read(imageFile);
            } catch (IOException e) {
                System.err.println("Lỗi khi tải ảnh từ hệ thống tệp: " + imagePath);
                e.printStackTrace();
                loadFromResources(imagePath); // Thử tải từ resources nếu thất bại
            }
        } else {
            loadFromResources(imagePath); // Nếu không tìm thấy trong hệ thống tệp, thử tải từ resources
        }

        if (backgroundImage == null) {
            System.err.println("Không thể tải ảnh nền: " + imagePath + ". Vui lòng kiểm tra đường dẫn và vị trí file.");
        }
    }

    private void loadFromResources(String imagePath) {
        try {
            // Giả sử ảnh nằm trong thư mục resources cùng cấp với src, hoặc trong classpath
            // Ví dụ: nếu imagePath là "apartment.jpg" và nó nằm trong src/main/resources
            // và đường dẫn sẽ là "/apartment.jpg" (bắt đầu bằng /)
            String resourcePath = imagePath.startsWith("/") ? imagePath : "/" + imagePath;
            URL imageUrl = getClass().getResource(resourcePath);
            if (imageUrl != null) {
                backgroundImage = ImageIO.read(imageUrl);
            } else {
                System.err.println("Không tìm thấy ảnh trong resources: " + resourcePath + " (Đường dẫn thử: " + resourcePath + ")");
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi tải ảnh từ resources: " + imagePath);
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            int panelWidth = this.getWidth();
            int panelHeight = this.getHeight();
            int imgWidth = backgroundImage.getWidth(this);
            int imgHeight = backgroundImage.getHeight(this);

            if (imgWidth <= 0 || imgHeight <= 0) { // Ảnh không hợp lệ
                g.setColor(getBackground()); // Vẽ màu nền của panel nếu ảnh lỗi
                g.fillRect(0, 0, panelWidth, panelHeight);
                return;
            }

            double imgAspect = (double) imgWidth / imgHeight;
            double panelAspect = (double) panelWidth / panelHeight;

            int newImgWidth;
            int newImgHeight;

            // Tính toán kích thước mới để giữ tỷ lệ và vừa với panel
            if (panelAspect > imgAspect) {
                // Panel rộng hơn ảnh (so với chiều cao), ảnh sẽ vừa chiều cao panel
                newImgHeight = panelHeight;
                newImgWidth = (int) (newImgHeight * imgAspect);
            } else {
                // Panel cao hơn ảnh (so với chiều rộng) hoặc tỷ lệ bằng nhau, ảnh sẽ vừa chiều rộng panel
                newImgWidth = panelWidth;
                newImgHeight = (int) (newImgWidth / imgAspect);
            }

            // Căn giữa ảnh trong panel
            int x = (panelWidth - newImgWidth) / 2;
            int y = (panelHeight - newImgHeight) / 2;

            g.drawImage(backgroundImage, x, y, newImgWidth, newImgHeight, this);
        } else {
            // Vẽ màu nền mặc định nếu không có ảnh
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
