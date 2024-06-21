# Use Ubuntu as the base image
FROM ubuntu:20.04

# Avoid prompts from apt
ENV DEBIAN_FRONTEND=noninteractive

# Install common dependencies
RUN apt-get update && apt-get install -y \
    build-essential \
    cmake \
    git \
    wget \
    python3 \
    python3-pip \
    openjdk-11-jdk \
    maven \
    libgl1-mesa-glx \
    libglib2.0-0 \
    && rm -rf /var/lib/apt/lists/*

# Set up Python
RUN ln -s /usr/bin/python3 /usr/bin/python
RUN pip3 install --upgrade pip

# Install Python dependencies
COPY requirements.txt .
RUN pip3 install --no-cache-dir -r requirements.txt

# Set up work directory
WORKDIR /app

# Copy all source files
COPY . .

# Compile C++ program
RUN g++ -std=c++17 -o file_explorer cpp_file_explorer/main.cpp -lstdc++fs

# Build Java program
RUN cd java_chatbot && mvn clean package

# Create a shell script to run the desired program
RUN echo '#!/bin/bash\n\
case $1 in\n\
  cpp) ./file_explorer ;;\n\
  mnist) python python_mnist/mnist_classification.py ;;\n\
  java) java -jar java_chatbot/target/simple-chatbot-1.0-SNAPSHOT.jar ;;\n\
  sentiment) python python_sentiment/sentiment_analysis.py ;;\n\
  *) echo "Usage: ./run.sh [cpp|mnist|java|sentiment]" ;;\n\
esac' > run.sh && chmod +x run.sh

# Set the entrypoint to the shell script
ENTRYPOINT ["./run.sh"]

# Default command (if no argument is provided to docker run)
CMD ["cpp"]
