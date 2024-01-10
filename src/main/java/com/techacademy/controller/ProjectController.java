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
import com.techacademy.entity.Report;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.ProjectService;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("projects")
public class ProjectController {

    private final ProjectService projectService;
    private final EmployeeService employeeService;

    @Autowired
    public ProjectController(ProjectService projectService, EmployeeService employeeService) {
        this.projectService = projectService;
        this.employeeService = employeeService;
    }

    /**
     * 日報一覧画面
     */
    @GetMapping
    public String list(@AuthenticationPrincipal UserDetail userdetail, Model model) {

        if (userdetail.getEmployee().getRole().toString().equals("ADMIN")) {
            model.addAttribute("listSize", projectService.findAll().size());
            model.addAttribute("projectList", projectService.findAll());
        } else {
            model.addAttribute("listSize", projectService.findByEmployeeCode(userdetail.getEmployee().getCode()).size());
            model.addAttribute("projectList", projectService.findByEmployeeCode(userdetail.getEmployee().getCode()));
        }

        return "projects/list";
    }

    /**
     * 日報詳細画面
     */
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable Integer id, Model model) {

        model.addAttribute("projects", projectService.findById(id));
        return "projects/detail";
    }

    /**
     * 日報新規登録画面
     */
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report, Model model) {

        // AuthenticationPrincipalアノテーション無しで取得
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Principalからログインユーザの情報を取得
        String userName = auth.getName();
        // employee_codeをここで代入
        report.setEmployee_code(userName);
        // ログインユーザー名を取得
        String loginUserName = employeeService.findByCode(userName).getName();
        model.addAttribute("loginUserName", loginUserName);

        return "projects/new";
    }

    /**
     * 日報新規登録処理
     */
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res, Model model) {

        List<Report> reportList = projectService.findByEmployeeCode(report.getEmployee_code());

        for(Report s : reportList){
            if(s.getReport_date().equals(report.getReport_date())) {
                model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DATECHECK_ERROR),
                        ErrorMessage.getErrorValue(ErrorKinds.DATECHECK_ERROR));

                return create(report, model);
            }

        }

        // 入力チェック
        if (res.hasErrors()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userName = auth.getName();
            report.setEmployee_code(userName);
            String loginUserName = employeeService.findByCode(userName).getName();
            model.addAttribute("loginUserName", loginUserName);

            return create(report, model);
        }


        // 論理削除を行った従業員番号を指定すると例外となるためtry~catchで対応
        // (findByIdでは削除フラグがTRUEのデータが取得出来ないため)
        try {
            ErrorKinds result = projectService.save(report);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return create(report, model);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return create(report, model);
        }

        return "redirect:/projects";
    }

    /**
     * 日報削除処理
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
        model.addAttribute("employeeName", projectService.findById(id).getEmployee().getName());

        return "projects/update";
    }

    /**
     * 日報更新処理
     */
    @PostMapping(value = "/{id}/update")
    public String update(@Validated Report report, BindingResult res, Model model) {

        // 同じ日付の日報がないかチェック
        List<Report> reportList = projectService.findByEmployeeCode(report.getEmployee_code());

        reportList.remove(projectService.findById(report.getId()));
        for(Report s : reportList){
            if(s.getReport_date().equals(report.getReport_date())) {
                model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DATECHECK_ERROR),
                        ErrorMessage.getErrorValue(ErrorKinds.DATECHECK_ERROR));

                return edit(report.getId(), model);
            }

        }

        // 入力チェック
        if (res.hasErrors()) {

            return "projects/update";
        }


        // 論理削除を行った従業員番号を指定すると例外となるためtry~catchで対応
        // (findByIdでは削除フラグがTRUEのデータが取得出来ないため)
        try {
            ErrorKinds result = projectService.updateReport(report);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return edit(report.getId(), model);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return edit(report.getId(), model);
        }

        return "redirect:/projects";

    }

}
