/*
 * Copyright 2020 Johan Dykström
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.dykstrom.javafx;

import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import se.dykstrom.javafx.service.ConversionService;
import se.dykstrom.javafx.service.ConversionServiceImpl;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * A controller class for the main view.
 */
public class MainController implements Initializable {

    @FXML
    public TextField textField1;
    @FXML
    public TextField textField2;
    @FXML
    public Spinner<Integer> spinner1;
    @FXML
    public Spinner<Integer> spinner2;
    @FXML
    public Label errorLabel1;
    @FXML
    public Label errorLabel2;

    private final InvalidationListener textFieldListener1 = observable -> convertFrom1To2();
    private final InvalidationListener textFieldListener2 = observable -> convertFrom2To1();

    private final ConversionService conversionService = new ConversionServiceImpl();

    /** A reference to the running animation, or null if no animation is running. */
    private ColorBlinkTransition blink;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        spinner1.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Character.MIN_RADIX, Character.MAX_RADIX, 10));
        spinner2.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Character.MIN_RADIX, Character.MAX_RADIX, 16));

        spinner1.valueProperty().addListener(textFieldListener2);
        spinner2.valueProperty().addListener(textFieldListener1);

        textField1.textProperty().addListener(textFieldListener1);
        textField2.textProperty().addListener(textFieldListener2);
    }

    private void convertFrom1To2() {
        convertNumber(textField1, textField2, textFieldListener2, spinner1, spinner2, errorLabel1);
    }

    private void convertFrom2To1() {
        convertNumber(textField2, textField1, textFieldListener1, spinner2, spinner1, errorLabel2);
    }

    /**
     * Converts the number in {@code fromTextField} from the base defined by {@code fromSpinner} to the base
     * defined by {@code toSpinner} and, if successful, stores the result in {@code toTextField}. If the number
     * cannot be converted, an error message is displayed in the {@code fromErrorLabel}.
     */
    private void convertNumber(TextField fromTextField,
                               TextField toTextField,
                               InvalidationListener invalidationListener,
                               Spinner<Integer> fromSpinner,
                               Spinner<Integer> toSpinner,
                               Label fromErrorLabel) {
        // Temporary remove listener to avoid creating an infinite loop
        toTextField.textProperty().removeListener(invalidationListener);

        if (fromTextField.getText().isBlank()) {
            fromErrorLabel.setText("");
            toTextField.setText("");
        } else {
            try {
                String convertedValue = conversionService.convertNumber(fromTextField.getText().strip(), fromSpinner.getValue(), toSpinner.getValue());
                fromErrorLabel.setText("");
                toTextField.setText(convertedValue);

                // Only start a new transition if there is no running transition
                // We don't have to worry about threads here, because we are always
                // running on the JavaFX Application Thread
                if (blink == null) {
                    blink = new ColorBlinkTransition(Duration.seconds(0.5), toTextField, Color.LIGHTGREEN);
                    blink.setOnFinished(event -> blink = null);
                    blink.play();
                }
            } catch (NumberFormatException e) {
                fromErrorLabel.setText("Not a number in base " + fromSpinner.getValue());
                toTextField.setText("");
            }
        }

        // Add the listener back again
        toTextField.textProperty().addListener(invalidationListener);
    }
}
