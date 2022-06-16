package backend.test.tutorial.javastreamsql;

import backend.test.dbmsreplication.inmemorysql.InMemorySQL;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import backend.test.tutorial.javastreamsql.models.Employee;


public class Main {
	public static void main(String[] args) throws Exception {
		// You can uncomment each of those exercise below :
		// ex1_a();
		// ex1_b();
		// ex2();
		// ex3_a();
		// ex3_b();
		// ex4();
		// ex5();
		// ex6();
		// ex7_a();
		// ex7_b();
		// ex8_a();
		// ex8_b();
		// ex9_a();
		// ex9_b();
	}

	/**
	 * Ex1: WHERE + ORDER BY + LIMIT with Stream version
	 *
	 * Top 3 richest men
	 *
	 */
	public static void ex1_a () {
		List<Employee> result = employees().stream()
			.filter(employee -> "M".equals(employee.getGender()))	
			.sorted((e1, e2) -> e2.getSalary() - e1.getSalary())	
			.limit(3)												
			.collect(Collectors.toList());

		// Print
		System.out.println("Salary | Name");
		result.forEach(employee ->
			System.out.println(
				String.format("%s   | %s", employee.getSalary(), employee.getName())
			)
		);
	}

	/**
	 * Ex1: WITHOUT Stream version
	 */
	public static void ex1_b () {
		List<Employee> temp = new ArrayList<>();
		for (Employee employee : employees()) {
			if ("M".equals(employee.getGender())) { // WHERE gender = 'M'
				temp.add(employee);
			}
		}

		Collections.sort(temp, (e1, e2) -> e2.getSalary() - e1.getSalary()); // ORDER BY salary DESC
		int limit = (temp.size() > 0 && temp.size() <= 3) ? temp.size() : 3;
		List<Employee> result = (
			temp.isEmpty()) ? Collections.emptyList() : temp.subList(0, limit); // LIMIT X

		// Print
		System.out.println("Salary | Name");
		result.forEach(employee ->
			System.out.println(
				String.format("%s   | %s", employee.getSalary(), employee.getName())
			)
		);
	}

	/**
	 * Ex2: ORDER BY multiple fields
	 *
	 *		SELECT *
	 *		FROM employees
	 *		ORDER BY gender ASC, salary ASC, name ASC
	 */
	public static void ex2 () {
		List<Employee> result = employees().stream()
			.sorted(
				Comparator.comparing(Employee::getGender)	// ORDER BY gender ASC
					.thenComparing(Employee::getSalary)		// , salary ASC
					.thenComparing(Employee::getName)		// , name ASC
			)
			.collect(Collectors.toList());

		// Print
		System.out.println("Gender | Salary | Name");
		result.forEach(employee ->
				System.out.println(String.format("%s      | %s   | %s", employee.getGender(), employee.getSalary(), employee.getName()))
		);
	}

	/**
	* Contoh3: Fungsi agregat: `MIN` + `MAX` + `AVG` + `COUNT` + `SUM`
	*
	* `MIN`: Usia termuda
	* PILIH MIN (usia) SEBAGAI usia termuda DARI karyawan;
	*
	* `MAX`: Gaji tertinggi
	* PILIH MAX(gaji) SEBAGAI Gaji tertinggi DARI karyawan;
	*
	* `AVG`: Usia rata-rata karyawan
	* PILIH AVG(usia) SEBAGAI usia rata-rata DARI karyawan;
	*
	* `COUNT`: Jumlah wanita
	* SELECT COUNT(*) AS womenCount DARI karyawan WHERE gender = 'F';
	*
	* `SUM`: Jumlah gaji dengan pajak 21,7%
	* PILIH SUM(gaji) * 1,217 SEBAGAI salarySumWithTaxes DARI karyawan;
	*/

