import tensorflow as tf
from tensorflow.keras import layers, models
from tensorflow.keras.datasets import mnist
import matplotlib.pyplot as plt
import logging
import numpy as np

# Configure logging
logging.basicConfig(filename='mnist_classification.log', level=logging.INFO,
                    format='%(asctime)s - %(levelname)s - %(message)s')

# Load the dataset
logging.info("Loading MNIST dataset")
(x_train, y_train), (x_test, y_test) = mnist.load_data()

# Normalize the data
logging.info("Normalizing data")
x_train, x_test = x_train / 255.0, x_test / 255.0

# Build the model
logging.info("Building the model")
model = models.Sequential([
    layers.Flatten(input_shape=(28, 28)),
    layers.Dense(128, activation='relu'),
    layers.Dropout(0.2),
    layers.Dense(10, activation='softmax')
])

# Compile the model
logging.info("Compiling the model")
model.compile(optimizer='adam',
              loss='sparse_categorical_crossentropy',
              metrics=['accuracy'])

# Train the model
logging.info("Training the model")
history = model.fit(x_train, y_train, epochs=5, validation_split=0.2)

# Log training results
for epoch, acc, val_acc in zip(range(1, 6), history.history['accuracy'], history.history['val_accuracy']):
    logging.info(f"Epoch {epoch}: accuracy={acc:.4f}, validation_accuracy={val_acc:.4f}")

# Evaluate the model
logging.info("Evaluating the model")
test_loss, test_acc = model.evaluate(x_test, y_test)
logging.info(f'Test accuracy: {test_acc:.4f}')
print(f'Test accuracy: {test_acc:.4f}')

# Make predictions
logging.info("Making predictions")
predictions = model.predict(x_test)

# Plot the first 5 test images and their predicted labels
logging.info("Plotting results")
plt.figure(figsize=(10, 10))
for i in range(5):
    plt.subplot(1, 5, i + 1)
    plt.imshow(x_test[i], cmap=plt.cm.binary)
    predicted = np.argmax(predictions[i])
    actual = y_test[i]
    plt.title(f'Pred: {predicted}\nActual: {actual}')
    plt.axis('off')
    logging.info(f"Image {i+1}: Predicted={predicted}, Actual={actual}")
plt.savefig('mnist_predictions.png')
plt.show()

logging.info("MNIST Classification completed")
