# tangle-hornet-monitoring
Faz a leitura e monitora o tempo de resposta em milisegundos (ms) de consultas a transações na *Tangle Hornet* a partir de um índice.

## Como utilizar
Recomendamos a utilização do Docker, mas também é possível executar o projeto através do arquivo `.jar`.

### Via Docker

Você pode utilizar a nossa imagem que está disponível no [Docker Hub](https://hub.docker.com/r/larsid/tangle-reader), ou fazer o *build* da imagem manualmente.

#### Build da imagem Docker:

1. Clone este projeto;
2. Acesse o diretório do projeto;
3. Digite o comando para realizar o *build* da imagem:
   ```powershell
   docker build -t larsid/tangle-hornet-monitoring:<tag_name> .
   ```
4. Execute o container<sup>1</sup>:
   ```powershell
   docker run -it larsid/tangle-hornet-monitoring:<tag_name>
   ```
   
###### Obs<sup>1</sup>: Dessa maneira irá executar com as configurações padrões. ############
   
### Via `.jar`

1. Clone este projeto;
2. Acesse o diretório do projeto;
3. Compile o projeto:
   ```powershell
   mvn clean compile assembly:single
   ```
4. Execute o projeto<sup>2</sup>:
   ```powershell
   java -jar target/tangle-reader-1.0.0-jar-with-dependencies.jar
   ```
   
###### Obs<sup>2</sup>: Dessa maneira irá executar com as configurações padrões. ############

## Sobrescrevendo as configurações padrões

### Via Docker

| Parâmetro | Descrição | Valor padrão |
| --------- | --------- | ------------ |
| API_PORT | Porta na qual a API irá executar | 3000
| NODE_URL | URL do nó da *Tangle Hornet* |	127.0.0.1
| NODE_PORT | Porta do nó da *Tangle Hornet* |	14265
| READ_INDEX | Índice que será utilizada para as consultas de leitura na *Tangle Hornet*. | readIndex |
| WRITE_INDEX | Índice que será utilizada para as escritas na *Tangle Hornet*. | writeIndex |

Após realizar o *build* da imagem ou utilizando a imagem disponibilizada no [Docker Hub](https://hub.docker.com/r/larsid/tangle-reader), basta utilizar os parâmetros acima.

#### Exemplo:

```powershell
docker run -it -e API_PORT=3000 -e NODE_URL=172.18.0.5 -e INDEX=my_index larsid/tangle-hornet-monitoring:<tag_name>

```

### Via `.jar`

| Parâmetro | Descrição | Valor padrão |
| --------- | --------- | ------------ |
| -apt | Porta na qual a API irá executar | 3000
| -ridx | Índice que será utilizada para as consultas de leitura na *Tangle Hornet*. | readIndex |
| -widx | Índice que será utilizada para as escritas na *Tangle Hornet*. | writeIndex |
| -r | Monitoramento de leitura | None |
| -w | Monitoramento de escrita | None |

###### Obs: Também é possível alterar essas configurações através do arquivo [tangle-reader.properties](./src/main/resources/br/uefs/larsid/iot/soft/tangle-reader.properties) ######

Após compilar o projeto, basta utilizar os parâmetros acima.

#### Exemplo:

```powershell
java -jar target/tangle-reader-1.0.0-jar-with-dependencies.jar -apt 3000 -ridx my_index
```