	public static void ex3_a () {
		Optional<Integer> youngestAge = employees().stream()
			.map(Employee::getAge)
			.min(Integer::compare);		
		Optional<Integer> highestSalary =  employees().stream()
			.map(Employee::getSalary)
			.max(Integer::compare);		

		OptionalDouble averageAge = employees().stream()
			.mapToInt(Employee::getAge)
			.average();				

		long womenCount = employees().stream()
			.filter(employee -> "F".equals(employee.getGender()))	
			.count();												

		double salarySumWithTaxes = employees().stream()
			.mapToDouble(Employee::getSalary)
			.sum() * 1.217;						
		// Print
		System.out.println("WITHOUT REDUCE()");
		System.out.println(String.format("Youngest Age                  : %s", youngestAge.orElse(null)));
		System.out.println(String.format("Highest salary                : %s", highestSalary.orElse(null)));
		System.out.println(String.format("Average age of employees      : %s", averageAge.orElse(0)));
		System.out.println(String.format("Number of women               : %s", womenCount));
		System.out.println(String.format("Sum of salary with 21.7%% taxes: %s", salarySumWithTaxes));
	}

	/**
	 * Ex3: with reduce() syntax
	 */
	public static void ex3_b () {
		Optional<Integer> youngestAge = employees().stream()
			.map(Employee::getAge)
			.reduce((a, b) -> (a < b) ? a : b);		

		Optional<Integer> highestSalary =  employees().stream()
			.map(Employee::getSalary)
			.reduce((a, b) -> (a > b) ? a : b);		

		// AVG => SUM() / COUNT(*)
		AtomicInteger count = new AtomicInteger(1);
		OptionalDouble sumAge = employees().stream()
			.mapToDouble(Employee::getAge)
			.reduce((a, b) -> {
				count.incrementAndGet();
				return a + b;
			});
		OptionalDouble averageAge = sumAge.isPresent() ?
			OptionalDouble.of(sumAge.getAsDouble() / count.get()) : OptionalDouble.empty();

		long womenCount = employees().stream()
			.filter(employee -> "F".equals(employee.getGender()))	
			.map(e -> 1L)
			.reduce(0L, (a, b) -> a + b);			

		double salarySumWithTaxes = employees().stream()
			.map(Employee::getSalary)
		// Print
		System.out.println("WITH REDUCE()");
		System.out.println(String.format("Youngest Age                  : %s", youngestAge.orElse(null)));
		System.out.println(String.format("Highest salary                : %s", highestSalary.orElse(null)));
		System.out.println(String.format("Average age of employees      : %s", averageAge));
		System.out.println(String.format("Number of women               : %s", womenCount));
		System.out.println(String.format("Sum of salary with 21.7%% taxes: %s", salarySumWithTaxes));
	}

	/**
	 * Ex4: MAX + GROUP BY
	 * Richest man and richest woman
	 */
	public static void ex4 () {
		Map<String, Optional<Integer>> result = employees().stream()
			.collect(Collectors.groupingBy(
				Employee::getGender,														
				Collectors.mapping(Employee::getSalary, Collectors.maxBy(Integer::compare))	
			));

		// Print
		result.entrySet().forEach(entry ->
			System.out.println(String.format("Gender: %s | Max Salary: %s", entry.getKey(), entry.getValue()))
		);
	}

	/**
	 * Ex5: AVG + GROUP BY
	 * Average salary of men and women
	 */
	public static void ex5 () {
		Map<String, Double> result = employees().stream()
			.collect(Collectors.groupingBy(
				Employee::getGender,							
				Collectors.averagingDouble(Employee::getSalary)	
			));

		// Print
		result.entrySet().forEach(entry ->
			System.out.println(String.format("Gender: %s | Average Salary: %s", entry.getKey(), entry.getValue()))
		);
	}

	/**
	 * Ex6: COUNT + GROUP BY
	 * Number of men and women
	 *		GROUP BY gender;
	 */
	public static void ex6 () {
		Map<String, Long> result = employees().stream()
			.collect(Collectors.groupingBy(
				Employee::getGender,		// GROUP BY gender
				Collectors.counting()		// COUNT(*)
			));

		// Print
		result.entrySet().forEach(entry ->
			System.out.println(String.format("Gender: %s | Count: %s", entry.getKey(), entry.getValue()))
		);
	}

	/**
	 * Ex7 (A): DISTINCT and GROUP BY
	 */
	public static void ex7_a () {
		Map<Integer, List<Employee>> genders2 = employees().stream()
			.collect(Collectors.groupingBy(Employee::getSalary));		// GROUP BY

		// Print
		System.out.println(String.format("Genders: %s", genders2.entrySet().stream().map(Map.Entry::getKey).map(Object::toString).collect(Collectors.joining(", "))));
	}

