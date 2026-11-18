package br.com.carteiraclara.api;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.carteiraclara.application.ConsultarCarteira;
import br.com.carteiraclara.application.RegistrarPosicao;
import jakarta.validation.Valid;

/**
 * Endereços HTTP das posições. Só traduz HTTP/JSON para os casos de uso:
 * as regras ficam no domínio e na application.
 */
@RestController
@RequestMapping("/api/posicoes")
public class PosicaoController {

    private final RegistrarPosicao registrarPosicao;
    private final ConsultarCarteira consultarCarteira;

    public PosicaoController(RegistrarPosicao registrarPosicao, ConsultarCarteira consultarCarteira) {
        this.registrarPosicao = registrarPosicao;
        this.consultarCarteira = consultarCarteira;
    }

    /** POST /api/posicoes — registra (ou substitui) a posição de um ativo. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PosicaoResponse registrar(@Valid @RequestBody RegistrarPosicaoRequest pedido) {
        var posicao = registrarPosicao.executar(pedido.simbolo(), pedido.quantidade(), pedido.precoMedio());
        return PosicaoResponse.de(posicao);
    }

    /** GET /api/posicoes?BTC=350000&ETH=18000 — resumo da carteira com os preços informados. */
    @GetMapping
    public ResumoCarteiraResponse consultar(@RequestParam Map<String, String> precos) {
        var resumo = consultarCarteira.executar(converter(precos));
        return ResumoCarteiraResponse.de(resumo);
    }

    /** Os parâmetros da URL chegam como texto; aqui viram número. */
    private static Map<String, BigDecimal> converter(Map<String, String> precos) {
        var convertidos = new HashMap<String, BigDecimal>();
        precos.forEach((simbolo, texto) -> {
            try {
                convertidos.put(simbolo, new BigDecimal(texto.trim()));
            } catch (NumberFormatException erro) {
                throw new IllegalArgumentException("Preço inválido para " + simbolo + ": " + texto);
            }
        });
        return convertidos;
    }
}