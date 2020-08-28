package com.example.fruitscape.POJOModels;

import java.util.Locale;

public class PredictionResult {

    public final String predictionLabel;
    public final float predictionConfidence;

    public PredictionResult(String label, float confidence) {
        this.predictionLabel = label;
        this.predictionConfidence = confidence;
    }

    @Override
    public String toString() {
        return predictionLabel + " " + String.format(Locale.getDefault(), "(%.1f%%) ", predictionConfidence * 100.0f);
    }

}
