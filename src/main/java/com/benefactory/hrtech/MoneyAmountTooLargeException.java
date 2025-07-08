package com.benefactory.hrtech;

import com.benefactory.hrtech.exception.MoneyAmountException;

import java.math.BigDecimal;

/**
 * Исключение, выбрасываемое когда денежная сумма превышает максимально допустимое значение.
 * 
 * Данное исключение возникает при попытке преобразовать в прописной вид
 * сумму, которая превышает установленные ограничения системы.
 * 
 */
public class MoneyAmountTooLargeException extends MoneyAmountException {

    private final BigDecimal attemptedAmount;
    private final BigDecimal maxAllowedAmount;

    /**
     * Конструктор с информацией о превышении лимита.
     * 
     * @param attemptedAmount   сумма, которую пытались преобразовать
     * @param maxAllowedAmount  максимально допустимая сумма
     */
    public MoneyAmountTooLargeException(BigDecimal attemptedAmount, BigDecimal maxAllowedAmount) {
        super(String.format("Сумма %s превышает максимально допустимую %s", 
              attemptedAmount, maxAllowedAmount));
        this.attemptedAmount = attemptedAmount;
        this.maxAllowedAmount = maxAllowedAmount;
    }

    /**
     * Конструктор с кастомным сообщением.
     * 
     * @param message           пользовательское сообщение об ошибке
     * @param attemptedAmount   сумма, которую пытались преобразовать
     * @param maxAllowedAmount  максимально допустимая сумма
     */
    public MoneyAmountTooLargeException(String message, BigDecimal attemptedAmount, BigDecimal maxAllowedAmount) {
        super(message);
        this.attemptedAmount = attemptedAmount;
        this.maxAllowedAmount = maxAllowedAmount;
    }

    /**
     * Получить сумму, которую пытались преобразовать.
     * 
     * @return переданная сумма
     */
    public BigDecimal getAttemptedAmount() {
        return attemptedAmount;
    }

    /**
     * Получить максимально допустимую сумму.
     * 
     * @return максимальная сумма
     */
    public BigDecimal getMaxAllowedAmount() {
        return maxAllowedAmount;
    }
} 