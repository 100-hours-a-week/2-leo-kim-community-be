package org.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication// 👈 컨트롤러 패키지를 직접 스캔!
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}