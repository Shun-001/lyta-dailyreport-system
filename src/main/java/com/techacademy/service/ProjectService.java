package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Project;
import com.techacademy.repository.EmployeeRepository;
import com.techacademy.repository.ProjectRepository;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    //private final PasswordEncoder passwordEncoder;
    private final EmployeeService employeeService;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, EmployeeRepository employeeRepository, EmployeeService employeeService) {
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
    }

    /**
     * プロジェクト保存
     * @param project
     * @return
     */
    @Transactional
    public ErrorKinds save(Project project, String members) {

        List<String> memberIdList = new ArrayList<>();
        // employeeのprojectフィールドにprojectを格納
        if(Objects.nonNull(members)) {
            memberIdList = Arrays.asList(members.split(","));
            for(String id : memberIdList) {
                Employee emp = employeeService.findByCode(id);
                emp.setProject(project);
            }
        }

        project.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        project.setCreatedAt(now);
        project.setUpdatedAt(now);

        projectRepository.save(project);
        return ErrorKinds.SUCCESS;
    }

    /**
     * プロジェクト削除
     * @param id
     * @return
     */
    @Transactional
    public ErrorKinds delete(int id) {

        Project project = findById(id);
        LocalDateTime now = LocalDateTime.now();
        project.setUpdatedAt(now);
        project.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }


    /**
     * プロジェクト更新
     * @param project
     * @return
     */
    @Transactional
    public ErrorKinds updateProject(Project project) {

        project.setDeleteFlg(false);
        LocalDateTime now = LocalDateTime.now();
        project.setUpdatedAt(now);
        project.setCreatedAt(findById(project.getId()).getCreatedAt());

        projectRepository.save(project);
        return ErrorKinds.SUCCESS;
    }


    /**
     * プロジェクト一覧表示処理
     * @return
     */
    public List<Project> findAll() {
        return projectRepository.findAll();
    }


    public List<Employee> findByProjectId(Integer projectId) {

        List<Employee> allEmployees = employeeRepository.findAll();
        List<Employee> members = new ArrayList<Employee>();
        for(Employee employee : allEmployees) {
            // getProjectがnullになる
            if(Objects.nonNull(employee.getProject()))  {
                if(employee.getProject().getId().equals(projectId)) {
                    members.add(employee);
                }
            }
        }
        return members;

    }

    /**
     * プロジェクトをidで検索
     * @param id
     * @return
     */
    public Project findById(int id) {
        Optional<Project> option = projectRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Project project = option.orElse(null);
        return project;
    }

}
