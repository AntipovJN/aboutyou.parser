package factory;

import service.AboutYouParserService;
import service.impl.AboutYouParserServiceImpl;

import java.util.Objects;

public class AboutYouParserServiceFactory {

    private static AboutYouParserService aboutYouParserService;

    public static AboutYouParserService getInstance(){
        if(Objects.isNull(aboutYouParserService)){
            aboutYouParserService = new AboutYouParserServiceImpl();
        }
        return aboutYouParserService;
    }
}
