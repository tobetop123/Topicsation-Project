import requests
from bs4 import BeautifulSoup
import json
import mysql.connector
import datetime

cnx = mysql.connector.connect(
    host="localhost",
    port="3306",
    user="webuser",
    password="webuser",
    database="webdb"
)

cursor = cnx.cursor()

def get_article_content(article_url):
    article_response = requests.get(article_url)
    article_soup = BeautifulSoup(article_response.text, 'html.parser')
    article_paragraphs = article_soup.find_all('div', {'data-component': 'text-block'})
    return ' '.join([paragraph.get_text(strip=True) for paragraph in article_paragraphs])

# 각 카테고리의 URL
def crawl_bbc_news():
    today = datetime.datetime.today().strftime('%Y-%m-%d')
    urls = [
        'https://www.bbc.com/news/business',
        'https://www.bbc.com/news/technology',
        'https://www.bbc.com/news/science_and_environment',
        'https://www.bbc.com/news/entertainment_and_arts',
        'https://www.bbc.com/news/health'
    ]
    data = {
        'business': [],
        'tech': [],
        'science': [],
        'entertainment': [],
        'health': []
    }

    for url in urls:
        response = requests.get(url)
        soup = BeautifulSoup(response.text, 'html.parser')
        articles = soup.find_all('div', {'class': 'gs-c-promo'})

        for article in articles:
            title_element = article.find('h3', {'class': 'gs-c-promo-heading__title'})
            link_element = article.find('a', {'class': 'gs-c-promo-heading'})

            if title_element is None or link_element is None:
                continue

            title = title_element.get_text(strip=True)
            article_url = link_element['href']

            # article_url이 올바른 형식인지 확인
            if not article_url.startswith('http'):
                article_url = f"https://www.bbc.com{article_url}"

            # 올바른 형식일 때만 content를 가져옵니다.
            content = get_article_content(article_url)

            for category_key in data.keys():
                if category_key.lower() in article_url:
                    data[category_key].append({
                        'title': title,
                        'content': content
                    })

    return data

def save_news_to_database(data):
    for category_key, category_value in data.items():
        if category_key == 'business':
            news_id = 1
        elif category_key == 'tech':
            news_id = 2
        elif category_key == 'science':
            news_id = 3
        elif category_key == 'entertainment':
            news_id = 4
        elif category_key == 'health':
            news_id = 5
        elif category_key == 'today':
            news_id = 6
        else:
            continue

        newsjson = json.dumps(category_value, ensure_ascii=False)
        update_query = f"UPDATE news_suggestion SET newsjson = %s WHERE news_id = %s"
        cursor.execute(update_query, (newsjson, news_id))

    cnx.commit()
    print("데이터베이스 업데이트 완료")

news_data = crawl_bbc_news()

save_news_to_database(news_data)