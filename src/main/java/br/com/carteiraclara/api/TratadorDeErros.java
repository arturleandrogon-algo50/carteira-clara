package br.com.carteiraclara.api;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Transforma erros de dados enviados pela pessoa em 400 Bad Request,
 * com uma mensagem clara, em vez de um erro 500 genérico.
 */
@RestControllerAdvice
public class TratadorDeErros {

    public record ErroResponse(String erro) {}

    /** Regras do domínio: ativo fora do catálogo, quantidade negativa, preço inválido... */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse argumentoInvalido(IllegalArgumentException erro) {
        return new ErroResponse(erro.getMessage());
    }

    /** Campos do JSON que não passaram nas anotações do RegistrarPosicaoRequest. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse campoInvalido(MethodArgumentNotValidException erro) {
        var mensagem = erro.getBindingResult().getFieldErrors().stream()
                .map(campo -> campo.getField() + ": " + campo.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return new ErroResponse(mensagem);
    }
}