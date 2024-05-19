package br.uefs.larsid.iot.soft.utils;

import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * 
 * @author Allan Capistrano
 * @version 1.0.0
 */
public class CsvWriter {

  private String filePath;
  private CSVWriter writer;
  private static final Logger logger = Logger.getLogger(
    CsvWriter.class.getName()
  );

  /**
   * Método construtor.
   * 
   * @param fileName String - Nome do arquivo.
   */
  public CsvWriter(String fileName) {
    this.filePath = "./";

    this.createFile(fileName);
  }

  /**
   * Método construtor.
   * 
   * @param filePath String - Caminho onde o arquivo será criado
   * @param fileName String - Nome do arquivo.
   */
  public CsvWriter(String filePath, String fileName) {
    this.filePath = filePath;
    
    this.createFile(fileName);
  }

  /**
   * Cria o arquivo .csv.
   *
   * @param fileName String - Nome do arquivo. Obs: Não é necessário passar o
   * .csv
   */
  private void createFile(String fileName) {
    try {
      String fileCompletePath = this.filePath + fileName + ".csv";
      FileWriter fileWriter = new FileWriter(fileCompletePath, true);

      this.writer = new CSVWriter(fileWriter);
    } catch (IOException ioe) {
      logger.severe(ioe.getMessage());
    }
  }

  /**
   * Escreve e salva os dados no arquivo .csv.
   *
   * @param data String[] - Dados que serão gravados no arquivo.
   */
  public void writeData(String[] data) {
    try {
      this.writer.writeNext(data);
      this.writer.flush();
    } catch (IOException ioe) {
      logger.severe(ioe.getMessage());
    }
  }

  /**
   * Fecha o arquivo .csv.
   */
  public void closeFile() {
    try {
      this.writer.close();
    } catch (IOException ioe) {
      logger.severe(ioe.getMessage());
    }
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }
}
