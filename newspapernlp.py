import newspaper

from newspaper import Article

url = raw_input('article URL: ')
article = Article(url, fetch_images=False)

article.download()

article.parse()

article.nlp()

print(article.keywords)