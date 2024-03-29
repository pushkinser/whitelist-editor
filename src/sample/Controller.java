package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Controller {

    private static final String NEW_LINE_REGEXP = "\n";
    private static final String RUSSIAN_WORD_REGEXP = "[А-я]";
    private static final String RUSSIAN_WORD_AND_SLASH_REGEXP = "[А-я]\\/[А-я]";
    private static final String WHITESPACE = "\\s+";
    private static final String NEW_LINE = "\\n";

    @FXML
    private TextArea rawEndpoint;

    @FXML
    private TextField whitelistData;

    @FXML
    private Label rawCount;

    @FXML
    public void initialize() {
        rawEndpoint.textProperty().addListener((observable, oldValue, newValue) -> {
            String endpointText = rawEndpoint.getText();
            String[] splitRowEndpoints = endpointText.split(NEW_LINE_REGEXP);
            rawCount.setText(String.valueOf(splitRowEndpoints.length));
        });
    }

    @FXML
    public void convertToWhitelistData() {
        cleanUpRawTextFields();

        String endpointsText = rawEndpoint.getText();

        String[] splitRowEndpoints = endpointsText.split(NEW_LINE_REGEXP);
        StringBuilder singleRow = convertEndpointsToSingleRow(splitRowEndpoints);
        String resultDataWhitelist = singleRow.toString();

        whitelistData.setText(resultDataWhitelist);
    }

    @FXML
    public void copyWhitelistData() {
        convertToWhitelistData();

        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();

        String whitelistDataText = whitelistData.getText();

        content.putString(whitelistDataText);
        clipboard.setContent(content);
    }

    private void cleanUpRawTextFields() {
        String endpointsText = rawEndpoint.getText();

        String[] splitRowEndpoints = endpointsText.split(NEW_LINE_REGEXP);
        List<String> endpoints = Arrays.asList(splitRowEndpoints);

        String resultDataWhitelist = endpoints.stream()
                                              .map(s -> s.replaceAll(RUSSIAN_WORD_AND_SLASH_REGEXP, ""))
                                              .map(s -> s.replaceAll(RUSSIAN_WORD_REGEXP, ""))
                                              .map(s -> s.replaceAll(WHITESPACE, ""))
                                              .filter(s -> !s.isEmpty())
                                              .map(endpoint -> endpoint + NEW_LINE_REGEXP)
                                              .distinct()
                                              .collect(Collectors.joining());

        //TODO: Add string sorting.

        rawEndpoint.setText(resultDataWhitelist);
    }

    private StringBuilder convertEndpointsToSingleRow(String[] splitRowEndpoints) {
        StringBuilder sb = new StringBuilder();
        int endpointCount = splitRowEndpoints.length - 1;

        for (int i = 0; i < endpointCount; i++) {
            String endpoint = splitRowEndpoints[i];
            sb.append(endpoint).append(NEW_LINE);
        }
        sb.append(splitRowEndpoints[endpointCount]);

        return sb;
    }
}
