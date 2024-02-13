FROM eclipse-temurin:21.0.2_13-jre

# Install packages
RUN apt-get update
RUN apt-get upgrade -y
RUN apt-get install -y \
		apt-transport-https \
		ca-certificates \
        jq \
		git \
		git-lfs \
		curl \
		wget \
		gnupg \
		gnupg-agent \
		software-properties-common

# Clone repositories
RUN mkdir /darkan && cd /darkan && \
		git clone --depth 1 https://gitlab.com/darkanrs/world-server.git

# Entrypoint to set up data folders and cache
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh
ENTRYPOINT ["/docker-entrypoint.sh"]

# Expose ports and run the server
EXPOSE 43595 43596
WORKDIR /darkan/world-server
CMD ["./run-release.sh"]