package com.techacademy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Project;
import com.techacademy.entity.Report;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.ProjectService;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("projects")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService, ReportService reportService, EmployeeService employeeService) {
        this.projectService = projectService;
    }

    /**
     * プロジェクト一覧画面
     * @param userdetail
     * @param model
     * @return
     */
    @GetMapping
    public String list(@AuthenticationPrincipal UserDetail userdetail, Model model) {
/*
        if (userdetail.getEmployee().getRole().toString().equals("ADMIN")) {
            model.addAttribute("listSize", projectService.findAll().size());
            model.addAttribute("projectList", projectService.findAll());
        } else {
            model.addAttribute("listSize", projectService.findByEmployeeCode(userdetail.getEmployee().getCode()).size());
            model.addAttribute("projectList", projectService.findByEmployeeCode(userdetail.getEmployee().getCode()));
        }
*/
        return "projects/list";
    }

    /**
     * プロジェクト詳細画面
     * @param id
     * @param model
     * @return
     */
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable Integer id, Model model) {

        model.addAttribute("projects", projectService.findById(id));
        return "projects/detail";
    }

    /**
     * プロジェクト新規登録画面
     * @param report
     * @param model
     * @return
     */
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Project project, Model model) {
/*
        // AuthenticationPrincipalアノテーション無しで取得
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Principalからログインユーザの情報を取得
        String userName = auth.getName();
        // employee_codeをここで代入
        report.setEmployee_code(userName);
        // ログインユーザー名を取得
        String loginUserName = employeeService.findByCode(userName).getName();
        model.addAttribute("loginUserName", loginUserName);
*/
        return "projects/new";
    }

    /**
     * プロジェクト新規登録処理
     * @param project
     * @param res
     * @param model
     * @return
     */
    @PostMapping(value = "/add")
    public String add(@Validated Project project, BindingResult res, Model model) {

        List<Project> projectList = projectService.findAll();

        for(Project s : projectList){
            if(s.getProjectCode().equals(project.getProjectCode())) {
                model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DATECHECK_ERROR),
                        ErrorMessage.getErrorValue(ErrorKinds.DATECHECK_ERROR));

                return create(project, model);
            }

        }

        // 入力チェック
        if (res.hasErrors()) {
                return create(project, model);
        }


        // 論理削除を行った従業員番号を指定すると例外となるためtry~catchで対応
        // (findByIdでは削除フラグがTRUEのデータが取得出来ないため)
        try {
            ErrorKinds result = projectService.save(project);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return create(project, model);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return create(project, model);
        }

        return "redirect:/projects";
    }

    /**
     * プロジェクト削除処理
     * @param id
     * @param model
     * @return
     */
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable Integer id, Model model) {

        ErrorKinds result = projectService.delete(id);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("project", projectService.findById(id));
            return detail(id, model);
        }

        return "redirect:/projects";
    }


    /**
     * 日報更新画面
     */
    @GetMapping(value = "/{id}/update")
    public String edit(@PathVariable int id, Model model) {
        model.addAttribute("project", projectService.findById(id));
        //model.addAttribute("employeeName", projectService.findById(id).getEmployee().getName());

        return "projects/update";
    }

    /**
     * 日報更新処理
     */
    @PostMapping(value = "/{id}/update")
    public String update(@Validated Project project, BindingResult res, Model model) {

/*
        // 同じ日付の日報がないかチェック
        List<Report> reportList = projectService.findByEmployeeCode(project.getEmployee_code());

        reportList.remove(projectService.findById(project.getId()));
        for(Report s : reportList){
            if(s.getReport_date().equals(project.getReport_date())) {
                model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DATECHECK_ERROR),
                        ErrorMessage.getErrorValue(ErrorKinds.DATECHECK_ERROR));

                return edit(project.getId(), model);
            }

        }
*/
        // 入力チェック
        if (res.hasErrors()) {

            return "projects/update";
        }


        // 論理削除を行った従業員番号を指定すると例外となるためtry~catchで対応
        // (findByIdでは削除フラグがTRUEのデータが取得出来ないため)
        try {
            ErrorKinds result = projectService.updateProject(project);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return edit(project.getId(), model);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return edit(project.getId(), model);
        }

        return "redirect:/projects";

    }

}
