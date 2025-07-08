package com.benefactory.hrtech;

import com.benefactory.hrtech.exception.InvalidMoneyAmountException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Утилитный класс для преобразования денежных сумм в прописное написание на русском языке.
 * Класс поддерживает преобразование сумм в диапазоне от 0 до 99,999.99 рублей
 * с автоматическим склонением валютных единиц.
 */
public class MoneyToWords {

    /**
     * Массив мужского рода от 0 до 19.
     * Используется для склонения рублей и единиц в составе тысяч.
     * Первый элемент намеренно "", т.к. ноль не склоняется и обрабатывается отдельно
     */
    private static final String[] UNITS_MALE = {
            "", "один", "два", "три", "четыре", "пять",
            "шесть", "семь", "восемь", "девять", "десять",
            "одиннадцать", "двенадцать", "тринадцать", "четырнадцать",
            "пятнадцать", "шестнадцать", "семнадцать", "восемнадцать", "девятнадцать"
    };

    /**
     * Массив женского рода от 0 до 19.
     * Используется для склонения тысяч ("одна тысяча", "две тысячи").
     * Первый элемент намеренно "", т.к. ноль не склоняется и обрабатывается отдельно
     */
    private static final String[] UNITS_FEMALE = {
            "", "одна", "две", "три", "четыре", "пять",
            "шесть", "семь", "восемь", "девять", "десять",
            "одиннадцать", "двенадцать", "тринадцать", "четырнадцать",
            "пятнадцать", "шестнадцать", "семнадцать", "восемнадцать", "девятнадцать"
    };

    /**
     * Массив названий десятков от 20 до 90.
     * Индекс 0 и 1 не используются (для чисел 0-19 используется массив UNITS).
     */
    private static final String[] TENS = {
            "", "", "двадцать", "тридцать", "сорок",
            "пятьдесят", "шестьдесят", "семьдесят", "восемьдесят", "девяносто"
    };

    /**
     * Массив названий сотен от 100 до 900.
     * Индекс 0 соответствует отсутствию сотен.
     */
    private static final String[] HUNDREDS = {
            "", "сто", "двести", "триста", "четыреста",
            "пятьсот", "шестьсот", "семьсот", "восемьсот", "девятьсот"
    };

    /**
     * Максимальная поддерживаемая сумма для преобразования.
     */
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("99999.99");

    /**
     * Определяет правильную форму для числительного в зависимости от числа.

     * @param number число для анализа
     * @param one    форма для единственного числа (1)
     * @param few    форма для чисел 2-4
     * @param many   форма для чисел 5-20, 25-30 и т.д.
     * @return подходящая словоформа
     */
    private static String getWordForm(int number, String one, String few, String many) {
        // Анализируем последние две цифры для определения правильного склонения
        int lastTwoDigits = number % 100;
        int lastDigit = number % 10;
        
        // Особый случай: числа от 11 до 19 всегда используют форму "many"
        if (lastTwoDigits >= 11 && lastTwoDigits <= 19) {
            return many;
        }
        
        // Обычные правила склонения по последней цифре
        if (lastDigit == 1) {
            return one;      // 1, 21, 31, ... → "рубль"
        }
        if (lastDigit >= 2 && lastDigit <= 4) {
            return few;      // 2-4, 22-24, 32-34, ... → "рубля"
        }
        
        return many;         // 0, 5-20, 25-30, ... → "рублей"
    }

