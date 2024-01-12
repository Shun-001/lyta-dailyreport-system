package com.techacademy.entity;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "projects")
@SQLRestriction("delete_flg = false")
public class Project {

    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * プロジェクトコード
     */
    @NotEmpty
    @Length(min = 5, max = 5)
    @Column(nullable = false)
    private Integer projectCode;

    /**
     * プロジェクト名
     */
    @NotEmpty
    @Length(max = 50)
    @Column(nullable = false)
    private String projectName;

    /**
     *  期間(from)
     */
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate termFrom;

    /**
     *  期間(to)
     */
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate termTo;

    /**
     * メンバー
     */
    @ManyToOne
    @JoinColumn(name = "employee_code", referencedColumnName = "code"
        , columnDefinition="varchar(10) not null")
    private List<Employee> members;

    /**
     * 削除フラグ
     */
    @Column(columnDefinition="TINYINT", nullable = false)
    private boolean deleteFlg;

}