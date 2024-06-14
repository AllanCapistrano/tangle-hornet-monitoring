FROM openjdk:8
LABEL maintainder="AllanCapistrano <asantos@ecomp.uefs.br>"

RUN wget https://github.com/AllanCapistrano/tangle-hornet-api/releases/download/v1.5.0/tangle-hornet-api

ENV API_PORT=3000 \
    NODE_URL=127.0.0.1 \
    NODE_PORT=14265 \
    READ_INDEX=readIndex \
    WRITE_INDEX=writeIndex \
    READ_MULTIPLE_INDEX=readMultipleIndex \
    ZMQ_SOCKET_PROTOCOL=tcp \
    ZMQ_SOCKET_URL=127.0.0.1 \
    ZMQ_SOCKET_PORT=5556

ADD target/tangle-hornet-monitoring-1.0.0-jar-with-dependencies.jar bin/tangle-hornet-monitoring-1.0.0-jar-with-dependencies.jar
ADD tangle-hornet-config.sh /tangle-hornet-config.sh
ADD tangle-hornet-monitoring.sh tangle-hornet-monitoring.sh

RUN mv ./tangle-hornet-api /bin
RUN chmod +x /bin/tangle-hornet-api
RUN chmod +x tangle-hornet-monitoring.sh
RUN chmod +x tangle-hornet-config.sh

ENTRYPOINT ["/bin/bash", "./tangle-hornet-config.sh"]