package com.example.fruitscape.ml;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ModelConfig {

    public static final String MODEL_FILENAME = "converted_model.tflite";

    public static final int INPUT_IMG_SIZE_WIDTH = 200, INPUT_IMG_SIZE_HEIGHT = 200;
    private static final int FLOAT_TYPE_SIZE = 4, PIXEL_SIZE = 3;
    static final int MAX_CLASSIFICATION_RESULTS = 1;

    //public static final int IMAGE_MEAN = 70;

    static final int MODEL_INPUT_SIZE = FLOAT_TYPE_SIZE * INPUT_IMG_SIZE_WIDTH * INPUT_IMG_SIZE_HEIGHT * PIXEL_SIZE;

    static final List<String> OUTPUT_LABELS = Collections.unmodifiableList(Arrays.asList("Anthracnose_stage1",
            "Anthracnose_stage2", "Healthy"));


    static final float CLASSIFICATION_THRESHOLD = 0.5f, IMAGE_STD = 255.0f;

}
