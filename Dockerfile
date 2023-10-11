FROM openjdk:8
LABEL maintainder="AllanCapistrano <asantos@ecomp.uefs.br>"

RUN wget https://github.com/AllanCapistrano/tangle-hornet-api/releases/download/v1.3.1/tangle-hornet-api

ENV API_PORT=3000 \
    NODE_URL=127.0.0.1 \
    NODE_PORT=14265 \
    TAG=clientTag

ADD target/tangle-reader-1.0.0-jar-with-dependencies.jar bin/tangle-reader-1.0.0-jar-with-dependencies.jar
ADD tangle-hornet-config.sh /tangle-hornet-config.sh
ADD tangle-reader.sh tangle-reader.sh

RUN mv ./tangle-hornet-api /bin
RUN chmod +x /bin/tangle-hornet-api
RUN chmod +x tangle-reader.sh
RUN chmod +x tangle-hornet-config.sh

ENTRYPOINT ["/bin/bash", "./tangle-hornet-config.sh"]