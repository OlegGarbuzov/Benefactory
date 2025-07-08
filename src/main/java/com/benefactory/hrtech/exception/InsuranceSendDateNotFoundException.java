package com.benefactory.hrtech.exception;

/**
 * Исключение, выбрасываемое когда не удается найти подходящую дату отправки данных в страховую компанию.
 * 
 *  Данное исключение может возникнуть в следующих случаях:
 *  Все возможные даты отправки в заданном периоде уже прошли
 *  Ошибка в конфигурации дней отправки<
 *  Системная ошибка в расчете дат
 */
public class InsuranceSendDateNotFoundException extends RuntimeException {

    public InsuranceSendDateNotFoundException(String message) {
        super(message);
    }

    public InsuranceSendDateNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 