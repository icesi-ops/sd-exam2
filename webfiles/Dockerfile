FROM python:3.6

EXPOSE 5000

WORKDIR /webfiles

COPY requirements.txt /webfiles
RUN pip install -r requirements.txt

COPY app.py /webfiles
CMD python app.py
