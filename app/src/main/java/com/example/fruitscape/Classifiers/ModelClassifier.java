package com.example.fruitscape.Classifiers;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import com.example.fruitscape.POJOModels.PredictionResult;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import static com.example.fruitscape.Classifiers.Configuration.IMAGE_STD;

public class ModelClassifier {

    private final Interpreter newInterpreter;

    //Parameterized constructor with interpreter
    private ModelClassifier(Interpreter interpreter) {
        newInterpreter = interpreter;
    }

    //Load the model file and initialize the classifier by passing the interpreter to the classifier
    public static ModelClassifier classifierGenerator(AssetManager assetM, String modelLocation) throws IOException {
        MappedByteBuffer byteBufferVal = tfliteModelLoader(assetM, modelLocation);
        Interpreter interpreter = new Interpreter(byteBufferVal);

        return new ModelClassifier(interpreter);
    }

    // Outputs prediction result
    public List<PredictionResult> predictImage(Bitmap bitmap) {
        ByteBuffer byteBuffer = bitToByteBufferConversion(bitmap);
        float[][] predictionResult = new float[1][Configuration.PREDICTION_LABELS.size()];

        newInterpreter.run(byteBuffer, predictionResult);

        return getClassifiedResult(predictionResult);
    }

    //Returns the sorted results arralist which are prediction label and the confidence level
    private List<PredictionResult> getClassifiedResult(float[][] result) {
        PriorityQueue<PredictionResult> classifiedResults = new PriorityQueue<>(
                Configuration.OUTPUT_RESULTS,
                (lhs, rhs) -> Float.compare(rhs.predictionConfidence, lhs.predictionConfidence)
        );

        for (int i = 0; i < Configuration.PREDICTION_LABELS.size(); ++i) {
            float confidence = result[0][i];

            if (confidence > Configuration.PREDICTION_INIT) {
                Configuration.PREDICTION_LABELS.size();
                classifiedResults.add(new PredictionResult(Configuration.PREDICTION_LABELS.get(i), confidence));
            }
        }

        return new ArrayList<>(classifiedResults);
    }
    
    // converts the bit values of the input images to bytebuffer
    private ByteBuffer bitToByteBufferConversion(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(Configuration.TFLITE_MODEL_SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] pixelsVals = new int[Configuration.INPUT_WIDTH * Configuration.INPUT_HEIGHT];
        bitmap.getPixels(pixelsVals, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int px : pixelsVals) {
            float r_channel = ((px >> 16) & 0xFF) / IMAGE_STD;
            float g_channel = ((px >> 8) & 0xFF) / IMAGE_STD;
            float b_channel = ((px) & 0xFF) / IMAGE_STD;

            byteBuffer.putFloat(r_channel);
            byteBuffer.putFloat(g_channel);
            byteBuffer.putFloat(b_channel);
        }

        return byteBuffer;
    }
    // map the bytebuffers using a FileDescriptor to load the model from location and assetmanager
    private static MappedByteBuffer tfliteModelLoader(AssetManager assetM, String modelLocation) throws IOException {
        AssetFileDescriptor aFDescriptor = assetM.openFd(modelLocation);

        long startOffVal = aFDescriptor.getStartOffset();
        long declaredLenVal = aFDescriptor.getDeclaredLength();

        FileInputStream fileInput = new FileInputStream(aFDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInput.getChannel();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffVal, declaredLenVal);
    }

}
