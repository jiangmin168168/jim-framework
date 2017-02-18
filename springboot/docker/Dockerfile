FROM java:8

MAINTAINER jim <jiangmin168168@hotmail.com>

WORKDIR /var/app/elasticsearch-2.4.2/bin

ADD elasticsearch-2.4.2 /var/app

ENTRYPOINT ./elasticsearch -d

EXPOSE 9200
EXPOSE 9300
