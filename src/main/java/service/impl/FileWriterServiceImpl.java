package service.impl;

import org.apache.log4j.Logger;
import service.FileWriterService;

import java.io.FileWriter;
import java.io.IOException;

public class FileWriterServiceImpl implements FileWriterService {

    private final Logger logger = Logger.getLogger(FileWriterServiceImpl.class);

    @Override
    public void writeToFile(String path, String source) {
        try (FileWriter writer = new FileWriter(path, false)) {
            writer.write(source);
            writer.flush();
        } catch (IOException ex) {
            logger.error("Failed writing to file",ex);
        }
    }
}
