import newspaper

from newspaper import Article

url = raw_input('article URL: ')
article = Article(url, fetch_images=False)

article.download()

article.parse()

print("'" + article.title.upper() + " " + article.text.replace('\n', ' ') + "',\n")