import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TagExtractorFrame extends JFrame {
    private JTextArea resultTextArea;
    private JFileChooser fileChooser;
    private Map<String, Integer> tagFrequencyMap;
    private Set<String> stopWords;

    public TagExtractorFrame() {
        setTitle("Tag Extractor");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tagFrequencyMap = new HashMap<>();
        stopWords = loadStopWords("stopwords.txt");

        createUI();
    }

    private void createUI() {
        resultTextArea = new JTextArea(20, 40);
        JScrollPane scrollPane = new JScrollPane(resultTextArea);
        JButton chooseFileButton = new JButton("Choose File");
        JButton extractTagsButton = new JButton("Extract Tags");
        JButton saveTagsButton = new JButton("Save Tags");

        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseFile();
            }
        });

        extractTagsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                extractTags();
            }
        });

        saveTagsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTags();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(chooseFileButton);
        buttonPanel.add(extractTagsButton);
        buttonPanel.add(saveTagsButton);

        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void chooseFile() {
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            resultTextArea.setText("Selected File: " + selectedFile.getName());
            tagFrequencyMap.clear();
        }
    }

    private void extractTags() {
        if (tagFrequencyMap.isEmpty()) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null) {
                try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        processLine(line);
                    }
                    displayTags();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                resultTextArea.setText("Please choose a file first.");
            }
        } else {
            resultTextArea.setText("Tags already extracted. Choose a new file to reset.");
        }
    }

    private void processLine(String line) {
        String[] words = line.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
        for (String word : words) {
            if (!stopWords.contains(word)) {
                tagFrequencyMap.put(word, tagFrequencyMap.getOrDefault(word, 0) + 1);
            }
        }
    }

    private void displayTags() {
        resultTextArea.setText("Tags and their frequencies:\n");
        for (Map.Entry<String, Integer> entry : tagFrequencyMap.entrySet()) {
            resultTextArea.append(entry.getKey() + ": " + entry.getValue() + "\n");
        }
    }

    private void saveTags() {
        if (!tagFrequencyMap.isEmpty()) {
            JFileChooser saveFileChooser = new JFileChooser();
            saveFileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

            int result = saveFileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File saveFile = saveFileChooser.getSelectedFile();
                try (PrintWriter writer = new PrintWriter(saveFile)) {
                    for (Map.Entry<String, Integer> entry : tagFrequencyMap.entrySet()) {
                        writer.println(entry.getKey() + ": " + entry.getValue());
                    }
                    resultTextArea.append("Tags saved to: " + saveFile.getName());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            resultTextArea.setText("No tags to save. Extract tags first.");
        }
    }

    private Set<String> loadStopWords(String stopWordsFile) {
        Set<String> stopWordsSet = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(stopWordsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stopWordsSet.add(line.toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopWordsSet;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TagExtractorFrame().setVisible(true);
            }
        });
    }
}

