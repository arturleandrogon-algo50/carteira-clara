package br.com.carteiraclara.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint simples para confirmar que a aplicação está de pé.
 * Também deixa explícito o aviso de que o sistema é informativo.
 */
@RestController
@RequestMapping("/api")
public class StatusController {

    static final String AVISO =
            "Ferramenta informativa e educativa. Não é recomendação de investimento.";

    public record StatusResponse(String aplicacao, String situacao, String aviso) {}

    @GetMapping("/status")
    public StatusResponse status() {
        return new StatusResponse("carteira-clara", "no ar", AVISO);
    }
}
