package com.techacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techacademy.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Integer> {

}