	/**
	 * Ex7 (B): DISTINCT and GROUP BY
	 */
	public static void ex7_b () {
		List<Integer> genders1 = employees().stream()
			.map(Employee::getSalary)
			.distinct()													// DISTINCT
			.collect(Collectors.toList());

		// Print
		System.out.println(String.format("Genders: %s", genders1.stream().map(Object::toString).collect(Collectors.joining(", "))));
	}

	/**
	 * Ex8: CROSS JOIN
	 */
	public static void ex8_a () {
		String[] t1 = {"A", "B", "C"};
		String[] t2 = {"B", "C", "D"};
		String[] t3 = {"C", "F", "E"};

		// CROSS JOIN
		List<Container> list = Stream.of(t1)
			.flatMap(x -> Arrays.stream(t2)			
				.flatMap(y -> Arrays.stream(t3)		
					.map(z -> new Container(x, y, z))
				)
			)
			.collect(Collectors.toList());

		// Print
		list.forEach(System.out::println);
	}

	/**
	 * Ex8: with InMemorySQL
	 */
	public static void ex8_b () throws SQLException {
		List<Tuple> t1 = tupleInit("A", "B", "C");
		List<Tuple> t2 = tupleInit("B", "C", "D");
		List<Tuple> t3 = tupleInit("C", "D", "E");

		String request = "SELECT t1.e AS x, t2.e AS y, t3.e AS z FROM t1 CROSS JOIN t2 CROSS JOIN t3";
		new InMemorySQL()
			.add(Tuple.class, t1)
			.add(Tuple.class, t2)
			.add(Tuple.class, t3)
			.executeQuery(Container.class, request)
			.forEach(System.out::println);
	}

	/**
	 * Ex9: INNER JOIN
	 */
	public static void ex9_a () {
		String[] t1 = {"A", "B", "C"};
		String[] t2 = {"B", "C", "D"};
		String[] t3 = {"C", "F", "E"};

		// CROSS JOIN
		List<Container> list = Stream.of(t1)
			.flatMap(x -> Arrays.stream(t2)				// t1 INNER JOIN t2
				.filter(y -> Objects.equals(x, y))		// ON t1.e = t2.e
				.flatMap(y -> Arrays.stream(t3)			// t2 INNER JOIN t3
					.filter(z -> Objects.equals(y, z))	// ON t2.e = t3.e
					.map(z -> new Container(x, y, z))
				)
			).collect(Collectors.toList());

		// Print
		list.forEach(System.out::println);
	}

	/**
	 * Ex9: with InMemorySQL
	 */
	public static void ex9_b () throws SQLException {
		List<Tuple> t1 = tupleInit("A", "B", "C");
		List<Tuple> t2 = tupleInit("B", "C", "D");
		List<Tuple> t3 = tupleInit("C", "D", "E");

		String request = "SELECT t1.e AS x, t2.e AS y, t3.e AS z " +
				"FROM t1 " +
				"INNER JOIN t2 " +
				"ON t1.e = t2.e " +
				"INNER JOIN t3 " +
				"ON t2.e = t3.e";
		new InMemorySQL()
			.add(Tuple.class, t1)
			.add(Tuple.class, t2)
			.add(Tuple.class, t3)
			.executeQuery(Container.class, request)
			.forEach(System.out::println);
	}

	public static List<Employee> employees() {
		return new ArrayList<Employee>(){{
			add(Employee.builder().gender("M").age(40).salary(9800).name("jade").build());
			add(Employee.builder().gender("M").age(33).salary(1500).name("BKENT").build());
			add(Employee.builder().gender("F").age(65).salary(6000).name("musk duck").build());
			add(Employee.builder().gender("F").age(24).salary(2500).name("hellokity").build());
		}};
	}

	public static class Tuple {
		String e;

		public Tuple(String e) {
			this.e = e;
		}
	}

	public static List<Tuple> tupleInit(String... values) {
		List<Tuple> t = new ArrayList<>();
		for (String v : values) {
			t.add(new Tuple(v));
		}
		return t;
	}

	public static class Container<T1, T2, T3> {
		public T1 x;
		public T2 y;
		public T3 z;

		public Container() {
		}

		public Container(T1 x, T2 y, T3 z){
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public String toString() {
			return x + " " + y + " " + z;
		}
	}
}
