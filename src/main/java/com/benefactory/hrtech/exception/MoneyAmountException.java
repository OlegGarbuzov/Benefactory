package com.benefactory.hrtech.exception;

/**
 * Базовое исключение для ошибок, связанных с обработкой денежных сумм.
 * 
 * <p>Данное исключение является родительским для всех специфических исключений,
 * возникающих при работе с денежными суммами в прописном виде.</p>

 */
public class MoneyAmountException extends RuntimeException {

    public MoneyAmountException(String message) {
        super(message);
    }

    public MoneyAmountException(String message, Throwable cause) {
        super(message, cause);
    }
} 