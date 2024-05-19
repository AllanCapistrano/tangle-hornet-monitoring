#!/bin/bash

if [ $# -eq 0 ]; then
    arg="-r"
else
    arg=$1
fi

# ------------------- Criando tangle-hornet.conf ----------------------------- #
cat <<EOF >/etc/tangle-hornet.conf
apiPort = $API_PORT

nodeUrl = $NODE_URL
nodePort = $NODE_PORT
EOF

# -------------------- Executando API em segundo plano ----------------------- #
./bin/tangle-hornet-api &

# ---------------------- Iniciando Tangle Reader ----------------------------- #
./tangle-hornet-monitoring.sh $arg