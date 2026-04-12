package org.example.medicinalplant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.example.medicinalplant.mapper")
public class MedicinalPlantApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicinalPlantApplication.class, args);
    }

}
