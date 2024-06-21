import sys
import logging
from transformers import pipeline
from textblob import TextBlob

# Configure logging
logging.basicConfig(filename='sentiment_analysis.log', level=logging.INFO,
                    format='%(asctime)s - %(levelname)s - %(message)s')

def load_sentiment_analyzer():
    try:
        logging.info("Loading sentiment analyzer")
        return pipeline("sentiment-analysis")
    except Exception as e:
        logging.error(f"Error loading sentiment analyzer: {e}")
        sys.exit(1)

def analyze_sentiment(text, sentiment_analyzer):
    try:
        logging.info(f"Analyzing sentiment for text: {text[:50]}...")
        result = sentiment_analyzer(text)
        sentiment = result[0]['label']
        score = result[0]['score']
        logging.info(f"Sentiment analysis result: {sentiment}, score: {score}")
        return sentiment, score
    except Exception as e:
        logging.error(f"Error during sentiment analysis: {e}")
        return None, None

def analyze_subjectivity_polarity(text):
    try:
        logging.info(f"Analyzing subjectivity and polarity for text: {text[:50]}...")
        blob = TextBlob(text)
        subjectivity = blob.sentiment.subjectivity
        polarity = blob.sentiment.polarity
        logging.info(f"Subjectivity: {subjectivity}, Polarity: {polarity}")
        return subjectivity, polarity
    except Exception as e:
        logging.error(f"Error during subjectivity/polarity analysis: {e}")
        return None, None

def get_sentiment_description(sentiment, score):
    if sentiment == "POSITIVE":
        return "positive" if score > 0.75 else "somewhat positive"
    elif sentiment == "NEGATIVE":
        return "negative" if score > 0.75 else "somewhat negative"
    else:
        return "neutral"

def print_results(sentiment, score, subjectivity, polarity):
    logging.info("Printing analysis results")
    print("\nSentiment Analysis Results:")
    print(f"Sentiment: {sentiment}")
    print(f"Confidence Score: {score:.4f}")
    description = get_sentiment_description(sentiment, score)
    print(f"Description: The text is {description}.")
    print(f"\nSubjectivity: {subjectivity:.4f} (0 = objective, 1 = subjective)")
    print(f"Polarity: {polarity:.4f} (-1 = negative, 1 = positive)")

def main():
    logging.info("Starting sentiment analysis program")
    sentiment_analyzer = load_sentiment_analyzer()
    
    while True:
        input_text = input("\nEnter the text for sentiment analysis (or 'q' to quit): ").strip()
        
        if input_text.lower() == 'q':
            logging.info("User chose to quit the program")
            print("Thank you for using the sentiment analyzer. Goodbye!")
            break
        
        if not input_text:
            logging.warning("User entered empty text")
            print("Please enter some text to analyze.")
            continue
        
        sentiment, score = analyze_sentiment(input_text, sentiment_analyzer)
        subjectivity, polarity = analyze_subjectivity_polarity(input_text)
        
        if sentiment is not None and subjectivity is not None:
            print_results(sentiment, score, subjectivity, polarity)
        else:
            print("An error occurred during the analysis. Please try again.")

if __name__ == "__main__":
    main()
