package factory;

import service.FileWriterService;
import service.impl.FileWriterServiceImpl;

import java.util.Objects;

public class FileWriterServiceFactory {

    private static FileWriterService fileWriteService;

    public static FileWriterService getInstance(){
        if(Objects.isNull(fileWriteService)){
            fileWriteService = new FileWriterServiceImpl();
        }
        return fileWriteService;
    }
}
