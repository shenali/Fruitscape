package com.example.fruitscape.Classifiers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Configuration {

    // The tflite model is defined along with input height, width
    public static final String TFLITE_MODEL = "converted_model.tflite";
    public static final int INPUT_WIDTH = 200, INPUT_HEIGHT = 200;
    private static final int FLOAT_SIZE = 4, PX_SIZE = 3;

    //Maximum outputs from the model defined and the model size is calculated
    static final int OUTPUT_RESULTS = 1;
    static final int TFLITE_MODEL_SIZE = FLOAT_SIZE * INPUT_WIDTH * INPUT_HEIGHT * PX_SIZE;

    //Labels of the outputs were put input a String List.
    static final List<String> PREDICTION_LABELS = Collections.unmodifiableList(Arrays.asList("Anthracnose_stage1",
            "Anthracnose_stage2", "Healthy"));
    static final float PREDICTION_INIT = 0.5f, IMAGE_STD = 255.0f;

}
