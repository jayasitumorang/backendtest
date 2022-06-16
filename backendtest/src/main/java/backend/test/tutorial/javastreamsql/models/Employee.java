package backend.test.tutorial.javastreamsql.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Employee {
	private String gender;
	private int age;
	private int salary;
	private String name;
}
