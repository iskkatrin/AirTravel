import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

class FlightBuilder {
    static List<Flight> createFlights() {
        // Создаем дату, которая наступит через три дня от текущего момента
        LocalDateTime threeDaysFromNow = LocalDateTime.now().plusDays(3);
        return Arrays.asList(
                // Обычный перелет с продолжительностью в два часа
                createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2)),
                // Обычный перелет с несколькими сегментами
                createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2),
                        threeDaysFromNow.plusHours(3), threeDaysFromNow.plusHours(5)),
                // Перелет с вылетом в прошлом
                createFlight(threeDaysFromNow.minusDays(6), threeDaysFromNow),
                // Перелет, который вылетает до его прилета
                createFlight(threeDaysFromNow, threeDaysFromNow.minusHours(6)),
                // Перелет с временем на земле более двух часов
                createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2),
                        threeDaysFromNow.plusHours(5), threeDaysFromNow.plusHours(6)),
                // Еще один перелет с временем на земле более двух часов
                createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2),
                        threeDaysFromNow.plusHours(3), threeDaysFromNow.plusHours(4),
                        threeDaysFromNow.plusHours(6), threeDaysFromNow.plusHours(7)));
    }

    private static Flight createFlight(final LocalDateTime... dates) {
        // Проверка на корректность количества переданных дат (должно быть четным)
        if ((dates.length % 2) != 0) {
            throw new IllegalArgumentException("you must pass an even number of dates");
        }
        List<Segment> segments = new ArrayList<>(dates.length / 2);
        // Создание сегментов из пар дат (вылет-прилет)
        for (int i = 0; i < (dates.length - 1); i += 2) {
            segments.add(new Segment(dates[i], dates[i + 1]));
        }
        return new Flight(segments);
    }
}

// Класс, представляющий перелет
class Flight {
    private final List<Segment> segments;

    Flight(final List<Segment> segs) {
        segments = segs;
    }

    List<Segment> getSegments() {
        return segments;
    }

    @Override
    public String toString() {
        return segments.stream().map(Object::toString)
                .collect(Collectors.joining(" "));
    }
}

// Класс, представляющий сегмент перелета
class Segment {
    private final LocalDateTime departureDate;
    private final LocalDateTime arrivalDate;

    Segment(final LocalDateTime dep, final LocalDateTime arr) {
        departureDate = Objects.requireNonNull(dep);
        arrivalDate = Objects.requireNonNull(arr);
    }

    LocalDateTime getDepartureDate() {
        return departureDate;
    }

    LocalDateTime getArrivalDate() {
        return arrivalDate;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        return '[' + departureDate.format(fmt) + '|' + arrivalDate.format(fmt) + ']';
    }
}

// Интерфейс для фильтрации перелетов
interface FlightFilter {
    boolean test(Flight flight);
}

// Класс для фильтрации перелетов по различным правилам
class FlightProcessor {
    private final List<FlightFilter> filters = new ArrayList<>();

    // Добавление нового фильтра
    void addFilter(FlightFilter filter) {
        filters.add(filter);
    }

    // Применение всех фильтров к списку перелетов
    List<Flight> filterFlights(List<Flight> flights) {
        return flights.stream()
                .filter(flight -> filters.stream().allMatch(filter -> filter.test(flight)))
                .collect(Collectors.toList());
    }
}

// Фильтр для исключения перелетов с вылетом до текущего момента времени
class DepartureBeforeNowFilter implements FlightFilter {
    @Override
    public boolean test(Flight flight) {
        LocalDateTime now = LocalDateTime.now();
        return flight.getSegments().stream().noneMatch(segment -> segment.getDepartureDate().isBefore(now));
    }
}

// Фильтр для исключения сегментов с датой прилета раньше даты вылета
class ArrivalBeforeDepartureFilter implements FlightFilter {
    @Override
    public boolean test(Flight flight) {
        return flight.getSegments().stream().noneMatch(segment -> segment.getArrivalDate().isBefore(segment.getDepartureDate()));
    }
}

// Фильтр для исключения перелетов с общим временем на земле, превышающим два часа
class GroundTimeExceedsTwoHoursFilter implements FlightFilter {
    @Override
    public boolean test(Flight flight) {
        List<Segment> segments = flight.getSegments();
        for (int i = 1; i < segments.size(); i++) {
            Segment previous = segments.get(i - 1);
            Segment current = segments.get(i);
            if (current.getDepartureDate().isAfter(previous.getArrivalDate().plusHours(2))) {
                return false;
            }
        }
        return true;
    }
}