    /**
     * Нормализует денежную сумму к стандартному формату с двумя знаками после запятой.
     * 
     * <p>Применяет округление вниз (RoundingMode.DOWN) для корректного отсечения
     * лишних копеек без искажения исходной суммы.</p>
     * 
     * @param amount исходная сумма для нормализации
     * @return нормализованная сумма с двумя знаками после запятой
     */
    private static BigDecimal normalizeAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.DOWN);
    }

    /**
     * Преобразует денежную сумму в прописное написание на русском языке.
     * Алгоритм работы:
     *   Валидация входной суммы (не более 99,999.99)
     *   Разделение на рубли и копейки
     *   Обработка тысяч (с женским родом)
     *   Обработка сотен, десятков и единиц рублей
     *   Склонение слова "рубль"
     *   Добавление копеек и склонение слова "копейка"
     * 
     * @param amount сумма для преобразования (не может быть null)
     * @return строковое представление суммы прописью
     * @throws InvalidMoneyAmountException если amount равно null или отрицательно
     * @throws MoneyAmountTooLargeException если сумма превышает максимально допустимую
     */
    public static String convertAmountToWords(BigDecimal amount) {
        // Валидация входных данных
        if (amount == null) {
            throw new InvalidMoneyAmountException("Сумма не может быть null");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidMoneyAmountException("Сумма не может быть отрицательной");
        }
        
        if (amount.compareTo(MAX_AMOUNT) > 0) {
            throw new MoneyAmountTooLargeException(amount, MAX_AMOUNT);
        }

        // Приводим к формату с двумя знаками после запятой (рубли.копейки)
        amount = normalizeAmount(amount);
        
        // Разделяем на рубли и копейки
        int rubles = amount.intValue(); // Целая часть - рубли
        int kopecks = amount.remainder(BigDecimal.ONE) // Дробная часть
                           .movePointRight(2)          // Умножаем на 100
                           .intValue();                // Получаем копейки

        // Формируем результирующую строку
        StringBuilder result = new StringBuilder();

        // Шаг 5: Обрабатываем тысячи (если есть)
        int thousands = rubles / 1000;
        int remainderAfterThousands = rubles % 1000;

        if (thousands > 0) {
            // Преобразуем тысячи в слова (используем женский род для "тысяча")
            result.append(convertTripleToWords(thousands, true)).append(" ");
            // Добавляем правильную форму слова "тысяча"
            result.append(getWordForm(thousands, "тысяча", "тысячи", "тысяч")).append(" ");
        }

        // Шаг 6: Обрабатываем оставшиеся рубли (сотни, десятки, единицы)
        String rublesWords = convertTripleToWords(remainderAfterThousands, false);
        if (!rublesWords.isEmpty()) {
            result.append(rublesWords).append(" ");
        } else if (rubles == 0) {
            // Специальный случай: если рублей совсем нет, добавляем "ноль"
            result.append("ноль").append(" ");
        }

        // Шаг 7: Добавляем правильную форму слова "рубль"
        result.append(getWordForm(rubles, "рубль", "рубля", "рублей")).append(" ");

        // Шаг 8: Добавляем копейки (всегда две цифры с ведущим нулем)
        result.append(String.format("%02d", kopecks)).append(" ");
        
        // Шаг 9: Добавляем правильную форму слова "копейка"
        result.append(getWordForm(kopecks, "копейка", "копейки", "копеек"));

        return result.toString().trim();
    }

    /**
     * Преобразует трехзначное число (0-999) в прописное написание.
     * 
     * <p>Обрабатывает сотни, десятки и единицы с учетом рода числительных 
     * (мужской род для рублей, женский род для тысяч).</p>
     * 
     * @param number число от 0 до 999
     * @param useFemaleForm true для использования женского рода числительных (тысячи),
     *                      false для мужского рода (рубли)
     * @return строковое представление числа прописью (может быть пустой строкой для 0)
     */
    private static String convertTripleToWords(int number, boolean useFemaleForm) {
        // Выбираем подходящий массив числительных в зависимости от рода
        String[] units = useFemaleForm ? UNITS_FEMALE : UNITS_MALE;

        // Разбиваем число на составляющие
        int hundredsDigit = number / 100;        // Разряд сотен (0-9)
        int tensAndUnits = number % 100;         // Последние две цифры (0-99)

        StringBuilder result = new StringBuilder();

        // Обрабатываем сотни
        if (hundredsDigit > 0) {
            result.append(HUNDREDS[hundredsDigit]).append(" ");
        }

        // Обрабатываем десятки и единицы
        if (tensAndUnits < 20) {
            // Числа от 0 до 19 берем напрямую из массива
            if (tensAndUnits > 0) {
                result.append(units[tensAndUnits]);
            }
        } else {
            // Числа от 20 до 99 разбиваем на десятки и единицы
            int tensDigit = tensAndUnits / 10;   // Разряд десятков (2-9)
            int unitsDigit = tensAndUnits % 10;  // Разряд единиц (0-9)
            
            result.append(TENS[tensDigit]);
            
            if (unitsDigit > 0) {
                result.append(" ").append(units[unitsDigit]);
            }
        }

        return result.toString().trim();
    }
}