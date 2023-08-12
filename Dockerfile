FROM openjdk:8
LABEL maintainder="AllanCapistrano <asantos@ecomp.uefs.br>"

ENV PROTOCOL=https \
    URL=nodes.devnet.iot.org \
    PORT=443 \
    TAG=clientTag

ADD target/tangle-reader-1.0.0-jar-with-dependencies.jar bin/tangle-reader-1.0.0-jar-with-dependencies.jar
ADD tangle-reader.sh tangle-reader.sh

RUN chmod +x tangle-reader.sh

CMD ./tangle-reader.sh