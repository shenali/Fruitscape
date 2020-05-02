import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

import cv2
from glob import glob
import tensorflow as tf
from keras.utils import np_utils
from keras.preprocessing.image import ImageDataGenerator
from keras.models import Sequential
from keras.layers import Dense
from keras.layers import Dropout
from keras.layers import Flatten
from keras.layers.convolutional import Conv2D
from keras.layers.convolutional import MaxPooling2D
from keras.layers import BatchNormalization

from sklearn import preprocessing
from sklearn.model_selection import train_test_split

from keras.preprocessing.image import save_img
from keras.preprocessing.image import img_to_array
from keras.regularizers import l2

# Declare image size
scale = 150
seed = 7

image_path = 'Data3/Train/*/*.png'
papayaImages = glob(image_path)
training_data_set = []
training_data_labels = []
imageCount = len(papayaImages)
iterator = 1

# reading images, assigning labels and resizing the image
for i in papayaImages:
    print(str(iterator) + '/' + str(imageCount), end='\r')
    training_data_set.append(cv2.resize(cv2.imread(i), (scale, scale)))
    training_data_labels.append(i.split('/')[-2])
    iterator = iterator + 1
training_data_set = np.asarray(training_data_set)
training_data_labels = pd.DataFrame(training_data_labels)

pre_processed_data = []
getEx = True

# converting the BGR image to RGB
for img in training_data_set:
    new = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    pre_processed_data.append(new)

    if getEx:
        plt.subplot(2, 3, 1)
        plt.imshow(img)
        plt.subplot(2, 3, 2);
        plt.imshow(new)
        plt.show()
        getEx = False
pre_processed_data = np.asarray(pre_processed_data)

dataLabels = preprocessing.LabelEncoder()
dataLabels.fit(training_data_labels[0])
print('Classes' + str(dataLabels.classes_))
encrypted_labels = dataLabels.transform(training_data_labels[0])
clear_all_labels = np_utils.to_categorical(encrypted_labels)
imageClasses = clear_all_labels.shape[1]
print(str(imageClasses))
# traininglabels[0].value_counts().plot(kind='pie')

pre_processed_data = pre_processed_data / 255
x_train, x_test, y_train, y_test = train_test_split(pre_processed_data,
                                                    clear_all_labels,
                                                    test_size=0.1,
                                                    random_state=seed,
                                                    stratify=clear_all_labels, )

training_data_generator = ImageDataGenerator(rotation_range=180,
                                             zoom_range=0.1,
                                             width_shift_range=0.1,
                                             height_shift_range=0.1,
                                             horizontal_flip=True,
                                             vertical_flip=True,
                                             )
training_data_generator.fit(x_train)

np.random.seed(seed)

papaya_classifier_model = Sequential()

papaya_classifier_model.add(Conv2D(filters=64, kernel_size=(5, 5), input_shape=(scale, scale, 3), activation='relu'))
papaya_classifier_model.add(BatchNormalization(axis=3))
papaya_classifier_model.add(Conv2D(filters=64, kernel_size=(5, 5), activation='relu'))
papaya_classifier_model.add(MaxPooling2D((2, 2)))
papaya_classifier_model.add(BatchNormalization(axis=3))
papaya_classifier_model.add(Dropout(0.1))

papaya_classifier_model.add(Conv2D(filters=128, kernel_size=(5, 5), activation='relu'))
papaya_classifier_model.add(BatchNormalization(axis=3))
papaya_classifier_model.add(Conv2D(filters=128, kernel_size=(5, 5), activation='relu'))
papaya_classifier_model.add(MaxPooling2D((2, 2)))
papaya_classifier_model.add(BatchNormalization(axis=3))
papaya_classifier_model.add(Dropout(0.1))

papaya_classifier_model.add(Conv2D(filters=256, kernel_size=(5, 5), activation='relu'))
papaya_classifier_model.add(BatchNormalization(axis=3))
papaya_classifier_model.add(Conv2D(filters=256, kernel_size=(5, 5), activation='relu'))
papaya_classifier_model.add(MaxPooling2D((2, 2)))
papaya_classifier_model.add(BatchNormalization(axis=3))
papaya_classifier_model.add(Dropout(0.1))

papaya_classifier_model.add(Flatten())

papaya_classifier_model.add(Dense(256, activation='relu'))
papaya_classifier_model.add(BatchNormalization())
papaya_classifier_model.add(Dropout(0.5))

papaya_classifier_model.add(Dense(256, activation='relu'))
papaya_classifier_model.add(BatchNormalization())
papaya_classifier_model.add(Dropout(0.5))

# papaya_classifier_model.add(Dense(imageClasses, activation='softmax'))
# papaya_classifier_model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])

papaya_classifier_model.add(Dense(imageClasses, W_regularizer=l2(0.01), activation='linear'))

papaya_classifier_model.compile(loss='hinge',
              optimizer='adadelta',
              metrics=['accuracy'])

papaya_classifier_model.summary()

print(x_test.shape, y_test.shape)
history = papaya_classifier_model.fit(x_train, y_train, epochs=2)

plt.plot(history.history['loss'])
plt.xlabel('Epochs')
plt.ylabel('Loss')
plt.show()

plt.plot(history.history['accuracy'])
plt.xlabel('Epochs')
plt.ylabel('Accuracy')
plt.show()

prediction_labels = papaya_classifier_model.predict(x_test)
print(prediction_labels.shape)
validation_accuracy = papaya_classifier_model.evaluate(x_test, y_test)
print("Validation Accuracy : {}".format(validation_accuracy[-1] * 100))

# Save model to an .h5 file
save_model = "SavedModels/keras_model_250px.h5"
papaya_classifier_model.save(save_model)

# convert the model to a tflite format
tensorflow_model = tf.keras.models.load_model(save_model)
converter = tf.lite.TFLiteConverter.from_keras_model(tensorflow_model)
converted_tflite_model = converter.convert()
open("SavedModels/converted_model_250px.tflite", "wb").write(converted_tflite_model)

test_image_id = [1, 2, 3, 4]
for id in test_image_id:
    pred_label = papaya_classifier_model.predict(x_test)
    print(pred_label[id])
    print(np.argmax(pred_label[id], axis=0))
    print("Predicted label : " + str(dataLabels.classes_[np.argmax(pred_label[id], axis=0)]))

    plt.title("Actual label : " + str(dataLabels.classes_[np.argmax(y_test[id], axis=0)]))

    plt.imshow(x_test[id])
    plt.show()

    img = x_test[id]
    img_array = img_to_array(img)
    # save the image with a new filename
    save_img("TestImages/test" + str(id) + ".png", img_array)
