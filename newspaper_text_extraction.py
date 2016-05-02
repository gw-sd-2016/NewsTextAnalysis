import sys
import newspaper

from newspaper import Article

url = sys.argv[1]
article = Article(url, fetch_images=False)

article.download()

article.parse()

print("'" + article.title.upper() + " " + article.text.replace('\n', ' ') + "',?\n")