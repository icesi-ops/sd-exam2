
from typing import List, Dict
from flask import Flask
import mysql.connector
import json
import logging
import random

app = Flask(__name__)


def cellphones_table() -> List[Dict]:
    config = {
        'user': 'root',
        'password': 'root',
        'host': 'db',
        'port': '3306',
        'database': 'cellphones'
    }
    connection = mysql.connector.connect(**config)
    cursor = connection.cursor()
    cursor.execute('SELECT * FROM cellphones_table')
    results = [{name: brand} for (name, brand) in cursor]
    cursor.close()
    connection.close()

    return results


@app.route('/')
def index() -> str:
    return json.dumps({'cellphones_table': cellphones_table()})


if __name__ == '__main__':
    app.run(host='0.0.0.0')
