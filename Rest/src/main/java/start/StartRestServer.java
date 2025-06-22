package start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("services")
@ComponentScan("models")
@ComponentScan("persistance")
@SpringBootApplication
public class StartRestServer{
    public static void main(String [] args){
        SpringApplication.run(StartRestServer.class, args);
    }
}

