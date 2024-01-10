package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Report;
import com.techacademy.repository.EmployeeRepository;
import com.techacademy.repository.ReportRepository;

@Service
public class ProjectService {

    private final ReportRepository reportRepository;
    //private final PasswordEncoder passwordEncoder;

    @Autowired
    public ProjectService(ReportRepository reportRepository, EmployeeRepository employeeRepository) {
        this.reportRepository = reportRepository;
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Report report) {

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


    // 日報更新
    @Transactional
    public ErrorKinds updateReport(Report report) {



        report.setDeleteFlg(false);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setCreatedAt(findById(report.getId()).getCreatedAt());
        report.setEmployee_code(report.getEmployee_code());

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }


    // 日報一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // 1件を検索
    public Report findById(int id) {
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

    public List<Report> findByEmployeeCode(String code) {
        return reportRepository.findByEmployee_code(code);
    }

}
