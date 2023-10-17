package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.service.interfaces.InfoInterface;

@Service
public class InfoService implements InfoInterface {
    @Value("${server.port}")
    private String port;

    @Override
    public String getPort() {
        return port;
    }
}