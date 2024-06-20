from transformers import pipeline
from textblob import TextBlob

# Load the pre-trained sentiment analysis pipeline for text
sentiment_analyzer = pipeline("sentiment-analysis")

# Sentiment analysis function for text
def analyze_sentiment(text):
    result = sentiment_analyzer(text)
    sentiment = result[0]['label']
    score = result[0]['score']
    return sentiment, score

# Subjectivity and polarity analysis function for text
def analyze_subjectivity_polarity(text):
    blob = TextBlob(text)
    subjectivity = blob.sentiment.subjectivity
    polarity = blob.sentiment.polarity
    return subjectivity, polarity

# Example usage
if __name__ == "__main__":
    input_text = input("Enter the text for sentiment analysis: ")
    sentiment, score = analyze_sentiment(input_text)
    subjectivity, polarity = analyze_subjectivity_polarity(input_text)

    print("\nSentiment Analysis:")
    print(f"Sentiment: {sentiment}")
    print(f"Confidence Score: {score:.4f}")

    if polarity > 0:
        print("The text expresses a positive sentiment.")
    elif polarity < 0:
        print("The text expresses a negative sentiment.")
    else:
        print("The text is neutral.")