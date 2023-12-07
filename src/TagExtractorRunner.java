import javax.swing.*;

public class TagExtractorRunner {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TagExtractorFrame frame = new TagExtractorFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
