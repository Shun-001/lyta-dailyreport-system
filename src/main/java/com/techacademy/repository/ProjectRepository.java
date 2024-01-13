package com.techacademy.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

public interface ProjectRepository extends JpaRepository<Report, Integer> {

    List<Employee> findByCode(String code);

}
