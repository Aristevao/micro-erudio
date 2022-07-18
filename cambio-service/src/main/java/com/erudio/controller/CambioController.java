package com.erudio.controller;

import com.erudio.model.Cambio;
import com.erudio.repository.CambioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

import static java.math.RoundingMode.CEILING;

@RequiredArgsConstructor
@RestController
@RequestMapping("cambio-service")
public class CambioController {

    private final Environment environment;
    private final CambioRepository repository;

    @GetMapping("{amount}/{from}/{to}")
    public Cambio getCambio(@PathVariable("amount") BigDecimal amount,
                            @PathVariable("from") String from,
                            @PathVariable("to") String to) {

        var cambio = repository.findByFromAndTo(from, to);
        if (cambio == null) throw new RuntimeException("Unsupported cambio");

        var port = environment.getProperty("local.server.port");
        BigDecimal conversionFactor = cambio.getConversionFactor();
        BigDecimal convertedValue = conversionFactor.multiply(amount);
        cambio.setConvertedValue(convertedValue.setScale(2, CEILING));
        cambio.setEnvironment(port);
        return cambio;
    }
}
