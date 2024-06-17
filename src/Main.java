import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Flight> flights = FlightBuilder.createFlights();

        FlightProcessor processor = new FlightProcessor();

        // Фильтрация по вылету до текущего момента времени
        processor.addFilter(new DepartureBeforeNowFilter());
        List<Flight> filteredByDepartureBeforeNow = processor.filterFlights(flights);
        System.out.println("Flights with departure before now:");
        filteredByDepartureBeforeNow.forEach(System.out::println);

        // Фильтрация по сегментам с датой прилета раньше даты вылета
        processor = new FlightProcessor(); // новый процессор для нового набора фильтров
        processor.addFilter(new ArrivalBeforeDepartureFilter());
        List<Flight> filteredByArrivalBeforeDeparture = processor.filterFlights(flights);
        System.out.println("Flights with segments where arrival is before departure:");
        filteredByArrivalBeforeDeparture.forEach(System.out::println);

        // Фильтрация по общему времени на земле, превышающему два часа
        processor = new FlightProcessor(); // новый процессор для нового набора фильтров
        processor.addFilter(new GroundTimeExceedsTwoHoursFilter());
        List<Flight> filteredByGroundTimeExceedsTwoHours = processor.filterFlights(flights);
        System.out.println("Flights with ground time exceeding two hours:");
        filteredByGroundTimeExceedsTwoHours.forEach(System.out::println);
    }

    private static void runTests() {
        System.out.println("\nЗапуск тестов...");

        List<Flight> flights = FlightBuilder.createFlights();

        // Тест фильтра DepartureBeforeNowFilter
        FlightProcessor processor = new FlightProcessor();
        processor.addFilter(new DepartureBeforeNowFilter());
        List<Flight> result = processor.filterFlights(flights);
        assert result.size() == 5 : "Тест не пройден: DepartureBeforeNowFilter. Ожидалось 5, получено " + result.size();
        System.out.println("Тест DepartureBeforeNowFilter пройден.");

        // Тест фильтра ArrivalBeforeDepartureFilter
        processor = new FlightProcessor();
        processor.addFilter(new ArrivalBeforeDepartureFilter());
        result = processor.filterFlights(flights);
        assert result.size() == 5 : "Тест не пройден: ArrivalBeforeDepartureFilter. Ожидалось 5, получено " + result.size();
        System.out.println("Тест ArrivalBeforeDepartureFilter пройден.");

        // Тест фильтра GroundTimeExceedsTwoHoursFilter
        processor = new FlightProcessor();
        processor.addFilter(new GroundTimeExceedsTwoHoursFilter());
        result = processor.filterFlights(flights);
        assert result.size() == 4 : "Тест не пройден: GroundTimeExceedsTwoHoursFilter. Ожидалось 4, получено " + result.size();
        System.out.println("Тест GroundTimeExceedsTwoHoursFilter пройден.");

        // Тест с применением нескольких фильтров
        processor = new FlightProcessor();
        processor.addFilter(new DepartureBeforeNowFilter());
        processor.addFilter(new ArrivalBeforeDepartureFilter());
        processor.addFilter(new GroundTimeExceedsTwoHoursFilter());
        result = processor.filterFlights(flights);
        assert result.size() == 3 : "Тест не пройден: Несколько фильтров. Ожидалось 3, получено " + result.size();
        System.out.println("Тест с несколькими фильтрами пройден.");

    }
}