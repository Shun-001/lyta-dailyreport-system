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
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;
    private final EmployeeService employeeService;

    @Autowired
    public ReportController(ReportService reportService, EmployeeService employeeService) {
        this.reportService = reportService;
        this.employeeService = employeeService;
    }

    /**
     * 日報一覧画面
     * @param userdetail
     * @param model
     * @return
     */
    @GetMapping
    public String list(@AuthenticationPrincipal UserDetail userdetail, Model model) {

        if (userdetail.getEmployee().getRole().toString().equals("ADMIN")) {
            model.addAttribute("listSize", reportService.findAll().size());
            model.addAttribute("reportList", reportService.findAll());
        } else {
            model.addAttribute("listSize", reportService.findByEmployeeCode(userdetail.getEmployee().getCode()).size());
            model.addAttribute("reportList", reportService.findByEmployeeCode(userdetail.getEmployee().getCode()));
        }

        return "reports/list";
    }

    /**
     * 日報詳細画面
     * @param id
     * @param model
     * @return
     */
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable Integer id, Model model) {

        model.addAttribute("report", reportService.findById(id));
        return "reports/detail";
    }

    /**
     * 日報検索画面
     * @return
     */
    @GetMapping(value = "/search")
    public String search() {

        return "reports/search";
    }

    // 日報検索処理
    @PostMapping(value = "/search")
    public String showSearchResult(@AuthenticationPrincipal UserDetail userdetail, Model model) {

        // 本当は検索条件に合うものだけをリストに格納して表示したい
        if (userdetail.getEmployee().getRole().toString().equals("ADMIN")) {
            model.addAttribute("listSize", reportService.findAll().size());
            model.addAttribute("reportList", reportService.findAll());
        } else {
            model.addAttribute("listSize", reportService.findByEmployeeCode(userdetail.getEmployee().getCode()).size());
            model.addAttribute("reportList", reportService.findByEmployeeCode(userdetail.getEmployee().getCode()));
        }


        return "/reports/list";
    }


    /** 日報新規登録画面 */
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

        return "reports/new";
    }

    /** 日報新規登録処理 */
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res, Model model) {

        List<Report> reportList = reportService.findByEmployeeCode(report.getEmployee_code());

        for(Report s : reportList){
            if(s.getReport_date().equals(report.getReport_date())) {
                model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DATECHECK_ERROR),
                        ErrorMessage.getErrorValue(ErrorKinds.DATECHECK_ERROR));

              //return "reports/update";
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

            //return "reports/new";
            return create(report, model);
        }


        // 論理削除を行った従業員番号を指定すると例外となるためtry~catchで対応
        // (findByIdでは削除フラグがTRUEのデータが取得出来ないため)
        try {
            ErrorKinds result = reportService.save(report);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return create(report, model);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return create(report, model);
        }

        return "redirect:/reports";
    }

    /** 日報削除処理 */
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable Integer id, Model model) {

        ErrorKinds result = reportService.delete(id);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("report", reportService.findById(id));
            return detail(id, model);
        }

        return "redirect:/reports";
    }


    /** 日報更新画面 */
    @GetMapping(value = "/{id}/update")
    public String edit(@PathVariable int id, Model model) {
        model.addAttribute("report", reportService.findById(id));
        model.addAttribute("employeeName", reportService.findById(id).getEmployee().getName());

        return "reports/update";
    }

    /** 日報更新処理 */
    @PostMapping(value = "/{id}/update")
    public String update(@Validated Report report, BindingResult res, Model model) {

        // 同じ日付の日報がないかチェック
        List<Report> reportList = reportService.findByEmployeeCode(report.getEmployee_code());

        reportList.remove(reportService.findById(report.getId()));
        for(Report s : reportList){
            if(s.getReport_date().equals(report.getReport_date())) {
                model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DATECHECK_ERROR),
                        ErrorMessage.getErrorValue(ErrorKinds.DATECHECK_ERROR));

                //return "reports/update";
                return edit(report.getId(), model);
            }

        }

        // 入力チェック
        if (res.hasErrors()) {

            return "reports/update";
            //return edit(report.getId(), model);
        }


        // 論理削除を行った従業員番号を指定すると例外となるためtry~catchで対応
        // (findByIdでは削除フラグがTRUEのデータが取得出来ないため)
        try {
            ErrorKinds result = reportService.updateReport(report);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return edit(report.getId(), model);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return edit(report.getId(), model);
        }

        return "redirect:/reports";

    }

}
