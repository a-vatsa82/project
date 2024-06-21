import sys
from transformers import pipeline
from textblob import TextBlob

def load_sentiment_analyzer():
    try:
        return pipeline("sentiment-analysis")
    except Exception as e:
        print(f"Error loading sentiment analyzer: {e}")
        sys.exit(1)

def analyze_sentiment(text, sentiment_analyzer):
    try:
        result = sentiment_analyzer(text)
        sentiment = result[0]['label']
        score = result[0]['score']
        return sentiment, score
    except Exception as e:
        print(f"Error during sentiment analysis: {e}")
        return None, None

def analyze_subjectivity_polarity(text):
    try:
        blob = TextBlob(text)
        subjectivity = blob.sentiment.subjectivity
        polarity = blob.sentiment.polarity
        return subjectivity, polarity
    except Exception as e:
        print(f"Error during subjectivity/polarity analysis: {e}")
        return None, None

def get_sentiment_description(sentiment, score):
    if sentiment == "POSITIVE":
        return "positive" if score > 0.75 else "somewhat positive"
    elif sentiment == "NEGATIVE":
        return "negative" if score > 0.75 else "somewhat negative"
    else:
        return "neutral"

def print_results(sentiment, score, subjectivity, polarity):
    print("\nSentiment Analysis Results:")
    print(f"Sentiment: {sentiment}")
    print(f"Confidence Score: {score:.4f}")
    print(f"Description: The text is {get_sentiment_description(sentiment, score)}.")
    print(f"\nSubjectivity: {subjectivity:.4f} (0 = objective, 1 = subjective)")
    print(f"Polarity: {polarity:.4f} (-1 = negative, 1 = positive)")

def main():
    sentiment_analyzer = load_sentiment_analyzer()
    
    while True:
        input_text = input("\nEnter the text for sentiment analysis (or 'q' to quit): ").strip()
        
        if input_text.lower() == 'q':
            print("Thank you for using the sentiment analyzer. Goodbye!")
            break
        
        if not input_text:
            print("Please enter some text to analyze.")
            continue
        
        sentiment, score = analyze_sentiment(input_text, sentiment_analyzer)
        subjectivity, polarity = analyze_subjectivity_polarity(input_text)
        
        if sentiment is not None and subjectivity is not None:
            print_results(sentiment, score, subjectivity, polarity)

if __name__ == "__main__":
    main()
