package com.techacademy.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity
@Table(name = "projects")
@SQLRestriction("delete_flg = false")
public class Project {

    /**
     * Id(主キー)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * プロジェクトコード
     */
    @Length(min = 5, max = 5, message = "5桁で入力してください")
    @Pattern(regexp = "^[0-9]+$")
    private String projectCode;

    /**
     * プロジェクト名
     */
    @NotEmpty()
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
     * 日付の前後チェック
     * @return
     */
    @AssertTrue(message = "開始日は終了日以前の日付を入力してください")
    public boolean isDateValid() {
        if (termFrom.isBefore(termTo)) {
            return true;
        }
        return false;
    }

    /**
     * 削除フラグ
     */
    @Column(columnDefinition="TINYINT", nullable = false)
    private boolean deleteFlg;

    /**
     * 登録日時
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * 更新日時
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * メンバーリスト
     */
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Employee> members;

}