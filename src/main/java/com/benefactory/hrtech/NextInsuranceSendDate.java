package com.benefactory.hrtech;

import com.benefactory.hrtech.exception.InsuranceSendDateNotFoundException;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
Написать функцию определяющую дату отправки списка в страховую:
отправка осуществляется 1, 10 и 20 числа каждого месяца в 18:00.
Если дата отправки попадает на рабочий/праздничный день - то отправка осуществляется в предыдущий рабочий день.
дата запроса == текущему времени
можно использовать функцию:
public getVacCheck(java.sql.Date modDate); проверяет дату, является ли она рабочей.
Если выходной- возвращает ближайший рабочий день. Возвращает переменную типа java.sql.Date
 */

/**
 * Утилитный класс для определения следующей даты отправки данных в страховую компанию.
 *  Класс реализует бизнес-логику расчета даты отправки согласно следующим правилам:
 *  Отправка осуществляется 1, 10 и 20 числа каждого месяца в 18:00
 *  Если дата отправки попадает на выходной/праздничный день,
 *  то отправка переносится на предыдущий рабочий день
 *  Поиск осуществляется от текущего времени в ближайшие 4 месяца
 * 
 * Примечание: Т.к. поведение предоставленного метода getVacCheck вызывает сомнения
 * (не указано, возвращает ли он предыдущий или следующий рабочий день),
 * принято решение реализовать собственную логику определения рабочих дней
 * с четким соблюдением требования "предыдущий рабочий день".
 */
public class NextInsuranceSendDate {

    /**
     * Дни месяца, в которые производится отправка данных в страховую.
     */
    private static final List<Integer> SEND_DAYS = Arrays.asList(1, 10, 20);
    
    /**
     * Время(час) отправки данных в страховую.
     */
    private static final int SEND_HOUR = 18;
    
    /**
     * Время(минуты) отправки данных в страховую.
     */
    private static final int SEND_MINUTE = 0;
    
    /**
     * Количество месяцев вперед для поиска ближайшей даты отправки.
     * Используется для гарантии нахождения подходящей даты.
     */
    private static final int MONTHS_TO_SEARCH = 4;

    /**
     * Выходные дни недели.
     */
    private static final Set<DayOfWeek> WEEKENDS = Set.of(
        DayOfWeek.SATURDAY, 
        DayOfWeek.SUNDAY
    );

    /**
     * Фиксированные российские праздники на 2025 год.
     * В производственной среде праздники должны загружаться из конфигурации
     * или внешнего источника с возможностью обновления.
     */
    private static final Set<LocalDate> HOLIDAYS_2025 = new HashSet<>(Arrays.asList(
        // Новогодние каникулы
        LocalDate.of(2025, 1, 1),   // Новый год
        LocalDate.of(2025, 1, 2),   // Новогодние каникулы
        LocalDate.of(2025, 1, 3),   // Новогодние каникулы
        LocalDate.of(2025, 1, 6),   // Новогодние каникулы
        LocalDate.of(2025, 1, 7),   // Рождество Христово
        LocalDate.of(2025, 1, 8),   // Новогодние каникулы
        
        // День защитника Отечества (перенос с воскресенья)
        LocalDate.of(2025, 2, 24),  // День защитника Отечества (перенос)
        
        // Международный женский день
        LocalDate.of(2025, 3, 8),   // Международный женский день
        LocalDate.of(2025, 3, 10),  // Перенос с 8 марта (суббота)
        
        // Праздник Весны и Труда
        LocalDate.of(2025, 5, 1),   // Праздник Весны и Труда
        LocalDate.of(2025, 5, 2),   // Дополнительный выходной
        
        // День Победы
        LocalDate.of(2025, 5, 9),   // День Победы
        
        // День России
        LocalDate.of(2025, 6, 12),  // День России
        LocalDate.of(2025, 6, 13),  // Дополнительный выходной
        
        // День народного единства
        LocalDate.of(2025, 11, 4),  // День народного единства
        
        // Новогодние каникулы (конец года)
        LocalDate.of(2025, 12, 31)  // Новогодние каникулы
    ));

