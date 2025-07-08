package com.benefactory.hrtech.exception;

/**
 * Исключение, выбрасываемое при передаче некорректных значений денежных сумм.
 * 
 * Данное исключение возникает в следующих случаях:
 *   Передано значение null
 *   Передано отрицательное значение
 *   Передано значение в некорректном формате
 */
public class InvalidMoneyAmountException extends MoneyAmountException {

    /**
     * Конструктор с сообщением об ошибке.
     * 
     * @param message описание причины некорректности суммы
     */
    public InvalidMoneyAmountException(String message) {
        super(message);
    }

    /**
     * Конструктор с сообщением об ошибке и причиной исключения.
     * 
     * @param message описание причины некорректности суммы
     * @param cause   исключение, ставшее причиной данной ошибки
     */
    public InvalidMoneyAmountException(String message, Throwable cause) {
        super(message, cause);
    }
} 