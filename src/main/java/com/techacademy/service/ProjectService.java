package com.techacademy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Project;
import com.techacademy.repository.EmployeeRepository;
import com.techacademy.repository.ProjectRepository;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    //private final PasswordEncoder passwordEncoder;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, EmployeeRepository employeeRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * プロジェクト保存
     * @param project
     * @return
     */
    @Transactional
    public ErrorKinds save(Project project) {

        project.setDeleteFlg(false);

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
        //report.setEmployee_code(report.getEmployee_code());

        projectRepository.save(project);
        return ErrorKinds.SUCCESS;
    }


    /**
     * 日報一覧表示処理
     * @return
     */
    public List<Project> findAll() {
        return projectRepository.findAll();
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
