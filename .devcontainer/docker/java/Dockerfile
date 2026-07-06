ARG  IMAGE_TAG=25.0.4_7-jdk-noble
FROM eclipse-temurin:${IMAGE_TAG}

ENV TZ=Asia/Tokyo
ENV PLAYWRIGHT_BROWSERS_PATH=/ms-playwright

RUN apt-get update && \
apt-get install -y --no-install-recommends \
git tree ca-certificates curl gnupg postgresql-client && \
apt-get clean && \
rm -rf /var/lib/apt/lists/*

RUN mkdir -p /etc/apt/keyrings && \
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg && \
echo "deb [signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu noble stable" | tee /etc/apt/sources.list.d/docker.list && \
apt-get update && \
apt-get install -y --no-install-recommends docker-ce-cli && \
apt-get clean && \
rm -rf /var/lib/apt/lists/*

ARG NODE_MAJOR=24
RUN mkdir -p /etc/apt/keyrings && \
curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key | gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg && \
echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_${NODE_MAJOR}.x nodistro main" | tee /etc/apt/sources.list.d/nodesource.list && \
apt-get update && \
apt-get install -y --no-install-recommends nodejs && \
apt-get clean && \
rm -rf /var/lib/apt/lists/*

ARG PLAYWRIGHT_VERSION=1.62.0
RUN npm install -g playwright@$PLAYWRIGHT_VERSION && \
    playwright install-deps && \
    playwright install chromium firefox webkit && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ARG GITLEAKS_VERSION=8.30.1
RUN curl -fsSL https://github.com/gitleaks/gitleaks/releases/download/v${GITLEAKS_VERSION}/gitleaks_${GITLEAKS_VERSION}_linux_x64.tar.gz | tar -xz -C /usr/local/bin gitleaks

CMD [ "sleep", "infinity" ]
