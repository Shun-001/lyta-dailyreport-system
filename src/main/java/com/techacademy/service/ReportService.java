package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    //private final PasswordEncoder passwordEncoder;

    @Autowired
    public ReportService(ReportRepository reportRepository, PasswordEncoder passwordEncoder) {
        this.reportRepository = reportRepository;
        //this.passwordEncoder = passwordEncoder;
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Report report) {
/*
        // 従業員番号重複チェック
        if (findByCode(employee.getCode()) != null) {
            return ErrorKinds.DUPLICATE_ERROR;
        }
*/
        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 従業員削除
    @Transactional
    public ErrorKinds delete(int id) {

        Report report = findById(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }


    // 従業員更新
    @Transactional
    public ErrorKinds updateDetail(Report report) {
/*
        // パスワード空白を判断
        if ("".equals(employee.getPassword())) {
            System.out.println("パスワードが空");
            // データベースのパスワードをパスワードに代入
            employee.setPassword(findByCode(employee.getCode()).getPassword());
        } else {
            ErrorKinds result = employeePasswordCheck(employee);
            if (ErrorKinds.CHECK_OK != result) {
                return result;
            }
        }
*/
        report.setDeleteFlg(false);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setCreatedAt(findById(report.getId()).getCreatedAt());
        report.setEmployee_code(report.getEmployee_code());

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }


    // 従業員一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // 1件を検索
    public Report findById(int id) {
        // findByIdで検索
        String strId = String.valueOf(id);
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

/*
    // 従業員パスワードチェック
    private ErrorKinds employeePasswordCheck(Employee employee) {

        // 従業員パスワードの半角英数字チェック処理
        if (isHalfSizeCheckError(employee)) {

            return ErrorKinds.HALFSIZE_ERROR;
        }

        // 従業員パスワードの8文字～16文字チェック処理
        if (isOutOfRangePassword(employee)) {

            return ErrorKinds.RANGECHECK_ERROR;
        }

        employee.setPassword(passwordEncoder.encode(employee.getPassword()));

        return ErrorKinds.CHECK_OK;
    }

    // 従業員パスワードの半角英数字チェック処理
    private boolean isHalfSizeCheckError(Employee employee) {

        // 半角英数字チェック
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
        Matcher matcher = pattern.matcher(employee.getPassword());
        return !matcher.matches();
    }

    // 従業員パスワードの8文字～16文字チェック処理
    public boolean isOutOfRangePassword(Employee employee) {

        // 桁数チェック
        int passwordLength = employee.getPassword().length();
        return passwordLength < 8 || 16 < passwordLength;
    }
*/

}