    /**
     * Определяет следующую дату отправки данных в страховую компанию.
     * Метод выполняет следующий алгоритм:
     *   Получает текущую дату и время
     *   Формирует список всех возможных дат отправки в ближайшие MONTHS_TO_SEARCH месяца
     *   Фильтрует даты, которые еще не наступили
     *   Выбирает ближайшую подходящую дату
     *   Проверяет, является ли эта дата рабочим днем
     *   Если нет - переносит на предыдущий рабочий день
     *   Возвращает результат с учетом времени SEND_HOUR / SEND_MINUTE
     * 
     * @param now текущая дата и время для расчета
     * @return следующая дата отправки данных в страховую
     * @throws InsuranceSendDateNotFoundException если не удалось найти подходящую дату
     */
    public static Timestamp getNextInsuranceSendDate(LocalDateTime now) {
        // **LocalDateTime now - вынесен в параметр на будущее, для возможности написания unit тестов**

        // Текущий год-месяц
        YearMonth currentYearMonth = YearMonth.from(now);
        
        // Ленивый поиск ближайшей подходящей даты
        // Т.к. логика умеренно сложна, то стримы не использую, что бы сохранить читаемость и возможность для отладки
        for (int monthOffset = 0; monthOffset < MONTHS_TO_SEARCH; monthOffset++) {
            YearMonth targetYearMonth = currentYearMonth.plusMonths(monthOffset);
            
            // Для каждого дня отправки (1, 10, 20) в текущем месяце
            for (Integer sendDay : SEND_DAYS) {
                /*
                 Учитываем случай, когда в месяце меньше дней (например, 31 февраля).
                 Ситуация гипотетическая, но лучше перестраховаться,
                 если вдруг в будущем кто-то добавит, например, 32 или 34 в SEND_DAYS
                 */
                LocalDate targetDate = targetYearMonth.atDay(
                    Math.min(sendDay, targetYearMonth.lengthOfMonth())
                );
                
                // Устанавливаем время отправки из констант
                LocalDateTime sendDateTime = targetDate.atTime(SEND_HOUR, SEND_MINUTE);
                
                // Проверяем, что это будущая дата
                if (!sendDateTime.isBefore(now)) {
                    // Проверяем, является ли найденная дата рабочим днем
                    Date candidateDate = Date.valueOf(sendDateTime.toLocalDate());
                    Date workingDate = getVacCheck(candidateDate);
                    
                    // Формируем финальную дату с учетом времени 18:00
                    LocalDateTime finalDateTime = workingDate.toLocalDate().atTime(SEND_HOUR, SEND_MINUTE);
                    
                    // Убедимся, что финальная дата все еще в будущем
                    // (getVacCheck может вернуть дату из предыдущего месяца)
                    // Почему isBefore, а не isAfter? -
                    // isAfter - это точно "будущее", исключает "настоящее", в случаях, елси дата отправки совпала
                    // ровно с текуйщей датой и временем, а мы хотим "захватить" и текущее время
                    if (!finalDateTime.isBefore(now)) {
                        return Timestamp.valueOf(finalDateTime);
                    }
                    // Иначе продолжаем поиск дальше
                }
            }
        }
        
        // Если ничего не найдено, выбрасываем исключение
        throw new InsuranceSendDateNotFoundException(
            "Не удалось найти дату отправки в ближайшие " + MONTHS_TO_SEARCH + " месяцев"
        );
    }

    /**
     * Проверяет, является ли указанная дата рабочим днем.
     * Если дата попадает на выходной или праздничный день, 
     * возвращает ближайший предыдущий рабочий день.
     * 
     * Реализация учитывает:
     * - Выходные дни (суббота, воскресенье)
     * - Официальные российские праздники
     * - Поиск именно предыдущего рабочего дня (не следующего)
     * 
     * @param date проверяемая дата
     * @return рабочий день (либо исходная дата, либо предыдущий рабочий день)
     */
    private static Date getVacCheck(Date date) {
        LocalDate localDate = date.toLocalDate();
        
        // Если дата уже рабочая, возвращаем как есть
        if (isWorkingDay(localDate)) {
            return date;
        }
        
        // Ищем предыдущий рабочий день
        int maxIterations = 10; // Защита от бесконечного цикла
        int iterations = 0;
        
        while (!isWorkingDay(localDate) && iterations < maxIterations) {
            localDate = localDate.minusDays(1);
            iterations++;
        }
        
        if (iterations >= maxIterations) {
            throw new IllegalStateException(
                "Не удалось найти рабочий день за " + maxIterations + " дней назад от " + date
            );
        }
        
        return Date.valueOf(localDate);
    }

    /**
     * Проверяет, является ли указанная дата рабочим днем.
     * 
     * @param date проверяемая дата
     * @return true, если дата является рабочим днем
     */
    private static boolean isWorkingDay(LocalDate date) {
        // Проверяем выходные дни
        if (WEEKENDS.contains(date.getDayOfWeek())) {
            return false;
        }
        
        // Проверяем праздники
        if (isHoliday(date)) {
            return false;
        }
        
        return true;
    }

    /**
     * Проверяет, является ли указанная дата праздником.
     * 
     * В текущей реализации поддерживается только 2025 год.
     * В производственной среде необходимо расширить поддержку
     * на другие годы и добавить загрузку из внешних источников.
     * 
     * @param date проверяемая дата
     * @return true, если дата является праздником
     */
    private static boolean isHoliday(LocalDate date) {
        // В данной реализации поддерживаем только 2025 год
        if (date.getYear() == 2025) {
            return HOLIDAYS_2025.contains(date);
        }
        
        // Для других годов считаем, что праздников нет
        // В производственной среде здесь должна быть логика
        // загрузки праздников для соответствующего года
        return false;
    }
}
