import sys
import newspaper

from newspaper import Article

url = sys.argv[1]
article = Article(url, fetch_images=False)

article.download()

article.parse()

article.nlp()

keywords = article.keywords

for keyword in keywords:
    print(keyword)