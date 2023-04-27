FROM phusion/baseimage:focal-1.0.0

# Use baseimage-docker's init system.
CMD ["/sbin/my_init"]

# ...put your own build instructions here...
LABEL org.opencontainers.image.authors = "trenton.kress@gmail.com"

# Install packages
RUN apt-get update
RUN apt-get upgrade -y
RUN apt-get install -y \
		apt-transport-https \
		ca-certificates \
		git \
		git-lfs \
		curl \
		wget \
		gnupg \
		gnupg-agent \
		software-properties-common \
		openjdk-19-jdk

# Install Mongodb
RUN wget -qO - https://www.mongodb.org/static/pgp/server-5.0.asc | apt-key add -
RUN echo "deb http://repo.mongodb.org/apt/debian buster/mongodb-org/5.0 main" | tee /etc/apt/sources.list.d/mongodb-org-5.0.list
RUN apt-get update && apt-get install -y mongodb-org

RUN sed -i "s,\\(^[[:blank:]]*bindIp:\\) .*,\\1 0.0.0.0," /etc/mongod.conf

RUN mkdir /etc/service/mongodb
RUN touch /etc/service/mongodb/run
RUN echo "#!/bin/sh\nexec mongod --config /etc/mongod.conf" > /etc/service/mongodb/run
RUN chmod +x /etc/service/mongodb/run

EXPOSE 27017

# Clone repositories
RUN mkdir /darkan && cd /darkan && \
		git clone https://gitlab.com/darkanrs/cache.git && \
		git clone https://gitlab.com/darkanrs/world-server.git

RUN cd /darkan/world-server && ./gradlew build

RUN mkdir /etc/service/world-server
RUN touch /etc/service/world-server/run
RUN echo "#!/bin/sh\ncd /darkan/world-server/ && exec ./gradlew run" > /etc/service/world-server/run
RUN chmod +x /etc/service/world-server/run

EXPOSE 43595 43596

# Clean up APT when done.
RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*