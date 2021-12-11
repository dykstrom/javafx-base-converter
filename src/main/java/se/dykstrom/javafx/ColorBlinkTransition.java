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

import java.util.List;

import javafx.animation.Transition;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.util.Duration;

/**
 * A transition that briefly changes that color of a text field, and makes it blink.
 */
class ColorBlinkTransition extends Transition {

    private static final int MAX_INDEX = 100;

    /** An array of pre-calculated backgrounds to set while interpolating. */
    private final Background[] backgrounds = new Background[MAX_INDEX + 1];

    private final TextField textField;

    /**
     * Creates a new transition that makes the given {@code textField} blink in color {@code blinkColor}
     * for the given {@code duration}.
     */
    public ColorBlinkTransition(Duration duration, TextField textField, Color blinkColor) {
        this.textField = textField;

        Color startColor = getStartColor(textField);
        BackgroundFill[] fills = textField.getBackground().getFills().toArray(new BackgroundFill[0]);
        BackgroundFill originalFill = fills[fills.length - 1];

        for (int i = 0; i < MAX_INDEX; i++) {
            Color color = calculateColor(startColor, blinkColor, 1.0 * i / MAX_INDEX);
            fills[fills.length - 1] = new BackgroundFill(color, originalFill.getRadii(), originalFill.getInsets());
            backgrounds[i] = new Background(fills);
        }
        // Use the original fill in the last background so the original will be restored
        fills[fills.length - 1] = originalFill;
        backgrounds[MAX_INDEX] = new Background(fills);

        setCycleDuration(duration);
    }

    @Override
    protected void interpolate(double frac) {
        int index = (int) (MAX_INDEX * frac);
        textField.setBackground(backgrounds[index]);
    }

    /**
     * Finds out the start color from the given text field.
     */
    private Color getStartColor(TextField textField) {
        List<BackgroundFill> fills = textField.getBackground().getFills();
        Paint paint = fills.get(fills.size() - 1).getFill();
        if (paint instanceof Color color) {
            return color;
        } else if (paint instanceof LinearGradient gradient) {
            List<Stop> stops = gradient.getStops();
            return stops.get(stops.size() - 1).getColor();
        }
        throw new IllegalArgumentException("Unsupported paint class: " + paint.getClass().getName());
    }

    /**
     * Calculates a color for the given fraction in time by interpolating the {@code startColor}
     * and {@code endColor}, taking into account the threshold that defines when in time to reverse
     * the interpolation.
     */
    private Color calculateColor(Color startColor, Color endColor, double frac) {
        double threshold = 0.05;

        Color color;
        if (frac < threshold) {
            color = startColor.interpolate(endColor, frac / threshold);
        } else {
            color = startColor.interpolate(endColor, (1.0 - frac) / (1.0 - threshold));
        }
        return color;
    }
}
